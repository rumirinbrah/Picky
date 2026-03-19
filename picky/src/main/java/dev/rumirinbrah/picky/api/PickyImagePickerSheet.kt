package dev.rumirinbrah.picky.api

import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.rumirinbrah.picky.media.ImagePickerAction
import dev.rumirinbrah.picky.media.MediaManager
import dev.rumirinbrah.picky.permissions.PermissionDialog
import dev.rumirinbrah.picky.permissions.PermissionManager
import dev.rumirinbrah.picky.permissions.openAppSettings
import dev.rumirinbrah.picky.presentation.AlbumsRoot
import dev.rumirinbrah.picky.presentation.ImagePreview
import dev.rumirinbrah.picky.presentation.MultiSelectActionsCard
import dev.rumirinbrah.picky.presentation.ObserveAsEvents
import dev.rumirinbrah.picky.presentation.PickerTabRow
import dev.rumirinbrah.picky.presentation.PickyBottomSheet
import dev.rumirinbrah.picky.presentation.PickySheetState
import dev.rumirinbrah.picky.presentation.RecentImagesPage
import dev.rumirinbrah.picky.presentation.VerticalSpace
import dev.rumirinbrah.picky.presentation.rememberPickySheetState
import dev.rumirinbrah.picky.util.MediaManagerEvents
import dev.rumirinbrah.picky.util.UIEvents
import dev.rumirinbrah.picky.util.checkStoragePermissions
import kotlinx.coroutines.launch


/**
 * A composable bottom sheet image picker that allows users to select images
 * from their device using a customizable grid and selection behavior.
 *
 * This is the main entry point for the Picky image picker. It supports both
 * single and multi-image selection through [PickyOption] and delivers results
 * via the [onResult] callback.
 *
 * The picker handles media querying, permissions, selection state, and UI rendering internally,
 * while allowing the host application to control state and appearance.
 *
 * @param modifier Modifier applied to the picker sheet container.
 * @param pickyState State holder for the picker. This should be remembered
 * and hoisted by the caller to retain picker state across recompositions.
 * @param onResult Callback invoked when the user completes a selection.
 * The type of result depends on the provided [option]:
 * - [PickyOption.PickSingle] → Uri
 * - [PickyOption.PickMultiple] → [List]<[Uri]>
 * @param option Defines the selection mode and behavior of the picker.
 * Determines the type of data returned in [onResult].
 * @param initialSheetState Initial visibility state of the picker sheet.
 * @param background Background color of the picker sheet.
 * @param tabColors Configuration for styling the "Recents | Albums" tab row.
 * @param selectionColors Configuration for styling selection indicators such
 * as borders and checkmarks.
 * @param gridConfig Configuration for the image grid layout including spacing
 * and number of columns.
 * @param sheetShape Shape of the picker sheet container.
 *
 * Example:
 * ```
 * PickyImagePickerSheet(
 *     pickyState = rememberPickyState(),
 *     option = PickyOption.PickSingle,
 *     onResult = { uri ->
 *         // handle selected image
 *     }
 * )
 * ```
 *
 * For multi-select:
 * ```
 * PickyImagePickerSheet(
 *     pickyState = rememberPickyState(),
 *     option = PickyOption.PickMultiple(maxItems = 5),
 *     onResult = { uris ->
 *         // handle selected images
 *     }
 * )
 * ```
 */
