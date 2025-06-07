package dev.pgm.poembox.domain

import dev.pgm.poembox.data.PoemRepositoryImpl

object ServiceLocator {
    val repository: PoemRepository by lazy { PoemRepositoryImpl() }
}
