package dev.pgm.poembox.repository

/**
 * Poem box repository
 *
 * @constructor Create empty Poem box repository
 */
class PoemBoxRepository {
    internal val dataBase = PoemBoxDatabase.getDatabase()

    /**
     * Insert draft
     *
     * @param draft
     */
    suspend fun insertDraft(draft: Draft) {
        dataBase?.draftDao()?.addDraft(draft)
    }

    /**
     * Find draft by title
     *
     * @param titleDraft
     */
    suspend fun findDraftByTitle(titleDraft: String) =
        dataBase?.draftDao()?.findByTitle(titleDraft)

    /** Get all sheet */
    suspend fun getAllSheet() = dataBase?.sheetDao()?.getAllSheet()

    /**
     * Find sheet by date creation
     *
     * @param date
     */
    suspend fun findSheetByDateCreation(date: String) = dataBase?.sheetDao()
        ?.findByDateCreation(date)

    /**
     * Delete sheet
     *
     * @param sheet
     */
    suspend fun deleteSheet(sheet: Sheet) {
        dataBase
            ?.sheetDao()
            ?.deleteSheet(sheet)
    }

    /**
     * Delete draft
     *
     * @param draft
     */
    suspend fun deleteDraft(draft: Draft) {
        dataBase?.draftDao()?.deleteDraft(draft)
    }

    /**
     * Add sheet
     *
     * @param sheet
     */
    suspend fun addSheet(sheet: Sheet) {
        dataBase?.sheetDao()?.addSheet(sheet)
    }

    /**
     * Update note by title
     *
     * @param note
     * @param title
     */
    suspend fun updateNoteByTitle(note: String, title: String) {
        dataBase?.draftDao()?.updateNoteByTitle(note, title)
    }
}