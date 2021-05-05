package com.prm.project1.database

import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * [TypeConverter] converting [LocalDate] to [String] and vice versa.
 */
class LocalDateStringConverter {

    @TypeConverter
    fun stringToLocalDate(string: String): LocalDate = LocalDate.parse(string)

    @TypeConverter
    fun localDateToString(date: LocalDate): String = date.toString()
}