package com.kotlity.core.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val CoolVetica = FontFamily(
    Font(resId = com.kotlity.core.resources.R.font.coolvetica_condensed),
    Font(
        resId = com.kotlity.core.resources.R.font.coolvetica_rg,
        weight = FontWeight.Bold
    )
)

val AlteHassGrotesk = FontFamily(
    Font(resId = com.kotlity.core.resources.R.font.alte_hass_grotesk_regular),
    Font(
        resId = com.kotlity.core.resources.R.font.alte_hass_grotesk_bold,
        weight = FontWeight.Bold
    )
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = CoolVetica,
        fontSize = 22.sp,
        color = black
    ),
    labelSmall = TextStyle(
        fontFamily = CoolVetica,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = onTertiary
    ),
    titleMedium = TextStyle(
        fontFamily = CoolVetica,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = black
    ),
    titleLarge = TextStyle(
        fontFamily = CoolVetica,
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold,
        color = black
    ),
    bodyMedium = TextStyle(
        fontFamily = AlteHassGrotesk,
        fontSize = 14.sp,
        color = darkBlack
    ),
    bodyLarge = TextStyle(
        fontFamily = AlteHassGrotesk,
        fontSize = 16.sp,
        color = halfGrey
    ),
    bodySmall = TextStyle(
        fontFamily = AlteHassGrotesk,
        fontSize = 13.sp,
        color = grey
    ),
    displayLarge = TextStyle(
        fontFamily = AlteHassGrotesk,
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold,
        color = white
    ),
    displayMedium = TextStyle(
        fontFamily = AlteHassGrotesk,
        fontSize = 16.sp,
        color = black
    ),
    displaySmall = TextStyle(
        fontFamily = AlteHassGrotesk,
        fontSize = 15.sp,
        color = halfGrey
    )

)