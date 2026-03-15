package dev.rumirinbrah.picky.api

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color


object PickyDefaults {

    /**
     * Creates a [PickyTabColors] instance used to style the **Recents | Albums**
     * tab row inside the Picky image picker.
     *
     * This allows customization of the container, tab content, selection state,
     * and indicator colors used by the tab component.
     *
     * By default, the colors are derived from the current [MaterialTheme]
     * so the picker automatically adapts to the host application's theme.
     *
     * @param containerColor Background color of the tab row container.
     * @param contentColor Default color applied to tab content.
     * @param selectedTabColor Color used for the currently selected tab.
     * @param unselectedTabColor Color used for the tab that is not selected.
     * @param tabIndicatorColor Color of the indicator shown beneath the selected tab.
     *
     * @return A [PickyTabColors] configuration object used by Picky components.
     *
     * Example:
     * ```
     * PickyDefaults.tabColors(
     *     containerColor = Color.Black,
     *     selectedTabColor = Color.White
     * )
     * ```
     */
    @Composable
    fun tabColors(
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainer ,
        contentColor: Color = MaterialTheme.colorScheme.onBackground,
        selectedTabColor: Color = MaterialTheme.colorScheme.onBackground,
        unselectedTabColor: Color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
        tabIndicatorColor: Color = MaterialTheme.colorScheme.primary
    ): PickyTabColors {
        return PickyTabColors(
            containerColor = containerColor ,
            contentColor =  contentColor,
            selectedTabColor = selectedTabColor ,
            unselectedTabColor = unselectedTabColor ,
            tabIndicatorColor = tabIndicatorColor
        )
    }
}

@Stable
class PickyTabColors(
    val containerColor: Color ,
    val contentColor: Color ,
    val selectedTabColor: Color ,
    val unselectedTabColor: Color ,
    val tabIndicatorColor: Color
)



