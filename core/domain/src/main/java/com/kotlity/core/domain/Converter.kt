package com.kotlity.core.domain

interface Converter<I, O> {

    fun from(value: I): O

    fun to(value: O): I
}