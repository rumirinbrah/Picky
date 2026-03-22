package dev.rumirinbrah.picky.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.rumirinbrah.feature_picky.R


/**
 * Bottom action bar for multi select options.
 *
 * Shows options such as Clear, Discard & Confirm
 *
 * @author zyzz
*/
@Composable
fun MultiSelectActionsCard(
    modifier: Modifier = Modifier,
    numImages : String,
    onDone : ()->Unit,
    onClear : ()-> Unit,
    onDiscard : ()->Unit,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer ,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    buttonColor : Color = MaterialTheme.colorScheme.primaryContainer,
    buttonContentColor : Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Row(
        modifier.clip(MaterialTheme.shapes.extraLarge)
            .fillMaxWidth()
//            .height(100.dp)
            .background(containerColor)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onClear()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.close) ,
                    contentDescription = "Clear selection",
                    modifier = Modifier.size(24.dp),
                    tint = contentColor
                )
            }
            Text(
                numImages,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    onDiscard()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            ) {
                Text(
                    "Discard"
                )
            }
            Button(
                onClick = {
                    onDone()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = buttonContentColor
                )
            ) {
                Text(
                    "Done"
                )
            }
        }
    }
}
