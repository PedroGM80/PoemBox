package dev.pgm.poembox.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pgm.poembox.data.PoemRepositoryImpl
import dev.pgm.poembox.domain.PoemRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPoemRepository(
        poemRepositoryImpl: PoemRepositoryImpl
    ): PoemRepository
}
