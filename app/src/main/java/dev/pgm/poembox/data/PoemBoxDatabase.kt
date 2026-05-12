package dev.pgm.poembox.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Poem box database
 * @author Pedro Gallego Morales
 */
@Database(
    entities = [Draft::class, Sheet::class],
    version = 1,
    exportSchema = true
)
abstract class PoemBoxDatabase : RoomDatabase() {

    /**
     * Draft dao
     *
     * @return  DraftDao
     */
    abstract fun draftDao(): DraftDao

    /**
     * Sheet dao
     *
     * @return  SheetDao
     */
    abstract fun sheetDao(): SheetDao
}
