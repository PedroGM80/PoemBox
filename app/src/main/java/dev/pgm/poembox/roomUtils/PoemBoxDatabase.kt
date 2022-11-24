package dev.pgm.poembox.roomUtils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Draft::class],
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

    companion object {
        @Volatile
        private var INSTANCE: PoemBoxDatabase? = null

        fun getDatabase(context: Context): PoemBoxDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            if (INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    INSTANCE = buildDatabase(context)
                }
            }
            // Return database.
            return INSTANCE!!
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