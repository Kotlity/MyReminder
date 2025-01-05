package com.kotlity.core.util

enum class DatabaseError: Error {
    ILLEGAL_STATE,
    SQLITE_CONSTRAINT,
    SQLITE_EXCEPTION,
    ILLEGAL_ARGUMENT,
    UNKNOWN
}