package com.example.prmproject1.database

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateStringConverter {

    @TypeConverter
    fun stringToLocalDate(string: String): LocalDate {
        return LocalDate.parse(string)
    }

    @TypeConverter
    fun localDateToString(date: LocalDate): String {
        return date.toString()
    }

}