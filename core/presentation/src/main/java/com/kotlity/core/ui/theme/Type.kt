package com.kotlity.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.kotlity.core.ResourcesConstant._14sp
import com.kotlity.core.ResourcesConstant._15sp
import com.kotlity.core.ResourcesConstant._16sp
import com.kotlity.core.ResourcesConstant._19sp
import com.kotlity.core.ResourcesConstant._24sp
import com.kotlity.core.ResourcesConstant._26sp
import com.kotlity.core.ResourcesConstant._30sp

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
        fontSize = _30sp,
        color = black
    ),
    labelSmall = TextStyle(
        fontFamily = CoolVetica,
        fontSize = _14sp,
        fontWeight = FontWeight.Bold,
        color = onTertiary
    ),
    titleMedium = TextStyle(
        fontFamily = CoolVetica,
        fontSize = _24sp,
        fontWeight = FontWeight.Bold,
        color = black
    ),
    titleLarge = TextStyle(
        fontFamily = CoolVetica,
        fontSize = _26sp,
        fontWeight = FontWeight.Bold,
        color = black
    ),
    bodyMedium = TextStyle(
        fontFamily = AlteHassGrotesk,
        fontSize = _14sp,
        color = darkBlack
    ),
    bodyLarge = TextStyle(
        fontFamily = AlteHassGrotesk,
        fontSize = _16sp,
        color = halfGrey
    ),
    bodySmall = TextStyle(
        fontFamily = AlteHassGrotesk,
        fontSize = _15sp,
        color = grey
    ),
    displayLarge = TextStyle(
        fontFamily = AlteHassGrotesk,
        fontSize = _19sp,
        fontWeight = FontWeight.Bold,
        color = white
    ),
    displayMedium = TextStyle(
        fontFamily = AlteHassGrotesk,
        fontSize = _16sp,
        color = black
    ),
    displaySmall = TextStyle(
        fontFamily = AlteHassGrotesk,
        fontSize = _15sp,
        color = halfGrey
    )

)