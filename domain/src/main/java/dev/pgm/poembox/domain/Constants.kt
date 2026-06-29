package dev.pgm.poembox.domain

object Constants {
    // Regex
    const val REGEX_WHITESPACE = "\\s+"
    const val REGEX_PUNCTUATION = "[.,;:]"
    const val REGEX_EMAIL = "[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-zA-Z.]{2,18}"
    
    // Formatting
    const val DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss"
    
    // Limits
    const val MAX_TITLE_LENGTH = 60
    const val TITLE_LENGTH_WARNING_THRESHOLD = 55
    const val MAX_AUTHOR_LENGTH = 40
    
    // Animation Durations
    const val ANIMATION_DURATION_DEFAULT = 600
    const val ANIMATION_DURATION_LONG = 1200
    
    // Others
    const val SYLLABLES_DEFAULT = "0"
    
    // Rendering Colors
    const val COLOR_PAPER_LIGHT = "#FAF3E0"
    const val COLOR_INK_DARK = "#3E2723"
    const val COLOR_MIDNIGHT_DEEP = "#1A1A2E"
    const val COLOR_STARLIGHT_LIGHT = "#F5F0E8"
    const val COLOR_ACCENT_LIGHT = "#8B6347"
    const val COLOR_ACCENT_DARK = "#9E77D0"
    const val COLOR_ACCENT_OVERLAY = "#C9A8E8"
}
