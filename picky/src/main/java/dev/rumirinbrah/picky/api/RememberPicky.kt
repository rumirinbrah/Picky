package dev.rumirinbrah.picky.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Used to hide/show image picker sheet
 *
 * Provides an instance of [PickyState]
 *
 * @author zyzz
*/
@Composable
fun rememberPickyImagePicker(

) : PickyState{
    return remember {
        PickyState()
    }
}