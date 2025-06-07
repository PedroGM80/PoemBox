package dev.pgm.poembox.data

import dev.pgm.poembox.domain.PoemRepository
import dev.pgm.poembox.data.Draft
import dev.pgm.poembox.data.Sheet

/**
 * Poem box repository
 *
 * @constructor Create empty Poem box repository
 */
class PoemRepositoryImpl : PoemRepository {
    internal val dataBase = PoemBoxDatabase.getDatabase()

    /**
     * Insert draft
     *
     * @param draft
     */
    override suspend fun insertDraft(draft: Draft) {
        dataBase?.draftDao()?.addDraft(draft)
    }

    /**
     * Find draft by title
     *
     * @param titleDraft
     */
    override suspend fun findDraftByTitle(titleDraft: String) =
        dataBase?.draftDao()?.findByTitle(titleDraft)

    /** Get all sheet */
    override suspend fun getAllSheet() = dataBase?.sheetDao()?.getAllSheet()

    /**
     * Find sheet by date creation
     *
     * @param date
     */
    override suspend fun findSheetByDateCreation(date: String) = dataBase?.sheetDao()
        ?.findByDateCreation(date)

    /**
     * Delete sheet
     *
     * @param sheet
     */
    override suspend fun deleteSheet(sheet: Sheet) {
        dataBase
            ?.sheetDao()
            ?.deleteSheet(sheet)
    }

    /**
     * Delete draft
     *
     * @param draft
     */
    override suspend fun deleteDraft(draft: Draft) {
        dataBase?.draftDao()?.deleteDraft(draft)
    }

    /**
     * Add sheet
     *
     * @param sheet
     */
    override suspend fun addSheet(sheet: Sheet) {
        dataBase?.sheetDao()?.addSheet(sheet)
    }

    /**
     * Update note by title
     *
     * @param note
     * @param title
     */
    override suspend fun updateNoteByTitle(note: String, title: String) {
        dataBase?.draftDao()?.updateNoteByTitle(note, title)
    }
}