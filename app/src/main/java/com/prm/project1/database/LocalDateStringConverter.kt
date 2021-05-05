package com.prm.project1.database

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateStringConverter {
    @TypeConverter
    fun stringToLocalDate(string: String): LocalDate = LocalDate.parse(string)

    @TypeConverter
    fun localDateToString(date: LocalDate): String = date.toString()
}