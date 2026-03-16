package dev.rumirinbrah.picky.api

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
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
     * @param selectedTabColor Color used for the label of currently selected tab.
     * @param selectedTabContainerColor Color used for the container of selected tab
     * @param unselectedTabColor Color used for the label of tab that is not selected.
     * @param tabIndicatorColor Color of the indicator shown beneath the selected tab.
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
        selectedTabColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedTabContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
        unselectedTabColor: Color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
        tabIndicatorColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
    ): PickyTabColors {
        return PickyTabColors(
            containerColor = containerColor ,
            contentColor =  contentColor,
            selectedTabColor = selectedTabColor ,
            selectedTabContainerColor = selectedTabContainerColor,
            unselectedTabColor = unselectedTabColor ,
            tabIndicatorColor = tabIndicatorColor
        )
    }

    /**
     * Creates a [PickySelectionColors] configuration used to style the visual
     * indicators of selected images inside the Picky picker.
     *
     * This includes the checkmark icon, its background, and the border indicator
     * drawn around selected images. By default, the colors are derived from the
     * current [MaterialTheme] so the picker integrates naturally with the host
     * application's theme.
     *
     * @param tickIconColor Color of the selection checkmark icon displayed on
     * selected images.
     * @param tickIconBackgroundColor Background color behind the checkmark icon.
     * (This is drawn as a circular container)
     * @param borderIndicatorColor Color of the border drawn around selected images.
     *
     * @return A [PickySelectionColors] instance used to customize selection UI
     * elements within the picker.
     *
     * Example:
     * ```
     * PickyDefaults.selectionColors(
     *     tickIconColor = Color.White,
     *     tickIconBackgroundColor = Color.Black,
     *     borderIndicatorColor = Color.White
     * )
     * ```
     */
    @Composable
    fun selectionColors(
        tickIconColor : Color = MaterialTheme.colorScheme.background,
        tickIconBackgroundColor : Color = MaterialTheme.colorScheme.onBackground,
        borderIndicatorColor : Color = MaterialTheme.colorScheme.onBackground
    ) : PickySelectionColors{
        return PickySelectionColors(
            tickIconColor = tickIconColor,
            tickIconBackgroundColor = tickIconBackgroundColor,
            borderIndicatorColor = borderIndicatorColor
        )
    }
}

@Stable
class PickyTabColors(
    val containerColor: Color ,
    val contentColor: Color ,
    val selectedTabColor: Color ,
    val selectedTabContainerColor: Color,
    val unselectedTabColor: Color ,
    val tabIndicatorColor: Color
)
@Stable
class PickySelectionColors(
    val tickIconColor : Color,
    val tickIconBackgroundColor : Color,
    val borderIndicatorColor : Color
)


