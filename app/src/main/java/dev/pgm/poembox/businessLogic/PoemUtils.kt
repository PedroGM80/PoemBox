package dev.pgm.poembox.businessLogic

fun countLinesWrote(text: String): Int {// This function return the number of lines wrote in the string
    var lines = 0
    text.forEach { element ->
        if (element == '\n') {
            lines++
        }
    }
    return lines
}

fun countEmptyLines(text: String): Int {// This function return the number of empty lines in the text field
    var emptyLines = 0
    text.forEach { element ->
        if (element == '\n') {
            if (text.substring(text.indexOf(element) + 1, text.indexOf(element) + 2) == " ") {
                emptyLines++
            }
        }
    }
    return emptyLines
}

fun countWords(text: String): Int {// This function return the number of words wrote in the text field
    var words = 0
    text.forEach { element ->
        if (element == ' ') {
            words++
        }
    }
    return words
}

fun countCharacters(text: String): Int {// This function return the number of characters wrote in the text field
    var characters = 0
    text.forEach { _ -> characters++ }
    return characters
}
