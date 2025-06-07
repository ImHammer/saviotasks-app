package com.github.imhammer.saviotasks.objects

import androidx.compose.ui.graphics.Color
import com.github.imhammer.saviotasks.ui.theme.MyColors

data class Notification(
    val text: String,
    val color: Color = MyColors.Gray
)
