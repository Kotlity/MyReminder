package com.kotlity.feature_reminder_editor.utils

internal interface TextFieldHintProvider<T> {

    fun getHint(data: T): String?
}