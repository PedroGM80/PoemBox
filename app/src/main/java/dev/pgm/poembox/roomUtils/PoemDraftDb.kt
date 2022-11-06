package dev.pgm.poembox.roomUtils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [PoemDraft::class], version = 1)
abstract class PoemDraftDb : RoomDatabase() {
    abstract fun poemDraftDao(): PoemDraftDao

    companion object {
        @Volatile
        private var INSTANCE: PoemDraftDb? = null
        fun getDatabase(
            context: Context
        ): PoemDraftDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    PoemDraftDb::class.java,
                    "app_database"
                )
                    .createFromAsset("sqlite.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}