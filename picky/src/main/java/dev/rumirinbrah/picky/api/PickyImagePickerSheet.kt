package dev.rumirinbrah.picky.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.rumirinbrah.picky.media.MediaManager

@Composable
fun PickyImagePickerSheet(
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val mediaManager = remember {
        MediaManager(scope,context)
    }
    val state by mediaManager.state.collectAsStateWithLifecycle()


}