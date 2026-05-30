package dev.pgm.poembox.domain

object StreakCalculator {
    /**
     * Calculates the new streak value.
     * @param lastDate ISO date of the last write ("yyyy-MM-dd") or null if never written
     * @param today Today's ISO date
     * @param yesterday Yesterday's ISO date
     * @param current Current streak count
     * @return New streak value: same if already counted today, current+1 if consecutive, 1 if broken
     */
    fun compute(lastDate: String?, today: String, yesterday: String, current: Int): Int {
        if (lastDate == today) return current
        return if (lastDate == yesterday) current + 1 else 1
    }

    fun computeMaxStreak(current: Int, newStreak: Int): Int = maxOf(current, newStreak)
}
