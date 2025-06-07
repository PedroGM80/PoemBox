package dev.pgm.poembox.domain

import dev.pgm.poembox.data.PoemRepositoryImpl
import dev.pgm.poembox.domain.PoemRepository

object ServiceLocator {
    val repository: PoemRepository by lazy { PoemRepositoryImpl() }
}
