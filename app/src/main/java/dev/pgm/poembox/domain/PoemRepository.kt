package dev.pgm.poembox.domain

import dev.pgm.poembox.data.Draft
import dev.pgm.poembox.data.Sheet

interface PoemRepository {
    suspend fun insertDraft(draft: Draft)
    suspend fun findDraftByTitle(titleDraft: String): Draft?
    suspend fun getAllSheet(): MutableList<Sheet>?
    suspend fun addSheet(sheet: Sheet)
    suspend fun findSheetByDateCreation(date: String): Sheet?
    suspend fun deleteSheet(sheet: Sheet)
    suspend fun deleteDraft(draft: Draft)
    suspend fun updateNoteByTitle(note: String, title: String)
}
