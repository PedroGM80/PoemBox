package dev.pgm.poembox.domain

import dev.pgm.poembox.domain.model.Draft
import dev.pgm.poembox.domain.model.Sheet

interface PoemRepository {
    suspend fun saveDraft(draft: Draft)
    suspend fun getDraftByTitle(title: String): Draft?
    suspend fun getAllSheets(): List<Sheet>
    suspend fun saveSheet(sheet: Sheet)
    suspend fun getSheetByDate(date: String): Sheet?
    suspend fun deleteSheet(sheet: Sheet)
    suspend fun deleteDraft(draft: Draft)
    suspend fun updateDraftAnnotation(title: String, annotation: String)
}
