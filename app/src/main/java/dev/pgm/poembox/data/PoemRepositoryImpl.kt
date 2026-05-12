package dev.pgm.poembox.data

import dev.pgm.poembox.domain.PoemRepository
import javax.inject.Inject

/**
 * Poem box repository implementation
 *
 * @property draftDao
 * @property sheetDao
 */
class PoemRepositoryImpl @Inject constructor(
    private val draftDao: DraftDao,
    private val sheetDao: SheetDao
) : PoemRepository {

    /**
     * Insert draft
     *
     * @param draft
     */
    override suspend fun insertDraft(draft: Draft) {
        draftDao.addDraft(draft)
    }

    /**
     * Find draft by title
     *
     * @param titleDraft
     */
    override suspend fun findDraftByTitle(titleDraft: String) =
        draftDao.findByTitle(titleDraft)

    /** Get all sheet */
    override suspend fun getAllSheet() = sheetDao.getAllSheet()

    /**
     * Find sheet by date creation
     *
     * @param date
     */
    override suspend fun findSheetByDateCreation(date: String) = 
        sheetDao.findByDateCreation(date)

    /**
     * Delete sheet
     *
     * @param sheet
     */
    override suspend fun deleteSheet(sheet: Sheet) {
        sheetDao.deleteSheet(sheet)
    }

    /**
     * Delete draft
     *
     * @param draft
     */
    override suspend fun deleteDraft(draft: Draft) {
        draftDao.deleteDraft(draft)
    }

    /**
     * Add sheet
     *
     * @param sheet
     */
    override suspend fun addSheet(sheet: Sheet) {
        sheetDao.addSheet(sheet)
    }

    /**
     * Update note by title
     *
     * @param note
     * @param title
     */
    override suspend fun updateNoteByTitle(note: String, title: String) {
        draftDao.updateNoteByTitle(note, title)
    }
}