package dev.rumirinbrah.picky.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.rumirinbrah.picky.api.PickyTabColors

/**
 * A tab row for wandera image picker. Has two tabs- Recents, Albums
 */
@Composable
internal fun PickerTabRow(
    modifier: Modifier = Modifier ,
    currentTab: Int ,
    onTabChange: (Int) -> Unit ,
    colors: PickyTabColors,
) {


    TabRow(
        modifier = modifier ,
        selectedTabIndex = currentTab ,
        containerColor = colors.containerColor ,
        contentColor = colors.contentColor ,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                color = colors.tabIndicatorColor ,
                modifier = Modifier.tabIndicatorOffset(tabPositions[currentTab]) ,
                height = 1.dp
            )
        }
    ) {
        CustomTab(
            title = "Recent" ,
            onClick = onTabChange ,
            selected = currentTab == 0 ,
            tabNo = 0 ,
            modifier = Modifier.fillMaxWidth(),
            selectedTabColor = colors.selectedTabColor,
            selectedTabContainerColor = colors.selectedTabContainerColor,
            unselectedTabColor = colors.unselectedTabColor
        )

        CustomTab(
            title = "Albums" ,
            onClick = onTabChange ,
            selected = currentTab == 1 ,
            tabNo = 1 ,
            modifier = Modifier.fillMaxWidth(),
            selectedTabColor = colors.selectedTabColor,
            unselectedTabColor = colors.unselectedTabColor
        )

    }
}

/**
 * Represents a tab in the tab row
 */
@Composable
internal fun CustomTab(
    modifier: Modifier = Modifier ,
    title: String ,
    selected: Boolean = false ,
    tabNo: Int ,
    onClick: (Int) -> Unit ,
    selectedTabColor: Color = MaterialTheme.colorScheme.onBackground ,
    selectedTabContainerColor: Color = MaterialTheme.colorScheme.primaryContainer ,
    unselectedTabColor: Color = MaterialTheme.colorScheme.onBackground.copy(0.5f) ,
    verticalPadding: Dp = 16.dp ,
    drawIndicator: Boolean = false ,
) {
    Box(
        modifier
            .drawBehind{
                if(selected){
                    drawRoundRect(
                        selectedTabContainerColor ,
                        cornerRadius = CornerRadius(35f,35f)
                    )
                }
            }
            .padding(vertical = verticalPadding)
            .clickable(
                indication = null ,
                interactionSource = null ,
                onClick = {
                    onClick(tabNo)
                }
            )
            .drawBehind {
                if (selected && drawIndicator) {
                    drawLine(
                        selectedTabColor ,
                        start = Offset(0f , size.height) ,
                        end = Offset(size.width , size.height) ,
                        strokeWidth = 5f
                    )
                }
            } ,
        contentAlignment = Alignment.Center
    ) {
        Text(
            title ,
            color = if (selected) {
                selectedTabColor
            } else {
                unselectedTabColor
            } ,
            fontWeight = FontWeight.Medium
        )
    }
}