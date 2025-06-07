package com.github.imhammer.saviotasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundedButton(
    modifier: Modifier = Modifier,
    title: String = "Button",
    radius: Dp = 5.dp,
    color: Color = Color.Black,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth(0.9f)
            .height(40.dp)
            .clip(RoundedCornerShape(radius))
            .background(color)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}