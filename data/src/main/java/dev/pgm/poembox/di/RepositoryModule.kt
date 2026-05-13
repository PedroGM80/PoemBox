package dev.pgm.poembox.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pgm.poembox.data.PoemRepositoryImpl
import dev.pgm.poembox.data.local.UserSessionManager
import dev.pgm.poembox.domain.PoemRepository
import dev.pgm.poembox.domain.SessionManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPoemRepository(
        poemRepositoryImpl: PoemRepositoryImpl
    ): PoemRepository

    @Binds
    @Singleton
    abstract fun bindSessionManager(
        userSessionManager: UserSessionManager
    ): SessionManager
}
