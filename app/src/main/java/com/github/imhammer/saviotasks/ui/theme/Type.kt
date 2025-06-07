package com.github.imhammer.saviotasks.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.imhammer.saviotasks.R

// Set of Material typography styles to start with
val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.itim)),
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        lineHeight = 24.sp
    ),
    displayLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.itim)),
        fontWeight = FontWeight.Normal,
        fontSize = 50.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.itim)),
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.itim)),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.itim)),
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
)