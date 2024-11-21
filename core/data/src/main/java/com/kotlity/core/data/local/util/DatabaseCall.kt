package com.kotlity.core.data.local.util

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.kotlity.core.data.local.toReminder
import com.kotlity.core.domain.util.DatabaseError
import com.kotlity.core.domain.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

suspend inline fun <reified T> databaseCall(
    dispatcher: CoroutineDispatcher,
    crossinline block: suspend () -> Result<T, DatabaseError>
): Result<T, DatabaseError> {
    return try {
        withContext(dispatcher) {
            block()
        }
    } catch (e: Exception) {
        val error = when(e) {
            is IllegalStateException -> DatabaseError.ILLEGAL_STATE
            is SQLiteConstraintException -> DatabaseError.SQLITE_CONSTRAINT
            is SQLiteException -> DatabaseError.SQLITE_EXCEPTION
            is IllegalArgumentException -> DatabaseError.ILLEGAL_ARGUMENT
            else -> DatabaseError.UNKNOWN
        }
        Result.Error(error)
    }
}

inline fun <reified T, reified R> databaseFlowCall(
    dispatcher: CoroutineDispatcher,
    databaseFlow: Flow<T>,
    noinline onCollect: suspend (T) -> Result<R, DatabaseError>
): Flow<Result<R, DatabaseError>> {
    return flow {
        emit(Result.Loading)
        databaseFlow.collect {
            emit(onCollect(it))
        }
    }
        .catch { throwable ->
            val error = when(throwable) {
                is IllegalStateException -> DatabaseError.ILLEGAL_STATE
                is SQLiteConstraintException -> DatabaseError.SQLITE_CONSTRAINT
                is SQLiteException -> DatabaseError.SQLITE_EXCEPTION
                is IllegalArgumentException -> DatabaseError.ILLEGAL_ARGUMENT
                else -> DatabaseError.UNKNOWN
            }
            emit(Result.Error(error))
        }
        .flowOn(dispatcher)
}