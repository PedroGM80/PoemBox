package dev.pgm.poembox.domain

import dev.pgm.poembox.repository.Draft
import dev.pgm.poembox.repository.PoemBoxRepository
import dev.pgm.poembox.repository.Sheet

/**
 * Use case
 *
 * @constructor Create empty Use case
 */
class UseCase {
    val repository = PoemBoxRepository()

    /**
     * Add draft
     *
     * @param draft
     */
    suspend fun addDraft(draft: Draft) {
        repository.insertDraft(draft)
    }

    /**
     * Find draft by title
     *
     * @param titleDraft
     */
    suspend fun findDraftByTitle(titleDraft: String) = repository.findDraftByTitle(titleDraft)


    /** Get all sheet */
    suspend fun getAllSheet() = repository.getAllSheet()

    /**
     * Add sheet
     *
     * @param sheet
     */
    suspend fun addSheet(sheet: Sheet) {
        repository.addSheet(sheet)
    }

    /**
     * Find sheet by date creation
     *
     * @param date
     */
    suspend fun findSheetByDateCreation(date: String) = repository.findSheetByDateCreation(date)

    /**
     * Delete sheet
     *
     * @param sheet
     */
    suspend fun deleteSheet(sheet: Sheet) {
        repository.deleteSheet(sheet)
    }

    /**
     * Delete draft
     *
     * @param draft
     */
    suspend fun deleteDraft(draft: Draft) {
        repository.deleteDraft(draft)
    }

    /**
     * Update note by title
     *
     * @param note
     * @param title
     */
    suspend fun updateNoteByTitle(note: String, title: String) {
        repository.updateNoteByTitle(note, title)
    }
}