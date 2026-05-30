package dev.pgm.poembox.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dev.pgm.poembox.data.local.entities.DraftEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [DraftDao] using an in-memory Room database.
 *
 * Must run on a device or emulator via :data:connectedAndroidTest.
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
class DraftDaoTest {

    private lateinit var db: PoemBoxDatabase

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PoemBoxDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    // ── insert and find by title ───────────────────────────────────────────────

    @Test
    fun insert_and_find_by_title() = runTest {
        val draft = DraftEntity(title = "Oda al mar", content = "Las olas rompen", author = "Lorca")
        db.draftDao().addDraft(draft)

        val found = db.draftDao().findByTitle("Oda al mar")

        assertNotNull(found)
        assertEquals("Oda al mar", found!!.title)
        assertEquals("Las olas rompen", found.content)
        assertEquals("Lorca", found.author)
    }

    // ── insert duplicate title replaces ────────────────────────────────────────

    @Test
    fun insert_duplicate_title_replaces() = runTest {
        val first = DraftEntity(title = "Soneto", content = "Primera versión", author = "Neruda")
        val second = DraftEntity(title = "Soneto", content = "Segunda versión", author = "Neruda")

        db.draftDao().addDraft(first)
        db.draftDao().addDraft(second)

        val all = db.draftDao().getAllDrafts()
        assertEquals(1, all.size)
        assertEquals("Segunda versión", all[0].content)
    }

    // ── getAllDrafts returns all ────────────────────────────────────────────────

    @Test
    fun getAllDrafts_returns_all() = runTest {
        db.draftDao().addDraft(DraftEntity(title = "Poema 1", content = "Verso 1", author = "A"))
        db.draftDao().addDraft(DraftEntity(title = "Poema 2", content = "Verso 2", author = "A"))
        db.draftDao().addDraft(DraftEntity(title = "Poema 3", content = "Verso 3", author = "A"))

        val all = db.draftDao().getAllDrafts()
        assertEquals(3, all.size)
    }

    // ── delete removes draft ───────────────────────────────────────────────────

    @Test
    fun delete_removes_draft() = runTest {
        val draft = DraftEntity(title = "A borrar", content = "Contenido", author = "B")
        db.draftDao().addDraft(draft)

        val inserted = db.draftDao().findByTitle("A borrar")
        assertNotNull(inserted)

        db.draftDao().deleteDraft(inserted!!)

        val afterDelete = db.draftDao().findByTitle("A borrar")
        assertNull(afterDelete)
    }

    // ── updateNoteByTitle updates annotation ──────────────────────────────────

    @Test
    fun updateNoteByTitle_updates_annotation() = runTest {
        db.draftDao().addDraft(
            DraftEntity(title = "Con nota", content = "Contenido", author = "C", annotation = "")
        )

        db.draftDao().updateNoteByTitle("Nueva anotación", "Con nota")

        val updated = db.draftDao().findByTitle("Con nota")
        assertNotNull(updated)
        assertEquals("Nueva anotación", updated!!.annotation)
    }

    // ── findById returns correct ───────────────────────────────────────────────

    @Test
    fun findById_returns_correct_draft() = runTest {
        val draft = DraftEntity(title = "Por ID", content = "Contenido ID", author = "D")
        db.draftDao().addDraft(draft)

        val inserted = db.draftDao().findByTitle("Por ID")
        assertNotNull(inserted)

        val byId = db.draftDao().findById(inserted!!.id)
        assertNotNull(byId)
        assertEquals("Por ID", byId!!.title)
    }
}
