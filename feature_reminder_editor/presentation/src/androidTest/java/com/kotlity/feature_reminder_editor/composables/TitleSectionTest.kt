package com.kotlity.feature_reminder_editor.composables

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.google.common.truth.Truth.assertThat
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.feature_reminder_editor.composables.title.TitleSection
import com.kotlity.utils.ComposeTestRuleProvider
import org.junit.After
import org.junit.Test

private const val correctInput = "Hello there !"
private const val wrongInput = "1 there is a wrong input"

class TitleSectionTest: ComposeTestRuleProvider() {

    private val title: SemanticsNodeInteraction by lazy { onNodeWithText(titleText) }
    private val titleIcon: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(string.titleIconDescription)) }
    private val titleTextField: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.titleTextFieldTestTag)) }
    private val titleTextFieldHint: SemanticsNodeInteraction by lazy { onNodeWithText(getString(string.insertTitle)) }
    private val titleTextFieldErrorText: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.titleTextFieldErrorTestTag)) }

    private val titleText = getString(string.title)
    private val titleTextFieldHintText = getString(string.insertTitle)
    private val errorText = getString(string.titleStartsWithADigit)

    private var inputText by mutableStateOf("")

    @After
    fun teardown() {
        inputText = ""
    }

    @Test
    fun initially_TitleTextFieldHintIsDisplayed_and_TitleTextFieldErrorTextIsNotDisplayed() {
        composeTestRule.setContent {
            MyReminderTheme {
                TitleSection(
                    text = inputText,
                    onTextChange = {},
                    hint = titleTextFieldHintText,
                    isError = false,
                    onFocusChange = {}
                )
            }
        }

        title
            .assertIsDisplayed()
            .assertTextEquals(titleText)

        titleIcon.assertIsDisplayed()

        titleTextField
            .assertTextEquals(titleTextFieldHintText, includeEditableText = false)
            .assertIsNotFocused()

        titleTextField.assertIsDisplayed()

        titleTextFieldErrorText.assertDoesNotExist()
    }

    @Test
    fun titleTextFieldWithCorrectedInput_removedTitleTextFieldHint_and_inFocusedState() {
        var isTitleTextFieldFocused = false

        composeTestRule.setContent {
            MyReminderTheme {
                TitleSection(
                    text = inputText,
                    onTextChange = { inputText = it },
                    hint = null,
                    isError = false,
                    onFocusChange = { isTitleTextFieldFocused = it }
                )
            }
        }

        titleTextField.performTextInput(correctInput)

        titleTextField.assertTextEquals(correctInput)

        titleTextFieldHint.assertDoesNotExist()

        titleTextFieldErrorText.assertDoesNotExist()

        assertThat(inputText).isEqualTo(correctInput)
        assertThat(isTitleTextFieldFocused).isTrue()
    }

    @Test
    fun titleTextFieldWithWrongInput_removedTitleTextFieldHint_and_inFocusedState() {
        var isTitleTextFieldFocused = false

        composeTestRule.setContent {
            MyReminderTheme {
                TitleSection(
                    text = inputText,
                    onTextChange = { inputText = it },
                    hint = null,
                    errorText = errorText,
                    isError = true,
                    onFocusChange = { isTitleTextFieldFocused = it }
                )
            }
        }

        titleTextField.performTextInput(wrongInput)

        titleTextField.assertTextEquals(wrongInput)

        titleTextFieldHint.assertDoesNotExist()

        titleTextFieldErrorText
            .assertIsDisplayed()
            .assertTextEquals(errorText)

        assertThat(inputText).isEqualTo(wrongInput)

        assertThat(isTitleTextFieldFocused).isTrue()
    }

    @Test
    fun titleTextFieldUpdatesFormWrongToCorrectInput_removedTitleTextFieldErrorText() {
        composeTestRule.setContent {
            val dynamicHint = if (inputText.isBlank()) titleTextFieldHintText else null
            val dynamicErrorText = if (inputText == wrongInput) errorText else null
            val dynamicError = dynamicErrorText != null

            MyReminderTheme {
                TitleSection(
                    text = inputText,
                    onTextChange = { inputText = it },
                    hint = dynamicHint,
                    errorText = dynamicErrorText,
                    isError = dynamicError,
                    onFocusChange = {}
                )
            }
        }

        titleTextField.assertTextContains("")

        titleTextField.performTextInput(wrongInput)

        titleTextField.assertTextEquals(wrongInput)

        titleTextFieldHint.assertDoesNotExist()

        titleTextFieldErrorText
            .assertIsDisplayed()
            .assertTextEquals(errorText)

        titleTextField.performTextClearance()

        titleTextFieldHint.assertIsDisplayed()

        titleTextFieldErrorText.assertDoesNotExist()

        titleTextField.performTextInput(correctInput)

        titleTextField.assertTextEquals(correctInput)

        titleTextFieldHint.assertDoesNotExist()
    }
}