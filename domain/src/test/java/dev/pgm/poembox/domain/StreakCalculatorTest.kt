package dev.pgm.poembox.domain

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [StreakCalculator].
 *
 * Pure function — no Android runtime needed, no coroutines.
 * Time: O(1) per call. Space: O(1) auxiliary.
 */
class StreakCalculatorTest {

    // ── compute() ─────────────────────────────────────────────────────────────

    @Test
    fun `same day returns same value — idempotent`() {
        val result = StreakCalculator.compute(
            lastDate = "2026-05-29",
            today = "2026-05-29",
            yesterday = "2026-05-28",
            current = 7
        )
        assertEquals(7, result)
    }

    @Test
    fun `yesterday increments streak by 1`() {
        val result = StreakCalculator.compute(
            lastDate = "2026-05-28",
            today = "2026-05-29",
            yesterday = "2026-05-28",
            current = 4
        )
        assertEquals(5, result)
    }

    @Test
    fun `two days ago resets streak to 1`() {
        val result = StreakCalculator.compute(
            lastDate = "2026-05-27",
            today = "2026-05-29",
            yesterday = "2026-05-28",
            current = 10
        )
        assertEquals(1, result)
    }

    @Test
    fun `null lastDate starts streak at 1`() {
        val result = StreakCalculator.compute(
            lastDate = null,
            today = "2026-05-29",
            yesterday = "2026-05-28",
            current = 0
        )
        assertEquals(1, result)
    }

    @Test
    fun `streak of 5 consecutive returns 6`() {
        val result = StreakCalculator.compute(
            lastDate = "2026-05-28",
            today = "2026-05-29",
            yesterday = "2026-05-28",
            current = 5
        )
        assertEquals(6, result)
    }

    // ── computeMaxStreak() ────────────────────────────────────────────────────

    @Test
    fun `computeMaxStreak keeps higher when current is greater`() {
        assertEquals(5, StreakCalculator.computeMaxStreak(current = 5, newStreak = 3))
    }

    @Test
    fun `computeMaxStreak updates to new max when newStreak is greater`() {
        assertEquals(7, StreakCalculator.computeMaxStreak(current = 3, newStreak = 7))
    }
}
