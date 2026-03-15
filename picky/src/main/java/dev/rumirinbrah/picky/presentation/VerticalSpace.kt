package dev.rumirinbrah.picky.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun VerticalSpace(
    height : Dp = 20.dp
) {
    Spacer(Modifier.height(height))
}