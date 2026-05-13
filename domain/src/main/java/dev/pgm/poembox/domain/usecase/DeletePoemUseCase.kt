package dev.pgm.poembox.domain.usecase

import dev.pgm.poembox.domain.PoemRepository
import dev.pgm.poembox.domain.model.Draft
import dev.pgm.poembox.domain.model.Sheet
import javax.inject.Inject

class DeletePoemUseCase @Inject constructor(
    private val repository: PoemRepository
) {
    suspend operator fun invoke(title: String, date: String) {
        val sheet = repository.getSheetByDate(date)
        val draft = repository.getDraftByTitle(title)
        
        if (sheet != null) repository.deleteSheet(sheet)
        if (draft != null) repository.deleteDraft(draft)
    }
}
