package dev.pgm.poembox.domain.usecase

import dev.pgm.poembox.domain.PoemRepository
import dev.pgm.poembox.domain.model.Draft
import javax.inject.Inject

class GetAllDraftsUseCase @Inject constructor(
    private val repository: PoemRepository
) {
    suspend operator fun invoke(): List<Draft> = repository.getAllDrafts()
}
