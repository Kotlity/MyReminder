package com.kotlity.feature_reminders.presentation.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.kotlity.core.presentation.ui.theme.MyReminderTheme
import com.kotlity.core.presentation.util.PreviewAnnotation
import com.kotlity.core.resources.R
import com.kotlity.core.resources.R.drawable.main_reminder_logo
import com.kotlity.core.resources.R.string.addYourTaskFirst
import com.kotlity.core.resources.ResourcesConstant._0_28
import com.kotlity.core.resources.ResourcesConstant._0_71

@Composable
fun EmptyRemindersSection(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    @DrawableRes logoRes: Int = main_reminder_logo,
    @StringRes labelRes: Int = addYourTaskFirst,
    labelStyle: TextStyle = MaterialTheme.typography.displaySmall,
    emptyRemindersLogoDescription: String? = stringResource(id = R.string.emptyRemindersLogoDescription)
) {

    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(_0_71),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(_0_28),
                painter = painterResource(id = logoRes),
                contentDescription = emptyRemindersLogoDescription
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen._5dp)))
            Text(
                text = stringResource(id = labelRes),
                style = labelStyle
            )
        }
    }
}

@PreviewAnnotation
@Composable
private fun EmptyRemindersSectionPreview() {
    MyReminderTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            EmptyRemindersSection(
                modifier = Modifier.fillMaxWidth(_0_71)
            )
        }
    }
}