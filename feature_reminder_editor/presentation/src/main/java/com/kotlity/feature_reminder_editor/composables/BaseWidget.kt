package com.kotlity.feature_reminder_editor.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.white

@Composable
internal fun BaseWidget(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    colors: CardColors = CardDefaults.cardColors(containerColor = white),
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = dimen._20dp)),
    border: BorderStroke? = null,
    content: @Composable (ColumnScope.() -> Unit)
) {

    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = content
    )
}