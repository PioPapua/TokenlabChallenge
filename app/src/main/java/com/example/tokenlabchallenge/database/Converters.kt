package com.example.tokenlabchallenge.database

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun listToString(list: List<String>?): String? {
        return list?.joinToString(separator = ",")
    }

    @TypeConverter
    fun stringToList(list: String?): List<String>? {
        return list?.split(",")
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}