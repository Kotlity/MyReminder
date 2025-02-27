package com.kotlity.feature_reminder_editor.utils

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.kotlity.utils.DateUtil
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

internal class DateUtil<A: ComponentActivity>(
    private val androidComposeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {

    private val NAVIGATE_TO_YEAR_TEXT = "Navigate to year"
    val zoneIdUTC = ZoneId.of("Z")

    val currentDateUTC: LocalDate = LocalDate.now(zoneIdUTC)
    private val currentYear = currentDateUTC.year

    private val selectedDateInDatePickerDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
    private val fullMonthYearDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val shortenedMonthDayAndYearFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    val typedDateInDateInputTextFieldDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

    fun getClosestAllowedToSelectDayNode(
        dateTimeFormatter: DateTimeFormatter = selectedDateInDatePickerDateFormatter,
        localDate: LocalDate = currentDateUTC,
        isWeekendDaysAllowed: Boolean = true,
        substring: Boolean = true
    ): SemanticsNodeInteraction {
        if (isWeekendDaysAllowed) {
            val dayText = localDate.format(dateTimeFormatter)
            return androidComposeTestRule.onNodeWithText(dayText, substring)
        }
        val closestWeekdayInMillis = DateUtil.findClosestWeekdayInMillis(localDate = localDate)
        val dayText = Instant.ofEpochMilli(closestWeekdayInMillis).atOffset(ZoneOffset.UTC).format(dateTimeFormatter)
        return androidComposeTestRule.onNodeWithText(dayText, substring)
    }

    fun getDateText(
        dateTimeFormatter: DateTimeFormatter = fullMonthYearDateFormatter,
        localDate: LocalDate = currentDateUTC
    ): String = localDate.format(dateTimeFormatter)

    fun getCurrentYearNode(): SemanticsNodeInteraction = androidComposeTestRule.onNodeWithText("$NAVIGATE_TO_YEAR_TEXT $currentYear")

    private fun getPreviousYears(
        @androidx.annotation.IntRange(1, 5) step: IntRange = IntRange(start = 1, endInclusive = 5)
    ): List<Int> = step.map { currentYear - it }

    fun getPreviousYearNodes(): List<SemanticsNodeInteraction> {
        return getPreviousYears().map { year ->
            androidComposeTestRule.onNodeWithText("$NAVIGATE_TO_YEAR_TEXT $year")
        }
    }

    fun getNextYearNode(): SemanticsNodeInteraction {
        val nextYear = currentYear + 1
        val nextYearTextNode = "$NAVIGATE_TO_YEAR_TEXT $nextYear"
        return androidComposeTestRule.onNodeWithText(nextYearTextNode)
    }

    fun getCurrentDayNode(
        dateTimeFormatter: DateTimeFormatter = selectedDateInDatePickerDateFormatter
    ): SemanticsNodeInteraction {
        val currentDateText = currentDateUTC.format(dateTimeFormatter)

        return androidComposeTestRule.onNodeWithText(
            currentDateText,
            substring = true
        )
    }

    fun getPreviousDayNodes(
        step: LongRange = LongRange(start = 1, endInclusive = 5),
        dateTimeFormatter: DateTimeFormatter = selectedDateInDatePickerDateFormatter
    ): List<SemanticsNodeInteraction> {
        val previousDateTexts = step
            .map { currentDateUTC.minusDays(it) }
            .map { dates -> dates.format(dateTimeFormatter) }

        return previousDateTexts.map { previousDateText ->
            androidComposeTestRule.onNodeWithText(previousDateText)
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
        dateTimeFormatter: DateTimeFormatter = selectedDateInDatePickerDateFormatter,
        substring: Boolean = true
    ): List<SemanticsNodeInteraction> {
        val saturdayLocalDate = getSaturdayLocalDate()

        val saturdayDateText = saturdayLocalDate.format(dateTimeFormatter)
        val sundayDateText = saturdayLocalDate.plusDays(1).format(dateTimeFormatter)

        return listOf(saturdayDateText, sundayDateText).map { androidComposeTestRule.onNodeWithText(it, substring) }
    }
}