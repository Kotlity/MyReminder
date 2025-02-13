package com.kotlity.utils

import androidx.compose.ui.test.SemanticsNodeInteraction

interface TestNodeFinder {

    fun onNodeWithContentDescription(
        description: String,
        substring: Boolean = false,
        ignoreCase: Boolean = false,
        useUnmergedTree: Boolean = false
    ): SemanticsNodeInteraction

    fun onNodeWithTestTag(
        tag: String,
        useUnmergedTree: Boolean = false
    ): SemanticsNodeInteraction

    fun onNodeWithText(
        text: String,
        substring: Boolean = false,
        ignoreCase: Boolean = false,
        useUnmergedTree: Boolean = false
    ): SemanticsNodeInteraction

}