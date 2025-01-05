package com.kotlity.core

interface Converter<I, O> {

    fun from(value: I): O

    fun to(value: O): I
}