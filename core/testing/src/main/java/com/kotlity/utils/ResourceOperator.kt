package com.kotlity.utils

import androidx.annotation.StringRes

interface ResourceOperator {

    fun getString(@StringRes id: Int): String
}