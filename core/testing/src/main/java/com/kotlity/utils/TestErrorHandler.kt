package com.kotlity.utils

import com.kotlity.core.util.Error
import com.kotlity.core.util.Result

interface TestErrorHandler<E: Error> {

    var error: E?

    fun updateError(error: E) {
        this.error = error
    }

    fun <T> handleError(action: () -> Result<T, E>): Result<T, E> {
        return if (error != null) Result.Error(error = error!!)
        else action()
    }
}