package com.kotlity.core.local.util

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.kotlity.core.util.DatabaseError
import com.kotlity.core.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

suspend inline fun <reified T> databaseCall(
    dispatcher: CoroutineDispatcher,
    crossinline block: suspend () -> Result<T, DatabaseError>
): Result<T, DatabaseError> {
    return try {
        withContext(dispatcher) {
            block()
        }
    } catch (e: Exception) {
        coroutineContext.ensureActive()
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
    crossinline flowProvider: () -> Flow<T>,
    crossinline mapper: (T) -> R
): Flow<Result<R, DatabaseError>> {
    return flow<Result<R, DatabaseError>> {
        emit(Result.Loading)
        flowProvider()
            .map { mapper(it) }
            .collect {
                val result = Result.Success(it)
                emit(result)
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