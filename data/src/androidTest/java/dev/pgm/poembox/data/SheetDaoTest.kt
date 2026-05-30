package dev.pgm.poembox.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dev.pgm.poembox.data.local.entities.SheetEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [SheetDao] using an in-memory Room database.
 *
 * Must run on a device or emulator via :data:connectedAndroidTest.
 * SheetDao uses OnConflictStrategy.IGNORE for inserts.
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
class SheetDaoTest {

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

    // ── insert and find by date ────────────────────────────────────────────────

    @Test
    fun insert_and_find_by_date() = runTest {
        val sheet = SheetEntity(draftTitle = "Oda al mar", validationDate = "2026-05-29")
        db.sheetDao().addSheet(sheet)

        val found = db.sheetDao().findByDateCreation("2026-05-29")

        assertNotNull(found)
        assertEquals("Oda al mar", found!!.draftTitle)
        assertEquals("2026-05-29", found.validationDate)
    }

    // ── getAllSheet returns all ─────────────────────────────────────────────────

    @Test
    fun getAllSheet_returns_all() = runTest {
        db.sheetDao().addSheet(SheetEntity(draftTitle = "Poema A", validationDate = "2026-05-01"))
        db.sheetDao().addSheet(SheetEntity(draftTitle = "Poema B", validationDate = "2026-05-02"))

        val all = db.sheetDao().getAllSheet()
        assertEquals(2, all.size)
    }

    // ── delete removes sheet ───────────────────────────────────────────────────

    @Test
    fun delete_removes_sheet() = runTest {
        val sheet = SheetEntity(draftTitle = "A borrar", validationDate = "2026-04-01")
        db.sheetDao().addSheet(sheet)

        val inserted = db.sheetDao().findByDateCreation("2026-04-01")
        assertNotNull(inserted)

        db.sheetDao().deleteSheet(inserted!!)

        val all = db.sheetDao().getAllSheet()
        assertTrue(all.isEmpty())
    }

    // ── insert duplicate is ignored (OnConflictStrategy.IGNORE) ──────────────

    @Test
    fun insert_duplicate_is_ignored() = runTest {
        // SheetEntity has autoGenerate PK — to test IGNORE we must insert a row that
        // already has the same primary key. We use updateSheet then re-insert to force a clash,
        // or we insert with an explicit duplicate id.
        val sheet = SheetEntity(id = 1, draftTitle = "Soneto", validationDate = "2026-03-15")
        db.sheetDao().addSheet(sheet)

        // Insert a second entity with the same explicit id — IGNORE means it won't replace
        val duplicate = SheetEntity(id = 1, draftTitle = "Otro título", validationDate = "2026-03-15")
        db.sheetDao().addSheet(duplicate)

        val all = db.sheetDao().getAllSheet()
        assertEquals(1, all.size)
        // Original row preserved (not replaced due to IGNORE)
        assertEquals("Soneto", all[0].draftTitle)
    }
}
