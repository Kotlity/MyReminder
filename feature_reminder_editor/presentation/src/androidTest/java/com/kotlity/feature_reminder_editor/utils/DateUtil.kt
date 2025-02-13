package com.kotlity.feature_reminder_editor.utils

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithText
import com.kotlity.utils.DateUtil
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

internal object DateUtil {

    private const val NAVIGATE_TO_YEAR_TEXT = "Navigate to year"
    val zoneIdUTC = ZoneId.of("Z")

    val currentDateUTC: LocalDate = LocalDate.now(zoneIdUTC)
    private val currentYear = currentDateUTC.year

    private val selectedDateInDatePickerDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
    private val fullMonthYearDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val shortenedMonthDayAndYearFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    val typedDateInDateInputTextFieldDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

    fun getClosestAllowedToSelectDayTextInDatePicker(
        dateTimeFormatter: DateTimeFormatter = selectedDateInDatePickerDateFormatter,
        localDate: LocalDate = currentDateUTC,
        isWeekendDaysAllowed: Boolean = true
    ): String {
        if (isWeekendDaysAllowed) return localDate.format(dateTimeFormatter)
        val closestWeekdayInMillis = DateUtil.findClosestWeekdayInMillis(localDate = localDate)
        return Instant.ofEpochMilli(closestWeekdayInMillis).atOffset(ZoneOffset.UTC).format(dateTimeFormatter)
    }

    fun getDateText(
        dateTimeFormatter: DateTimeFormatter = fullMonthYearDateFormatter,
        localDate: LocalDate = currentDateUTC
    ): String = localDate.format(dateTimeFormatter)

    fun getCurrentYearNode(
        semanticsNodeInteractionsProvider: SemanticsNodeInteractionsProvider
    ): SemanticsNodeInteraction = semanticsNodeInteractionsProvider.onNodeWithText("$NAVIGATE_TO_YEAR_TEXT $currentYear")

    private fun getPreviousYears(
        @androidx.annotation.IntRange(1, 5) step: IntRange = IntRange(start = 1, endInclusive = 5)
    ): List<Int> = step.map { currentYear - it }

    fun getPreviousYearNodes(
        semanticsNodeInteractionsProvider: SemanticsNodeInteractionsProvider
    ): List<SemanticsNodeInteraction> {
        return getPreviousYears().map { year ->
            semanticsNodeInteractionsProvider.onNodeWithText("$NAVIGATE_TO_YEAR_TEXT $year")
        }
    }

    fun getNextYearNode(
        semanticsNodeInteractionsProvider: SemanticsNodeInteractionsProvider
    ): SemanticsNodeInteraction {
        val nextYear = currentYear + 1
        val nextYearTextNode = "$NAVIGATE_TO_YEAR_TEXT $nextYear"
        return semanticsNodeInteractionsProvider.onNodeWithText(nextYearTextNode)
    }

    fun getCurrentDayNode(
        semanticsNodeInteractionsProvider: SemanticsNodeInteractionsProvider,
        dateTimeFormatter: DateTimeFormatter = selectedDateInDatePickerDateFormatter
    ): SemanticsNodeInteraction {
        val currentDateText = currentDateUTC.format(dateTimeFormatter)

        return semanticsNodeInteractionsProvider.onNodeWithText(
            currentDateText,
            substring = true
        )
    }

    fun getPreviousDayNodes(
        semanticsNodeInteractionsProvider: SemanticsNodeInteractionsProvider,
        step: LongRange = LongRange(start = 1, endInclusive = 5),
        dateTimeFormatter: DateTimeFormatter = selectedDateInDatePickerDateFormatter
    ): List<SemanticsNodeInteraction> {
        val previousDateTexts = step
            .map { currentDateUTC.minusDays(it) }
            .map { dates -> dates.format(dateTimeFormatter) }

        return previousDateTexts.map { previousDateText ->
            semanticsNodeInteractionsProvider.onNodeWithText(previousDateText)
        }
    }

    fun getSaturdayLocalDate(): LocalDate {
        var updatedDateUTC = currentDateUTC

        while (updatedDateUTC.dayOfWeek != DayOfWeek.SATURDAY) {
            updatedDateUTC = updatedDateUTC.plusDays(1)
        }
        return updatedDateUTC
    }

    fun getClosestWeekendNodes(
        semanticsNodeInteractionsProvider: SemanticsNodeInteractionsProvider,
        dateTimeFormatter: DateTimeFormatter = selectedDateInDatePickerDateFormatter
    ): List<SemanticsNodeInteraction> {
        val saturdayLocalDate = getSaturdayLocalDate()

        val saturdayDateText = saturdayLocalDate.format(dateTimeFormatter)
        val sundayDateText = saturdayLocalDate.plusDays(1).format(dateTimeFormatter)

        return listOf(saturdayDateText, sundayDateText).map { semanticsNodeInteractionsProvider.onNodeWithText(it) }
    }
}