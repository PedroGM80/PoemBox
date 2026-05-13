package dev.pgm.poembox.domain.usecase

import dev.pgm.poembox.domain.PoemRepository
import dev.pgm.poembox.domain.model.Sheet
import javax.inject.Inject

class SaveSheetUseCase @Inject constructor(
    private val repository: PoemRepository
) {
    suspend operator fun invoke(sheet: Sheet) {
        repository.saveSheet(sheet)
    }
}
