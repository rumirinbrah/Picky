package dev.rumirinbrah.picky.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.rumirinbrah.picky.R
import dev.rumirinbrah.picky.media.GalleryImage
import dev.rumirinbrah.picky.media.ImagePickerAction
import kotlinx.coroutines.flow.debounce

/**
 * Recent images
 * @author zyzz
*/
@Composable
internal fun RecentImagesPage(
    modifier: Modifier = Modifier ,
    images: List<GalleryImage> ,
    onAction: (ImagePickerAction) -> Unit ,
    onDismiss: () -> Unit ,
    loading: Boolean = false ,
    gridCells: Int = 3 ,
    verticalGridPadding: Dp = 2.dp ,
    horizontalGridPadding: Dp = 2.dp ,
) {

    val context = LocalContext.current

    val listState = rememberLazyGridState()
    var showInfoText by remember { mutableStateOf(false) }

    BackHandler {
        onDismiss()
    }

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
        modifier.fillMaxSize()
    ) {
        VerticalSpace(10.dp)

        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth()
                .weight(1f),
            columns = GridCells.Fixed(gridCells) ,
            state = listState ,
            verticalArrangement = Arrangement.spacedBy(verticalGridPadding) ,
            horizontalArrangement = Arrangement.spacedBy(horizontalGridPadding) ,
        ) {
            items(
                images ,
                key = {
                    it.id
                }
            ) { image ->
                Box(
                    Modifier
                        .animateItem()
                        .weight(1f)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(image.image)
                            .crossfade(true)
                            .build() ,
                        contentDescription = "Image" ,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable {
                                onAction(ImagePickerAction.SelectImage(image.image))
                            } ,
                        contentScale = ContentScale.Crop
                    )
                }

            }
            //some space at the bottom
            item {
                VerticalSpace()
            }
            val a = Icons.Default.Info
        }
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
                    imageVector = Icons.Default.Info,
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