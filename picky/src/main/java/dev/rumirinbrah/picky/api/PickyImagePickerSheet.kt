package dev.rumirinbrah.picky.api

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.rumirinbrah.picky.media.ImagePickerAction
import dev.rumirinbrah.picky.media.MediaManager
import dev.rumirinbrah.picky.permissions.PermissionManager
import dev.rumirinbrah.picky.presentation.PickyBottomSheet
import dev.rumirinbrah.picky.presentation.PickySheetState
import dev.rumirinbrah.picky.presentation.rememberPickySheetState
import dev.rumirinbrah.picky.util.checkStoragePermissions
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun PickyImagePickerSheet(
    modifier: Modifier = Modifier,
    pickyState: PickyState,
    initialSheetState : PickySheetState = PickySheetState.CLOSED,
    background: Color = MaterialTheme.colorScheme.surface ,
    sheetShape: Shape = RoundedCornerShape(topEnd = 40.dp , topStart = 40.dp)
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val pagerState = rememberPagerState { 2 }

    val mediaManager = remember {
        MediaManager(scope,context)
    }
    val state by mediaManager.state.collectAsStateWithLifecycle()

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


    //-------- TRIGGER OPS ON VISIBILITY --------
    LaunchedEffect(pickyState.pickerVisible) {

        if(!pickyState.pickerVisible){
            sheetState.animateTo(PickySheetState.CLOSED)
            mediaManager.onAction(ImagePickerAction.CancelSelection)
        }else{
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


    PickyBottomSheet(
        modifier,
        state = sheetState,
        onSheetClosed = {

        }
    ){
        Box(
            Modifier.fillMaxSize()
                .background(
                    background,
                    sheetShape
                )
        ){
            Column(
                Modifier
                    .fillMaxSize()
            ){

            }
        }
    }

}