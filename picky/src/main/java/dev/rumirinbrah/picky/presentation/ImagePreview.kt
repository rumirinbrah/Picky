package dev.rumirinbrah.picky.presentation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.rumirinbrah.picky.media.ImagePickerAction

@Composable
internal fun ImagePreview(
    modifier: Modifier = Modifier,
    image : Uri,
    onAction : (ImagePickerAction)->Unit
) {
    val context = LocalContext.current
    BackHandler() {
        onAction(ImagePickerAction.ClearPreviewImage)
    }
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable {
                onAction(ImagePickerAction.ClearPreviewImage)
            }
    ) {
        Column(
            Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier.clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onBackground.copy(0.5f))
                    .clickable(
                        onClick = {
                            onAction(ImagePickerAction.ClearPreviewImage)
                        }
                    )
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close preview",
                    tint = MaterialTheme.colorScheme.background
                )
            }
            VerticalSpace()
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(image)
                    .crossfade(true)
                    .build() ,
                contentDescription = "Preview image" ,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}