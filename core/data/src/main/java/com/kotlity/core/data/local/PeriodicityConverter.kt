package com.kotlity.core.data.local

import androidx.room.TypeConverter
import com.kotlity.core.domain.Converter
import com.kotlity.core.domain.Periodicity

class PeriodicityConverter: Converter<Periodicity, String> {

    @TypeConverter
    override fun from(value: Periodicity): String {
        return value.name.lowercase()
    }

    @TypeConverter
    override fun to(value: String): Periodicity {
        return Periodicity.valueOf(value.uppercase())
    }
}