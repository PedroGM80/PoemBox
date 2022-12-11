package dev.pgm.poembox.repository

class PoemBoxRepository {
    internal val dataBase = PoemBoxDatabase.getDatabase()

    suspend fun insertDraft(draft: Draft) {
        dataBase?.draftDao()?.addDraft(draft)
    }

    suspend fun findDraftByTitle(titleDraft: String) =
        dataBase?.draftDao()?.findByTitle(titleDraft)

    suspend fun getAllSheet() = dataBase?.sheetDao()?.getAllSheet()

    suspend fun findSheetByDateCreation(date: String) = dataBase?.sheetDao()
        ?.findByDateCreation(date)

    suspend fun deleteSheet(sheet: Sheet) {
        dataBase
            ?.sheetDao()
            ?.deleteSheet(sheet)
    }

    suspend fun deleteDraft(draft: Draft) {
        dataBase?.draftDao()?.deleteDraft(draft)
    }

    suspend fun addSheet(sheet: Sheet) {
        dataBase?.sheetDao()?.addSheet(sheet)
    }

    suspend fun updateNoteByTitle(note: String, title: String) {
        dataBase?.draftDao()?.updateNoteByTitle(note, title)
    }
}