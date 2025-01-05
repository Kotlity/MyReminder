package com.kotlity.core.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

sealed interface UiText {
    data class DynamicString(val text: String): UiText
    class StringResource(@StringRes val resId: Int, val args: Array<Any> = emptyArray()): UiText

    @Composable
    fun asComposeString(): String {
        return when(this) {
            is DynamicString -> text
            is StringResource -> LocalContext.current.getString(resId, args)
        }
    }

    fun asString(context: Context): String {
        return when(this) {
            is DynamicString -> text
            is StringResource -> context.getString(resId, args)
        }
    }
}