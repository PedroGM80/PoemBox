package dev.pgm.poembox.data.mapper

import dev.pgm.poembox.data.local.entities.DraftEntity
import dev.pgm.poembox.data.local.entities.SheetEntity
import dev.pgm.poembox.domain.model.Draft
import dev.pgm.poembox.domain.model.Sheet

fun DraftEntity.toDomain() = Draft(
    id = id,
    title = title,
    content = content,
    author = author,
    annotation = annotation,
    date = date,
    validationDate = validationDate
)

fun Draft.toEntity() = DraftEntity(
    id = id,
    title = title,
    content = content,
    author = author,
    annotation = annotation,
    date = date,
    validationDate = validationDate
)

fun SheetEntity.toDomain() = Sheet(
    id = id,
    draftTitle = draftTitle,
    validationDate = validationDate
)

fun Sheet.toEntity() = SheetEntity(
    id = id,
    draftTitle = draftTitle,
    validationDate = validationDate
)
