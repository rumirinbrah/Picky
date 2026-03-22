package dev.rumirinbrah.picky.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun ActionButtonHeader(
    @DrawableRes actionIcon: Int ,
    actionDescription: String? = null ,
    title : String? ,
    fontSize : TextUnit = 20.sp ,
    fontWeight : FontWeight = FontWeight.Bold ,
    textColor : Color = MaterialTheme.colorScheme.onBackground.copy(0.8f) ,
    itemsSpacing : Dp = 16.dp ,
    onAction: () -> Unit ,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(itemsSpacing)
    ) {
        IconButton(
            onAction,
            modifier = Modifier
        ) {
            Icon(
                painter = painterResource(actionIcon) ,
                contentDescription = actionDescription ,
                modifier = Modifier.size(30.dp)
            )
        }
        title?.let {
            Text(
                title ,
                modifier = Modifier,
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}