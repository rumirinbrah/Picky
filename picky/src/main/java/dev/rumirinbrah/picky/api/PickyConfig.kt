package dev.rumirinbrah.picky.api

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * Configuration for controlling the layout of the image grid inside the Picky picker.
 *
 * This defines the spacing between grid items and the number of columns displayed.
 * It can be used to customize the density and visual structure of the image grid
 * according to the host application's design requirements.
 *
 * @param horizontalSpacing Horizontal space between grid items.
 * @param verticalSpacing Vertical space between grid items.
 * @param gridCells Number of columns in the grid.
 * @sample sampleGridConfig
 */
@Stable
data class PickyGridConfig(
    val horizontalSpacing : Dp = 2.dp,
    val verticalSpacing : Dp = 2.dp,
    val gridCells : Int = 3
)
private fun sampleGridConfig(){
    PickyGridConfig(
        horizontalSpacing = 4.dp,
        verticalSpacing = 4.dp,
        gridCells = 4
    )
}