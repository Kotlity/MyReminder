package com.kotlity.feature_reminders.presentation.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.kotlity.core.presentation.ui.theme.MyReminderTheme
import com.kotlity.core.presentation.util.PreviewAnnotation
import com.kotlity.core.resources.ResourcesConstant._0_71

@Composable
fun Circle(
    modifier: Modifier = Modifier,
    @DrawableRes circleRes: Int,
    contentDescription: String? = null
) {

    Image(
        modifier = modifier,
        painter = painterResource(id = circleRes),
        contentDescription = contentDescription
    )
}

@PreviewAnnotation
@Composable
fun CirclePreview() {
    MyReminderTheme {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            EmptyRemindersSection(
                modifier = Modifier.fillMaxWidth(_0_71)
            )
            Circle(
                modifier = Modifier
                    .size(dimensionResource(id = com.kotlity.core.resources.R.dimen._80dp))
                    .offset(
                        dimensionResource(id = com.kotlity.core.resources.R.dimen.minus20dp),
                        dimensionResource(id = com.kotlity.core.resources.R.dimen._10dp)
                    )
                    .align(Alignment.BottomStart),
                circleRes = com.kotlity.core.resources.R.drawable.circle_106,
            )
        }
    }
}