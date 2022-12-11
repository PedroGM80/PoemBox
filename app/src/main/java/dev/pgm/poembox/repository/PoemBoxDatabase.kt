package dev.pgm.poembox.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.pgm.poembox.domain.ContextContentProvider

@Database(
    entities = [Draft::class, Sheet::class],
    version = 1,
    exportSchema = true
)//V1 need one build
/*
@Database(
    entities = [Draft::class],
    autoMigrations = [
        AutoMigration (from = 1, to = 2)
   ],
    version = 2,
    exportSchema = true
)*/

abstract class PoemBoxDatabase : RoomDatabase() {

    abstract fun draftDao(): DraftDao
    abstract fun sheetDao(): SheetDao

    companion object {
        @Volatile
        private var INSTANCE: PoemBoxDatabase? = null

        fun getDatabase(): PoemBoxDatabase? {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            if (INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    INSTANCE = ContextContentProvider.applicationContext()
                        ?.let { buildDatabase(it) }
                }
            }
            // Return database.
            return INSTANCE
        }

        private fun buildDatabase(context: Context): PoemBoxDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                PoemBoxDatabase::class.java,
                "poem_box_database"
            )
                .build()
        }
    }
}