package dev.pgm.poembox.data

import dev.pgm.poembox.data.local.dao.DraftDao
import dev.pgm.poembox.data.local.dao.SheetDao
import dev.pgm.poembox.data.mapper.toDomain
import dev.pgm.poembox.data.mapper.toEntity
import dev.pgm.poembox.domain.PoemRepository
import dev.pgm.poembox.domain.model.Draft
import dev.pgm.poembox.domain.model.Sheet
import javax.inject.Inject

class PoemRepositoryImpl @Inject constructor(
    private val draftDao: DraftDao,
    private val sheetDao: SheetDao
) : PoemRepository {

    override suspend fun saveDraft(draft: Draft) {
        draftDao.addDraft(draft.toEntity())
    }

    override suspend fun getDraftByTitle(title: String): Draft? {
        return draftDao.findByTitle(title)?.toDomain()
    }

    override suspend fun getAllDrafts(): List<Draft> {
        return draftDao.getAllDrafts().map { it.toDomain() }
    }

    override suspend fun getAllSheets(): List<Sheet> {
        return sheetDao.getAllSheet().map { it.toDomain() }
    }

    override suspend fun saveSheet(sheet: Sheet) {
        sheetDao.addSheet(sheet.toEntity())
    }

    override suspend fun getSheetByDate(date: String): Sheet? {
        return sheetDao.findByDateCreation(date)?.toDomain()
    }

    override suspend fun deleteSheet(sheet: Sheet) {
        sheetDao.deleteSheet(sheet.toEntity())
    }

    override suspend fun deleteDraft(draft: Draft) {
        draftDao.deleteDraft(draft.toEntity())
    }

    override suspend fun updateDraftAnnotation(title: String, annotation: String) {
        draftDao.updateNoteByTitle(annotation, title)
    }
}
