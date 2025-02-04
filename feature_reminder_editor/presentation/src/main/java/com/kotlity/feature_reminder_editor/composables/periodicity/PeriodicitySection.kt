package com.kotlity.feature_reminder_editor.composables.periodicity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.kotlity.core.Periodicity
import com.kotlity.core.ResourcesConstant._0_8
import com.kotlity.core.resources.R
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.PreviewAnnotation

@Composable
internal fun PeriodicitySection(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(dimensionResource(id = R.dimen._15dp)),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    periodicity: Periodicity,
    isExpanded: Boolean,
    onSelectedItemClick: () -> Unit,
    onPeriodicityItemClick: (Periodicity) -> Unit
) {

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        EditorPeriodicityTitleSection()
        PeriodicityPickerWidget(
            modifier = Modifier.fillMaxWidth(),
            periodicity = periodicity,
            isExpanded = isExpanded,
            onSelectedItemClick = onSelectedItemClick,
            onItemClick = onPeriodicityItemClick
        )
    }
}

@PreviewAnnotation
@Composable
private fun PeriodicitySectionPreview() {

    var periodicity by rememberSaveable {
        mutableStateOf(Periodicity.ONCE)
    }

    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    MyReminderTheme {
        PeriodicitySection(
            modifier = Modifier
                .fillMaxWidth(_0_8)
                .padding(20.dp)
            ,
            periodicity = periodicity,
            isExpanded = isExpanded,
            onSelectedItemClick = {
                isExpanded = !isExpanded
            },
            onPeriodicityItemClick = { chosenPeriodicity ->
                periodicity = chosenPeriodicity
            }
        )
    }
}