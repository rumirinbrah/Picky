package dev.rumirinbrah.picky.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import dev.rumirinbrah.picky.api.PickyGridConfig
import dev.rumirinbrah.picky.api.PickySelectionColors
import dev.rumirinbrah.picky.media.GalleryImage
import dev.rumirinbrah.picky.media.ImagePickerAction
import dev.rumirinbrah.picky.media.containsId
import kotlinx.coroutines.flow.debounce

@Composable
internal fun ImagesGrid(
    modifier: Modifier = Modifier ,
    images : List<GalleryImage> ,
    selectedImages : List<GalleryImage> ,
    onAction: (ImagePickerAction) -> Unit ,
    gridConfig: PickyGridConfig ,
    selectionColors : PickySelectionColors ,
    multiSelect : Boolean = false ,
) {
    val listState = rememberLazyGridState()
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo
        }.debounce(400)
            .collect { layoutInfo ->

                val total = layoutInfo.totalItemsCount
                val lastItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                //load more if we're near the END
                if (total >= 5 && lastItem >= total - 2) {
                    onAction(ImagePickerAction.LoadRecentsNextPage)
                }
            }
    }

    Column(

    ) {
        LazyVerticalGrid(
            modifier = modifier.fillMaxSize() ,
            columns = GridCells.Fixed(gridConfig.gridCells) ,
            verticalArrangement = Arrangement.spacedBy(gridConfig.verticalSpacing) ,
            horizontalArrangement = Arrangement.spacedBy(gridConfig.horizontalSpacing) ,
            state = listState
        ) {
            items(
                images ,
                key = {
                    it.id
                }
            ) { image ->
                ImageItem(
                    Modifier
                        .animateItem()
                        .weight(1f) ,
                    image = image ,
                    onClick = {
                        onAction(ImagePickerAction.SelectImage(it.image , it.id))
                    } ,
                    onLongClick = {
                        onAction(ImagePickerAction.PreviewImage(it.image))
                    },
                    selected = multiSelect && selectedImages.containsId(image.id) ,
                    selectionColors = selectionColors
                )
            }
            item {
                VerticalSpace()
            }
        }
    }
}