@Composable
fun <T> PickyImagePickerSheet(
    modifier: Modifier = Modifier ,
    pickyState: PickyState ,
    onResult: (result: T) -> Unit ,
    option: PickyOption<T> ,
    initialSheetState: PickySheetState = PickySheetState.CLOSED ,
    background: Color = MaterialTheme.colorScheme.surface ,
    tabColors: PickyTabColors = PickyDefaults.tabColors() ,
    selectionColors: PickySelectionColors = PickyDefaults.selectionColors() ,
    gridConfig: PickyGridConfig = PickyGridConfig() ,
    sheetShape: Shape = RoundedCornerShape(topEnd = 40.dp , topStart = 40.dp)
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val pagerState = rememberPagerState { 2 }

    //--------MEDIA--------
    val mediaManager = remember {
        MediaManager(scope , context)
    }
    val state by mediaManager.state.collectAsStateWithLifecycle()
    val mediaManagerEvents = mediaManager.events

    val sheetState = rememberPickySheetState(initialSheetState)


    //--------PERMS--------
    val permissionManager = remember {
        PermissionManager(scope)
    }
    val deniedPermsQueue = permissionManager.permissionQueue
    val permissionEvents = permissionManager.events
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        it.onEach { (permission , granted) ->
            permissionManager.onPermissionResult(permission , granted)
        }
    }

    //-------- SIDE EFFECTS --------

    //-------- SET PICKY OPTION --------
    LaunchedEffect(Unit) {
        mediaManager.onAction(ImagePickerAction.SetPickyOption(option))
    }

    //-------- TRIGGER OPS ON VISIBILITY --------
    LaunchedEffect(pickyState.pickerVisible) {

        //-------- CLOSE AND CLEANUP --------
        if (!pickyState.pickerVisible) {
            sheetState.animateTo(PickySheetState.CLOSED)
            mediaManager.onAction(ImagePickerAction.CancelSelection)
        } else {
            //-------- SHOW SHEET --------
            sheetState.animateTo(PickySheetState.HALF_EXPANDED)
            context.checkStoragePermissions(
                notGrantedBelowAndroid12 = {
                    permissionLauncher.launch(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    )
                } ,
                notGrantedAboveAndroid13 = {
                    permissionLauncher.launch(
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                    )
                } ,
                granted = {
                    mediaManager.onAction(ImagePickerAction.Load)
                }
            )
        }
    }

    //-------- CLEANUP --------
    DisposableEffect(Unit) {
        onDispose {
            mediaManager.onAction(ImagePickerAction.CancelSelection)
        }
    }

    //-------- PERMISSION EVENTS --------
    ObserveAsEvents(permissionEvents) { event ->
        when (event) {
            UIEvents.Success -> {
                mediaManager.onAction(ImagePickerAction.Load)
            }

            else -> Unit
        }
    }
    //-------- MEDIA M EVENTS --------
    ObserveAsEvents(mediaManagerEvents) { event ->
        when (event) {
            is MediaManagerEvents.OnImagesSelect -> {
                onResult(event.uri as T)
                pickyState.dismiss()
            }

            is MediaManagerEvents.OnImageSelect -> {
                onResult(event.uri as T)
                pickyState.dismiss()
            }

            is MediaManagerEvents.Error ->{
                Toast.makeText(context , event.errorMsg , Toast.LENGTH_SHORT).show()
            }
        }
    }

    PickyBottomSheet(
        modifier
            .fillMaxSize()
            .background(
                background ,
                sheetShape
            )
            .innerShadow(
                sheetShape ,
                shadow = Shadow(
                    radius = 3.dp ,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.1f) ,
                    spread = 1.dp ,
                    offset = DpOffset(x = 0.dp , y = 1.dp)
                )
            ) ,
        state = sheetState ,
        onSheetClosed = {
            pickyState.dismiss()
        } ,
        dismissTopContainer = true ,
        bottomActionBar = {
            if (state.multiSelect && state.selectedImages.isNotEmpty()) {
                MultiSelectActionsCard(
                    Modifier.padding(horizontal = 4.dp) ,
                    numImages = state.selectedImages.size.toString() ,
                    onDone = {
                        mediaManager.onAction(ImagePickerAction.ConfirmSelection)
                    } ,
                    onClear = {
                        mediaManager.onAction(ImagePickerAction.ClearSelection)
                    } ,
                    onDiscard = {
                        pickyState.dismiss()
                    }
                )
            }
        } ,
        containerOverlay = {
            //------- PREVIEW --------
            state.previewImage?.let {

                ImagePreview(
                    image = it,
                    onAction = {
                        mediaManager.onAction(it)
                    }
                )
            }
        }
    ) {
        Box(
            Modifier
        ) {
            Column(
                Modifier
                    .fillMaxSize()
//                    .animateContentSize()
            ) {
                //------- TAB ROW --------
                AnimatedVisibility(
                    state.tabRowVisible ,
                    enter = slideInVertically() ,
                    exit = fadeOut() + slideOutVertically()
                ) {
                    PickerTabRow(
                        currentTab = pagerState.currentPage ,
                        onTabChange = { newTab ->
                            scope.launch {
                                pagerState.animateScrollToPage(newTab)
                            }
                        } ,
                        colors = tabColors
                    )
                }

                HorizontalPager(
                    pagerState ,
                    modifier = Modifier.fillMaxWidth() ,
                    userScrollEnabled = state.tabRowVisible
                ) { page ->
                    when (page) {
                        0 -> {
                            RecentImagesPage(
                                images = state.images ,
                                selectedImages = state.selectedImages ,
                                loading = state.loading ,
                                onAction = { action ->
                                    mediaManager.onAction(action)
                                } ,
                                onDismiss = {
                                    pickyState.dismiss()
                                    mediaManager.onAction(ImagePickerAction.CancelSelection)
                                } ,
                                multiSelect = state.multiSelect ,
                                selectionColors = selectionColors ,
                                gridConfig = gridConfig
                            )
                        }

                        1 -> {
                            AlbumsRoot(
                                state = state ,
                                onAction = { action ->
                                    mediaManager.onAction(action)
                                } ,
                                onDismiss = {
                                    pickyState.dismiss()
                                    mediaManager.onAction(ImagePickerAction.CancelSelection)
                                } ,
                                selectionColors = selectionColors ,
                                gridConfig = gridConfig
                            )
                        }
                    }
                }
                if (state.multiSelect) {
                    VerticalSpace()
                }

            }

            //------- DENIED PERMISSIONS ALERT --------
            deniedPermsQueue.onEach { permission ->
                PermissionDialog(
                    permission = permission ,
                    isPermanentlyDeclined = !ActivityCompat.shouldShowRequestPermissionRationale(
                        context as ComponentActivity ,
                        permission
                    ) ,
                    onDismiss = {
                        permissionManager.onDismiss()
                    } ,
                    onOkClick = {
                        permissionManager.onDismiss()
                        permissionLauncher.launch(arrayOf(permission))
                    } ,
                    onGoToSettings = {
                        context.openAppSettings()
                        permissionManager.onDismiss()
                    }
                )
            }
        }
    }

}


//
//@Preview
//@Composable
//private fun ImagePickerPrev() {
//    Box(Modifier.fillMaxSize()) {
//        PickyImagePickerSheet(
//            pickyState = rememberPickyImagePicker() ,
//            onResult = {
//
//            } ,
//            option = PickyOption.PickMultiple()
//        )
//    }
//}


