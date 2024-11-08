package com.kotlity.core.data.local

import com.kotlity.core.domain.Converter
import com.kotlity.core.domain.Periodicity

class PeriodicityConverter: Converter<Periodicity, String> {

    override fun from(value: Periodicity): String {
        return value.name.lowercase()
    }

    override fun to(value: String): Periodicity {
        return Periodicity.valueOf(value.uppercase())
    }
}