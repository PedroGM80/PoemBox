package dev.pgm.poembox.screens

import dev.pgm.poembox.presentation.screens.FORM_EXAMPLES
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure data-integrity tests for FORM_EXAMPLES.
 * No Android context needed — operates only on the in-memory list.
 */
class FormsLibraryDataTest {

    // ── Collection size ───────────────────────────────────────────────────────

    @Test
    fun `FORM_EXAMPLES has exactly 7 items`() {
        assertEquals(7, FORM_EXAMPLES.size)
    }

    // ── Required fields non-blank ─────────────────────────────────────────────

    @Test
    fun `every form has a non-blank id`() {
        FORM_EXAMPLES.forEach { form ->
            assertTrue("id must not be blank, but form '${form.name}' has blank id", form.id.isNotBlank())
        }
    }

    @Test
    fun `every form has a non-blank name`() {
        FORM_EXAMPLES.forEach { form ->
            assertTrue("name must not be blank (id=${form.id})", form.name.isNotBlank())
        }
    }

    @Test
    fun `every form has a non-blank emoji`() {
        FORM_EXAMPLES.forEach { form ->
            assertTrue("emoji must not be blank (id=${form.id})", form.emoji.isNotBlank())
        }
    }

    @Test
    fun `every form has a non-blank structure description`() {
        FORM_EXAMPLES.forEach { form ->
            assertTrue("structure must not be blank (id=${form.id})", form.structure.isNotBlank())
        }
    }

    @Test
    fun `every form has a non-blank rhymeScheme`() {
        FORM_EXAMPLES.forEach { form ->
            assertTrue("rhymeScheme must not be blank (id=${form.id})", form.rhymeScheme.isNotBlank())
        }
    }

    @Test
    fun `every form has a non-blank author`() {
        FORM_EXAMPLES.forEach { form ->
            assertTrue("author must not be blank (id=${form.id})", form.author.isNotBlank())
        }
    }

    @Test
    fun `every form has a non-blank title`() {
        FORM_EXAMPLES.forEach { form ->
            assertTrue("title must not be blank (id=${form.id})", form.title.isNotBlank())
        }
    }

    @Test
    fun `every form has a non-blank poem`() {
        FORM_EXAMPLES.forEach { form ->
            assertTrue("poem must not be blank (id=${form.id})", form.poem.isNotBlank())
        }
    }

    // ── Unique ids ─────────────────────────────────────────────────────────────

    @Test
    fun `no duplicate ids in FORM_EXAMPLES`() {
        val ids = FORM_EXAMPLES.map { it.id }
        val distinct = ids.toSet()
        assertEquals(
            "Found duplicate ids: ${ids.groupBy { it }.filter { it.value.size > 1 }.keys}",
            distinct.size,
            ids.size
        )
    }

    // ── Multi-verse poems ──────────────────────────────────────────────────────

    @Test
    fun `each poem contains at least one newline making it multi-verse`() {
        FORM_EXAMPLES.forEach { form ->
            assertTrue(
                "poem '${form.title}' (id=${form.id}) must contain at least one newline",
                form.poem.contains('\n')
            )
        }
    }

    // ── Haiku: exactly 3 non-blank lines ──────────────────────────────────────

    @Test
    fun `haiku poem has exactly 3 non-blank lines`() {
        val haiku = FORM_EXAMPLES.first { it.id == "haiku" }
        val lines = haiku.poem.split('\n').filter { it.isNotBlank() }
        assertEquals(
            "Haiku must have exactly 3 non-blank lines but had ${lines.size}: $lines",
            3,
            lines.size
        )
    }

    // ── Soneto: exactly 14 non-blank lines ────────────────────────────────────

    @Test
    fun `soneto poem has exactly 14 non-blank lines`() {
        val soneto = FORM_EXAMPLES.first { it.id == "soneto" }
        val lines = soneto.poem.split('\n').filter { it.isNotBlank() }
        assertEquals(
            "Soneto must have exactly 14 non-blank lines but had ${lines.size}: $lines",
            14,
            lines.size
        )
    }

    // ── ID consistency checks ─────────────────────────────────────────────────

    @Test
    fun `specific expected ids are present`() {
        val expectedIds = setOf("haiku", "soneto", "redondilla", "cuarteta", "decima", "libre", "romance")
        val actualIds = FORM_EXAMPLES.map { it.id }.toSet()
        assertEquals(expectedIds, actualIds)
    }
}
