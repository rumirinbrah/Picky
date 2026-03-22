package dev.rumirinbrah.picky.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.rumirinbrah.feature_picky.R
import dev.rumirinbrah.picky.api.PickySelectionColors
import dev.rumirinbrah.picky.media.GalleryImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ImageItem(
    modifier: Modifier = Modifier ,
    image : GalleryImage ,
    onClick : (GalleryImage)->Unit ,
    onLongClick : (GalleryImage)->Unit = {},
    enabled : Boolean = true ,
    selected : Boolean = false ,
    selectionColors: PickySelectionColors
) {
    val context = LocalContext.current
    val scale by animateFloatAsState(
        targetValue = if(selected){
            0.9f
        }else{
            1f
        }
    )

    Box(
        modifier
            .drawBehind{
                if(selected){
                    drawRect(
                        selectionColors.borderIndicatorColor
                    )
                }else{
                    return@drawBehind
                }
            }
//            .drawWithContent(
//                onDraw = {
//                    drawContent()
//                    if(selected){
//                        drawRect(
//                            selectedIndicatorColor ,
//                            style = Stroke(10f)
//                        )
//                    }else{
//                        return@drawWithContent
//                    }
//                }
//            )
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(image.image)
                .crossfade(true)
                .build() ,
            contentDescription = "image" ,
            modifier = Modifier
                .graphicsLayer{
                    scaleX = scale
                    scaleY = scale
                }
                .aspectRatio(1f)
                .combinedClickable(
                    enabled = enabled ,
                    onClick = {
                        onClick(image)
                    } ,
                    onLongClick = {
                        onLongClick(image)
                    }
                ),
            contentScale = ContentScale.Crop
        )
        if(selected){
            TickIndicator(
                Modifier.align(Alignment.Center),
                background = selectionColors.tickIconBackgroundColor,
                iconTint = selectionColors.tickIconColor
            )
        }
    }
}

@Composable
private fun TickIndicator(
    modifier: Modifier = Modifier,
    background : Color,
    iconTint : Color
) {
    Box(
        modifier
            .clip(CircleShape)
            .background(background)
            .padding(4.dp)
    ){
        Icon(
            painter = painterResource(R.drawable.check) ,
            contentDescription = "selected",
            modifier = Modifier.size(20.dp),
            tint = iconTint
        )
    }
}

