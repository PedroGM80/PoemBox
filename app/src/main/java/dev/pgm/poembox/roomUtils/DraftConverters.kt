package dev.pgm.poembox.roomUtils

import androidx.room.TypeConverter
import java.util.*

class DraftConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}