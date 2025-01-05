package com.kotlity.feature_reminders.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.LocalScreenSize
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.core.util.ScreenDimensions
import com.kotlity.core.resources.R.*
import com.kotlity.core.ResourcesConstant._1f

@Composable
fun CirclesSection(modifier: Modifier = Modifier) {

    val screenSize = LocalScreenSize.current

    Box(modifier = modifier) {
        Circle(
            modifier = Modifier
                .size(dimensionResource(id = dimen._40dp))
                .offset(
                    x = dimensionResource(id = dimen._30dp),
                    y = dimensionResource(id = dimen._60dp)
                ),
            color = MaterialTheme.colorScheme.primary
        )
        Circle(
            modifier = Modifier
                .size(dimensionResource(id = dimen._60dp))
                .offset(
                    x = dimensionResource(id = dimen.minus30dp),
                    y = screenSize.height / 6.5f
                ),
            color = MaterialTheme.colorScheme.tertiary
        )
        Circle(
            modifier = Modifier
                .size(dimensionResource(id = dimen._80dp))
                .offset(
                    x = screenSize.width - dimensionResource(id = dimen._17dp),
                    y = screenSize.height / 1.8f
                ),
            color = MaterialTheme.colorScheme.tertiary
        )
        Circle(
            modifier = Modifier
                .size(dimensionResource(id = dimen._60dp))
                .offset(
                    x = screenSize.width / 1.35f,
                    y = screenSize.height / 1.5f
                ),
            color = MaterialTheme.colorScheme.primary
        )
        Circle(
            modifier = Modifier
                .size(dimensionResource(id = dimen._30dp))
                .align(Alignment.BottomStart)
                .offset(y = dimensionResource(id = dimen.minus35dp))
                .zIndex(_1f),
            color = MaterialTheme.colorScheme.primary
        )
        Circle(
            modifier = Modifier
                .size(dimensionResource(id = dimen._60dp))
                .align(Alignment.BottomStart)
                .offset(
                    x = dimensionResource(id = dimen.minus30dp),
                    y = dimensionResource(id = dimen._30dp)
                ),
            color = MaterialTheme.colorScheme.tertiary
        )
        Circle(
            modifier = Modifier
                .size(dimensionResource(id = dimen._35dp))
                .align(Alignment.BottomStart)
                .offset(
                    x = dimensionResource(id = dimen._35dp),
                    y = dimensionResource(id = dimen._30dp)
                )
            ,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@PreviewAnnotation
@Composable
private fun CirclesSectionPreview() {
    val screenConfiguration = LocalConfiguration.current
    val screenDimensions by remember(screenConfiguration) {
        mutableStateOf(ScreenDimensions(width = screenConfiguration.screenWidthDp.dp, height = screenConfiguration.screenHeightDp.dp))
    }

    CompositionLocalProvider(LocalScreenSize provides screenDimensions) {
        MyReminderTheme {
            CirclesSection(modifier = Modifier.fillMaxSize())
        }
    }
}