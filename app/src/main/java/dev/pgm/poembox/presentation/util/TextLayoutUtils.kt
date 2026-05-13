package dev.pgm.poembox.presentation.util

import android.graphics.Paint

internal fun wrapTextIntoLines(text: String, paint: Paint, maxWidth: Float): List<String> {
    val result = mutableListOf<String>()
    text.split("\n").forEach { paragraph ->
        if (paragraph.isBlank()) {
            result.add("")
            return@forEach
        }
        var line = ""
        for (word in paragraph.split(" ")) {
            val candidate = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(candidate) <= maxWidth) {
                line = candidate
            } else {
                if (line.isNotEmpty()) result.add(line)
                line = word
            }
        }
        if (line.isNotEmpty()) result.add(line)
    }
    return result
}
