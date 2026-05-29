package dev.pgm.poembox.ai

import dev.pgm.poembox.presentation.ai.DeviceAICapability
import dev.pgm.poembox.presentation.ai.DeviceAILevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure logic tests for DeviceAICapability.
 * levelLabel() and upgradeHint() are pure functions with no Android context dependency.
 */
class DeviceAICapabilityTest {

    // ── levelLabel ─────────────────────────────────────────────────────────────

    @Test
    fun `levelLabel LEVEL_RULES returns Asistente poetico`() {
        assertEquals("Asistente poético", DeviceAICapability.levelLabel(DeviceAILevel.LEVEL_RULES))
    }

    @Test
    fun `levelLabel LEVEL_LLM_INFERENCE returns IA local LLM`() {
        assertEquals("IA local (LLM)", DeviceAICapability.levelLabel(DeviceAILevel.LEVEL_LLM_INFERENCE))
    }

    @Test
    fun `levelLabel LEVEL_GEMINI_NANO returns Gemini Nano`() {
        assertEquals("Gemini Nano ✨", DeviceAICapability.levelLabel(DeviceAILevel.LEVEL_GEMINI_NANO))
    }

    @Test
    fun `levelLabel returns distinct label for each level`() {
        val labels = DeviceAILevel.entries.map { DeviceAICapability.levelLabel(it) }.toSet()
        assertEquals(
            "Each DeviceAILevel must have a unique label",
            DeviceAILevel.entries.size,
            labels.size
        )
    }

    @Test
    fun `levelLabel never returns blank string`() {
        DeviceAILevel.entries.forEach { level ->
            assertTrue(
                "levelLabel for $level must not be blank",
                DeviceAICapability.levelLabel(level).isNotBlank()
            )
        }
    }

    // ── upgradeHint ────────────────────────────────────────────────────────────

    @Test
    fun `upgradeHint LEVEL_RULES returns non-null string`() {
        val hint = DeviceAICapability.upgradeHint(DeviceAILevel.LEVEL_RULES)
        assertNotNull(hint)
    }

    @Test
    fun `upgradeHint LEVEL_RULES mentions 4 GB RAM requirement`() {
        val hint = DeviceAICapability.upgradeHint(DeviceAILevel.LEVEL_RULES)!!
        assertTrue(
            "Hint for LEVEL_RULES must mention '4 GB' but was: $hint",
            hint.contains("4 GB")
        )
    }

    @Test
    fun `upgradeHint LEVEL_LLM_INFERENCE returns non-null string`() {
        val hint = DeviceAICapability.upgradeHint(DeviceAILevel.LEVEL_LLM_INFERENCE)
        assertNotNull(hint)
    }

    @Test
    fun `upgradeHint LEVEL_LLM_INFERENCE mentions Gemini Nano`() {
        val hint = DeviceAICapability.upgradeHint(DeviceAILevel.LEVEL_LLM_INFERENCE)!!
        assertTrue(
            "Hint for LEVEL_LLM_INFERENCE must mention 'Gemini Nano' but was: $hint",
            hint.contains("Gemini Nano")
        )
    }

    @Test
    fun `upgradeHint LEVEL_GEMINI_NANO returns null already at maximum`() {
        assertNull(DeviceAICapability.upgradeHint(DeviceAILevel.LEVEL_GEMINI_NANO))
    }

    // ── Level ordering sanity ──────────────────────────────────────────────────

    @Test
    fun `enum entries are declared in ascending capability order`() {
        // LEVEL_RULES < LEVEL_LLM_INFERENCE < LEVEL_GEMINI_NANO
        val entries = DeviceAILevel.entries
        assertEquals(DeviceAILevel.LEVEL_RULES, entries[0])
        assertEquals(DeviceAILevel.LEVEL_LLM_INFERENCE, entries[1])
        assertEquals(DeviceAILevel.LEVEL_GEMINI_NANO, entries[2])
    }
}
