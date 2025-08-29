package com.example.raspapp.ui.theme


import androidx.compose.material3.Typography
import androidx.compose.ui.text.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with

val mPlusFamily = FontFamily(
    Font(com.example.raspapp.R.font.m_plus_rounded_1c_light, FontWeight.Light),
    Font(com.example.raspapp.R.font.m_plus_rounded_1c, FontWeight.Normal),
    Font(com.example.raspapp.R.font.m_plus_rounded_1c_medium, FontWeight.Medium)
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = mPlusFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center, // Выравниваем текст по центру строки
            trim = LineHeightStyle.Trim.Both // Убираем лишние отступы сверху и снизу
        )
    ))
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */