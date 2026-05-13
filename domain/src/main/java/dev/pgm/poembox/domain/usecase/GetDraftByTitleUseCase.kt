package dev.pgm.poembox.domain.usecase

import dev.pgm.poembox.domain.PoemRepository
import dev.pgm.poembox.domain.model.Draft
import javax.inject.Inject

class GetDraftByTitleUseCase @Inject constructor(
    private val repository: PoemRepository
) {
    suspend operator fun invoke(title: String): Draft? {
        return repository.getDraftByTitle(title)
    }
}
