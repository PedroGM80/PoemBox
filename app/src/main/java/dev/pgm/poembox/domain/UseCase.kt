package dev.pgm.poembox.domain

import dev.pgm.poembox.repository.Draft
import dev.pgm.poembox.repository.PoemBoxRepository
import dev.pgm.poembox.repository.Sheet

class UseCase {
    val repository = PoemBoxRepository()
    suspend fun addDraft(draft: Draft) {
        repository.insertDraft(draft)
    }

    suspend fun findDraftByTitle(titleDraft: String) = repository.findDraftByTitle(titleDraft)


    suspend fun getAllSheet() = repository.getAllSheet()

    suspend fun addSheet(sheet: Sheet) {
        repository.addSheet(sheet)
    }

    suspend fun findSheetByDateCreation(date: String) = repository.findSheetByDateCreation(date)

    suspend fun deleteSheet(sheet: Sheet) {
        repository.deleteSheet(sheet)
    }

    suspend fun deleteDraft(draft: Draft) {
        repository.deleteDraft(draft)
    }

    suspend fun updateNoteByTitle(note: String, title: String) {
        repository.updateNoteByTitle(note, title)
    }
}