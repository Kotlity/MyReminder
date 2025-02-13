package com.kotlity.feature_reminder_editor.composables.periodicity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.kotlity.core.Periodicity
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.ui.theme.halfGreyContainer
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.feature_reminder_editor.mappers.mapToString

@Composable
fun PeriodicityPickerWidget(
    modifier: Modifier = Modifier,
    periodicity: Periodicity,
    isExpanded: Boolean,
    shape: Shape = if (isExpanded) RoundedCornerShape(
        topStart = dimensionResource(id = dimen._12dp),
        topEnd = dimensionResource(id = dimen._12dp)
    ) else CircleShape,
    colors: CardColors = CardDefaults.cardColors(containerColor = halfGreyContainer),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    onSelectedItemClick: () -> Unit,
    onItemClick: (Periodicity) -> Unit
) {

    val context = LocalContext.current

    val periodicityItemLayoutState = rememberPeriodicityItemLayoutState()

    val icon = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    val iconContentDescription = if (isExpanded) string.periodicityArrowUpDescription else string.periodicityArrowDownDescription

    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border
    ) {
        SelectedPeriodicityItem(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { layoutCoordinates ->
                    periodicityItemLayoutState.updateLayoutState(layoutCoordinates = layoutCoordinates)
                }
                .testTag(stringResource(id = string.selectedPeriodicityItemTestTag)),
            text = periodicity.mapToString(context = context),
            icon = icon,
            iconContentDescription = iconContentDescription,
            onClick = {
                onSelectedItemClick()
            }
        )
        if (isExpanded) {
            Popup(
                offset = periodicityItemLayoutState.periodicityItemPosition,
            ) {
                Column(
                    modifier = Modifier
                        .width(periodicityItemLayoutState.periodicityItemSize.width)
                        .background(
                            color = halfGreyContainer,
                            shape = RoundedCornerShape(
                                bottomStart = dimensionResource(id = dimen._12dp),
                                bottomEnd = dimensionResource(id = dimen._12dp)
                            )
                        )
                        .testTag(stringResource(id = string.periodicityListTestTag))
                ) {
                    Periodicity.entries.forEach { periodicityItem ->
                        PeriodicityItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = dimensionResource(id = dimen._5dp))
                            ,
                            text = periodicityItem.mapToString(context = context),
                            onClick = {
                                onItemClick(periodicityItem)
                                onSelectedItemClick()
                            }
                        )
                    }
                }
            }
        }
    }
}

private data class PeriodicityItemSize(
    val width: Dp,
    val height: Dp
)

private interface PeriodicityItemLayoutState {

    val periodicityItemSize: PeriodicityItemSize

    val periodicityItemPosition: IntOffset

    fun updateLayoutState(layoutCoordinates: LayoutCoordinates)
}

private class PeriodicityItemLayoutStateImpl(private val density: Density): PeriodicityItemLayoutState {

    private var _periodicityItemSize by mutableStateOf(PeriodicityItemSize(width = 0.dp, height = 0.dp))
    override val periodicityItemSize: PeriodicityItemSize get() = _periodicityItemSize

    private var _periodicityItemPosition by mutableStateOf(IntOffset.Zero)
    override val periodicityItemPosition: IntOffset get() = _periodicityItemPosition

    override fun updateLayoutState(layoutCoordinates: LayoutCoordinates) {
        with(density) {
            val width = layoutCoordinates.size.width
            val height = layoutCoordinates.size.height

            val position = layoutCoordinates.positionInParent()
            val xPosition = position.x.dp.roundToPx()
            val yPosition = position.y.dp.roundToPx() + height

            _periodicityItemSize = _periodicityItemSize.copy(width = width.toDp(), height = height.toDp())
            _periodicityItemPosition = IntOffset(x = xPosition, y = yPosition)
        }
    }
}

@Composable
private fun rememberPeriodicityItemLayoutState(): PeriodicityItemLayoutState {
    val density = LocalDensity.current
    return remember { PeriodicityItemLayoutStateImpl(density = density) }
}

@PreviewAnnotation
@Composable
private fun PeriodicityPickerWidgetPreview() {

    var selectedPeriodicity by rememberSaveable {
        mutableStateOf(Periodicity.ONCE)
    }
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    MyReminderTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            PeriodicityPickerWidget(
                modifier = Modifier.fillMaxWidth(0.7f),
                periodicity = selectedPeriodicity,
                isExpanded = isExpanded,
                onSelectedItemClick = {
                    isExpanded = !isExpanded
                },
                onItemClick = { chosenPeriodicity ->
                    selectedPeriodicity = chosenPeriodicity
                }
            )
        }
    }
}