package dev.pgm.poembox.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.pgm.poembox.data.local.dao.DraftDao
import dev.pgm.poembox.data.local.dao.SheetDao
import dev.pgm.poembox.data.local.entities.DraftEntity
import dev.pgm.poembox.data.local.entities.SheetEntity

@Database(
    entities = [DraftEntity::class, SheetEntity::class],
    version = 1,
    exportSchema = true
)
abstract class PoemBoxDatabase : RoomDatabase() {
    abstract fun draftDao(): DraftDao
    abstract fun sheetDao(): SheetDao
}
