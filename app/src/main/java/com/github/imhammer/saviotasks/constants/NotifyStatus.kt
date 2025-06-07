package com.github.imhammer.saviotasks.constants

import androidx.compose.ui.graphics.Color
import com.github.imhammer.saviotasks.ui.theme.MyColors

enum class NotifyStatus(
    var color: Color
) {
    DANGER(MyColors.LightRed),
    INFO(MyColors.DarkGray),
    SUCCESS(MyColors.Lime)
}