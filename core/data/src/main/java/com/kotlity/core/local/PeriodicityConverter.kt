package com.kotlity.core.local

import androidx.room.TypeConverter
import com.kotlity.core.Converter
import com.kotlity.core.Periodicity

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