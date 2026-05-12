package dev.pgm.poembox.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.pgm.poembox.data.PoemBoxDatabase
import dev.pgm.poembox.data.local.dao.DraftDao
import dev.pgm.poembox.data.local.dao.SheetDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PoemBoxDatabase {
        return Room.databaseBuilder(
            context,
            PoemBoxDatabase::class.java,
            "poem_box_database"
        ).build()
    }

    @Provides
    fun provideDraftDao(database: PoemBoxDatabase): DraftDao {
        return database.draftDao()
    }

    @Provides
    fun provideSheetDao(database: PoemBoxDatabase): SheetDao {
        return database.sheetDao()
    }
}
