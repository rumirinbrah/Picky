package dev.rumirinbrah.picky.api

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

object PickyDefaults {

    /**
     * Can be used for customizing colors of the Recents | Albums Tab.
     *
     * @author zyzz
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



