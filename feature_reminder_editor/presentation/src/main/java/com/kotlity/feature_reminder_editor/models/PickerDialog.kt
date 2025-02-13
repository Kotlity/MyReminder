package com.kotlity.feature_reminder_editor.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

 sealed interface PickerDialog: Parcelable {
    @Parcelize
    enum class Time: PickerDialog {
        TIME_PICKER,
        TIME_INPUT
    }

    val isTime
        get() = this is Time

     val isTimePicker
         get() = this == Time.TIME_PICKER

     val isTimeInput
         get() = this == Time.TIME_INPUT
    val getTime
        get() = this as Time

    @Parcelize
    data object Date: PickerDialog

    val isDate
        get() = this is Date
}