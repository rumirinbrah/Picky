package dev.rumirinbrah.picky.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rumirinbrah.feature_picky.R
import dev.rumirinbrah.picky.api.PickyGridConfig
import dev.rumirinbrah.picky.api.PickySelectionColors
import dev.rumirinbrah.picky.media.GalleryImage
import dev.rumirinbrah.picky.media.ImagePickerAction

/**
 * Recent images
 * @author zyzz
*/
@Composable
internal fun RecentImagesPage(
    modifier: Modifier = Modifier ,
    images: List<GalleryImage> ,
    selectedImages : List<GalleryImage>,
    onAction: (ImagePickerAction) -> Unit ,
    onDismiss: () -> Unit ,
    loading: Boolean = false ,
    multiSelect : Boolean = false,
    selectionColors: PickySelectionColors,
    gridConfig: PickyGridConfig
) {

    val context = LocalContext.current

    val listState = rememberLazyGridState()
    var showInfoText by remember { mutableStateOf(false) }

    BackHandler {
        onDismiss()
    }

//    LaunchedEffect(listState) {
//        snapshotFlow {
//            listState.layoutInfo
//        }.debounce(400)
//            .collect { layoutInfo ->
//
//                val total = layoutInfo.totalItemsCount
//                val lastItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
//                //load more if we're near the END
//                if (total >= 5 && lastItem >= total - 2) {
//                    onAction(ImagePickerAction.LoadRecentsNextPage)
//                }
//            }
//    }

    Column(
        modifier.fillMaxSize()
    ) {
        VerticalSpace(10.dp)

        ImagesGrid(
            Modifier,
            images = images,
            selectedImages = selectedImages,
            onAction = {
                onAction(it)
            },
            gridConfig = gridConfig,
            multiSelect = multiSelect,
            selectionColors = selectionColors
        )

//        LazyVerticalGrid(
//            modifier = Modifier.fillMaxWidth()
//                .weight(1f),
//            columns = GridCells.Fixed(gridCells) ,
//            state = listState ,
//            verticalArrangement = Arrangement.spacedBy(verticalGridPadding) ,
//            horizontalArrangement = Arrangement.spacedBy(horizontalGridPadding) ,
//        ) {
//            items(
//                images ,
//                key = {
//                    it.id
//                }
//            ) { image ->
//                ImageItem(
//                    Modifier
//                        .animateItem()
//                        .weight(1f),
//                    image,
//                    onClick = {
//                        onAction(ImagePickerAction.SelectImage(it.image , it.id))
//                    },
//                    selected = multiSelect && selectedImages.containsId(image.id),
//                    selectionColors = selectionColors
//                )
//            }
//            //some space at the bottom
//            item {
//                VerticalSpace()
//            }
//        }
        VerticalSpace(10.dp)
        // Maybe you haven't given us full access to your files yet.
        //Please
        Column(
            Modifier.fillMaxWidth()
                .animateContentSize()
        ){
            Box(
                Modifier.clip(CircleShape)
                    .clickable {
                        showInfoText = !showInfoText
                    }
                    .align(Alignment.CenterHorizontally)
            ){
                Icon(
                    painter = painterResource(R.drawable.outline_info_24) ,
                    contentDescription = "Extra information",
                    modifier = Modifier.size(20.dp)
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                showInfoText,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = buildAnnotatedString {
                        pushStyle(
                            style = SpanStyle(
                                fontSize = 12.sp ,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                            )
                        )
                        append("Can't find all your images here? Maybe you haven't given us full access to your files yet. ")
                        append("Go to app settings and change the 'Photos And Videos' permission from ")
                        withStyle(
                            style = SpanStyle(
                                fontSize = 12.sp ,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                                fontWeight = FontWeight.Bold
                            )
                        ){
                            append("'Allow Selected' to 'Allow All'")
                        }
                        pop()
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
        VerticalSpace(10.dp)
        when {
            loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}