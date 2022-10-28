package dev.pgm.poembox.businessLogic

class Syllables private constructor(word: CharSequence) {

    private val word: CharSequence
    private val positions: ArrayList<Int>
    private val wordLength: Int

    private var stressedFound = false
    private var stressedPosition = 0
    private var letterAccent: Int


    init {
        letterAccent = -1
        this.word = word
        this.wordLength = word.length
        positions = ArrayList()
    }

    val syllables: ArrayList<CharSequence>
        get() {
            val syllables = ArrayList<CharSequence>(positions.size)
            for (index in positions.indices) {

                val start = positions[index]
                var end = wordLength

                if (positions.size > index + 1) {

                    end = positions[index + 1]
                }
                val seq = word.subSequence(start, end)
                syllables.add(seq)
            }
            return syllables
        }


    private fun process() {

        var numSyl = 0
        var index = 0

        while (index < wordLength) {
            positions.add(numSyl++, index)
            index = onset(index)
            index = nucleus(index)
            index = coda(index)
            when {
                stressedFound && stressedPosition == 0 -> {

                    stressedPosition = numSyl // it marks the stressed syllable
                }
            }
        }

        when {
            !stressedFound -> {
                stressedPosition =
                    when {
                        numSyl < 2 // Stressed last syllable// Stressed penultimate syllable// Polysyllables
                        -> {
                            numSyl // Monosyllables
                        }
                        else -> {
                            // Polysyllables
                            val endLetter = toLower(wordLength - 1)
                            when {
                                !isConsonant(wordLength - 1) || endLetter == 'y' ||
                                        endLetter == 'n' || (endLetter == 's')
                                        && !isConsonant(wordLength - 2) -> {
                                    numSyl - 1 // Stressed penultimate syllable
                                }
                                else -> {

                                    numSyl // Stressed last syllable
                                }
                            }
                        }
                    }
            }
        }
    }

    /**
     * Determines the onset of the current syllable whose begins in pos
     * and pos is changed to the follow position after end of onset.
     *
     * @param positionIn
     * @return pos
     */
    private fun onset(positionIn: Int): Int {
        var position = positionIn
        var lastConsonant = 'a'

        while (position < wordLength && isConsonant(position) && toLower(position) != 'y') {
            lastConsonant = toLower(position)
            position++
        }

        // (q | g) + u (example: queso, gueto)
        when {
            position < wordLength - 1 -> {
                when {
                    toLower(position) == 'u' -> {
                        when (lastConsonant) {
                            'q' -> {

                                position++
                            }
                            'g' -> {

                                val letter = toLower(position + 1)
                                if (letter in arrayOf('e', 'é', 'i', 'í')) {

                                    position++
                                }
                            }
                        }
                    }
                    toLower(position) == 'ü' && lastConsonant == 'g' -> {
                        // The 'u' with diaeresis is added to the consonant
                        position++
                    }
                }
            }
        }
        return position
    }

    /**
     * Determines the nucleus of current syllable whose onset ending on pos - 1
     * and changes pos to the follow position behind of nucleus
     */
    private fun nucleus(positionIn: Int): Int {
        // Saves the type of previous vowel when two vowels together exists
        var position = positionIn
        var previous = 0
        // 0 = open
        // 1 = close with written accent
        // 2 = close
        when {
            position >= wordLength -> return position
            toLower(position) == 'y' -> position++
        }

        // First vowel
        when {
            position < wordLength -> {
                when (toLower(position)) {
                    'á', 'à', 'é', 'è', 'ó', 'ò' -> {
                        letterAccent = position
                        stressedFound = true
                        previous = 0
                        position++
                    }
                    'a', 'e', 'o' -> {
                        previous = 0
                        position++
                    }
                    'í', 'ì', 'ú', 'ù', 'ü' -> {
                        letterAccent = position
                        position++
                        stressedFound = true
                        return position
                    }
                    'i', 'I', 'u', 'U' -> {
                        previous = 2
                        position++
                    }
                }
            }
        }

        // If 'h' has been inserted in the nucleus then it doesn't determine diphthong neither hiatus
        var aitch = false
        when {
            position < wordLength -> {
                when {
                    toLower(position) == 'h' -> {
                        position++
                        aitch = true
                    }
                }
            }
        }

        // Second vowel
        when {
            position < wordLength -> {
                when (toLower(position)) {
                    'á', 'à', 'é', 'è', 'ó', 'ò' -> {
                        letterAccent = position
                        when {
                            previous != 0 -> {
                                stressedFound = true
                            }
                        }
                        when (previous) {
                            0 -> {    // Two open-vowels don't form syllable
                                if (aitch) position--
                                return position
                            }
                            else -> {
                                position++
                            }
                        }
                    }
                    'a', 'e', 'o' -> if (previous == 0) {
                        when {
                            aitch -> position--
                        }
                        return position
                    } else {
                        position++
                    }
                    'í', 'ì', 'ú', 'ù' -> {
                        letterAccent = position
                        when {
                            previous != 0 -> {  // Diphthong
                                stressedFound = true
                                position++
                            }
                            aitch -> position--
                        }
                        return position
                    }
                    'i', 'u', 'ü' -> {
                        when {
                            position < wordLength - 1 -> { // ¿Is there a third vowel?
                                when {
                                    !isConsonant(position + 1) -> {
                                        when {
                                            toLower(position - 1) == 'h' -> position--
                                        }
                                        return position
                                    }
                                }
                            }
                        }

                        // Two equals close-vowels don't form diphthong
                        when {
                            toLower(position) != toLower(position - 1) -> position++
                        }
                        return position // It is a descendant diphthong
                    }
                }
            }
        }

        // Third vowel?
        when {
            position < wordLength -> {
                when {
                    toLower(position) == 'i' || toLower(position) == 'u' -> { // Close-vowel
                        position++
                        return position // It is a triphthong
                    }
                }
            }
        }
        return position
    }

    private fun coda(positionIn: Int): Int {
        var position = positionIn
        if (position >= wordLength || !isConsonant(position)) {
            return position // Syllable hasn't coda
        } else if (position == wordLength - 1) { // End of word
            position++
            return position
        }

        // If there is only a consonant between vowels, it belongs to the following syllable
        if (!isConsonant(position + 1)) return position
        val firstConsonant = toLower(position)
        val secondConsonant = toLower(position + 1)

        // Has the syllable a third consecutive consonant?
        if (position < wordLength - 2) {
            val thirdConsonant = toLower(position + 2)
            if (!isConsonant(position + 2)) { // There isn't third consonant
                // The groups ll, ch and rr begin a syllable
                when {
                    firstConsonant == 'l' && secondConsonant == 'l' -> return position
                    firstConsonant == 'c' && secondConsonant == 'h' -> return position
                    firstConsonant == 'r' && secondConsonant == 'r' -> return position

                    // A consonant + 'h' begins a syllable, except for groups sh and rh
                    firstConsonant != 's' && firstConsonant != 'r' && secondConsonant == 'h' -> return position

                    /*
                        If the letter 'y' is preceded by the some letter 's', 'l', 'r', 'n' or 'c' then
                       a new syllable begins in the previous consonant else it begins in the letter 'y'
                    */
                    secondConsonant == 'y' -> {
                        if (firstConsonant in arrayOf('s', 'l', 'r', 'n', 'c')) return position
                        position++
                        return position
                    }

                    // groups: gl - kl - bl - vl - pl - fl - tl
                    firstConsonant in arrayOf('b', 'v', 'c', 'k', 'f', 'g', 'p', 't') &&
                            secondConsonant == 'l'
                    -> {
                        return position
                    }

                    // groups: gr - kr - dr - tr - br - vr - pr - fr
                    firstConsonant in arrayOf('b', 'd', 'f', 'g', 'k', 'p', 't', 'v') &&
                            (secondConsonant == 'r')
                    -> {
                        return position
                    }
                    else -> {
                        position++
                        return position
                    }
                }

            } else { // There is a third consonant
                if (position + 3 == wordLength) { // Three consonants to the end, foreign words?
                    if (secondConsonant == 'y') {  // 'y' as vowel
                        if (firstConsonant in arrayOf('c', 'l', 'n', 'r', 's')) {
                            return position
                        }
                    }
                    if (thirdConsonant == 'y') { // 'y' at the end as vowel with c2

                        position++
                    } else {  // Three consonants to the end, foreign words?
                        position += 3
                    }
                    return position
                }
                if (secondConsonant == 'y') { // 'y' as vowel
                    if (firstConsonant in arrayOf('s', 'l', 'r', 'n', 'c')) return position
                    position++
                    return position
                }

                // The groups pt, ct, cn, ps, mn, gn, ft, pn, cz, tz and ts begin a syllable
                // when preceded by other consonant
                if (secondConsonant == 'p' && thirdConsonant == 't' || secondConsonant == 'c' && thirdConsonant == 't' || secondConsonant == 'c' && thirdConsonant == 'n' || secondConsonant == 'p' && thirdConsonant == 's' || secondConsonant == 'm' && thirdConsonant == 'n' || secondConsonant == 'g' && thirdConsonant == 'n' || secondConsonant == 'f' && thirdConsonant == 't' || secondConsonant == 'p' && thirdConsonant == 'n' || secondConsonant == 'c' && thirdConsonant == 'z' || secondConsonant == 't' && thirdConsonant == 's') {
                    position++
                    return position
                }
                if ((thirdConsonant == 'l' || thirdConsonant == 'r') || ((secondConsonant == 'c') && (thirdConsonant == 'h')) || (thirdConsonant == 'y')) {                   // 'y' as vowel
                    position++ // Following syllable begins in secondConsonant
                } else position += 2 // thirdConsonant begins the following syllable
            }
        } else {
            if (secondConsonant == 'y') return position
            position += 2 // The word ends with two consonants
        }
        return position
    }



    private fun toLower(position: Int): Char {
        return word[position].lowercaseChar()
    }

    private fun isConsonant(pos: Int): Boolean {
        return word[pos] in arrayOf(
            'a', 'á', 'A', 'Á', 'à', 'À', 'e', 'é', 'E', 'É', 'è', 'È', 'í', 'Í', 'ì', 'Ì',
            'o', 'ó', 'O', 'Ó', 'ò', 'Ò', 'ú', 'Ú', 'ù', 'Ù', 'i', 'I', 'u', 'U', 'ü', 'Ü')
    }

    companion object {
        fun process(seq: CharSequence): Syllables {
            val syllables = Syllables(seq)
            syllables.process()
            return syllables
        }


        fun getAccentedCharacter(syllables: Syllables): Char = if (syllables.letterAccent > -1) {

            syllables.word[syllables.letterAccent]
        } else {
            '0'
        }
    }
}

fun main() {
    val myTest = Syllables.process("esternocleidomastoideo")
    val myTest2 = Syllables.process("queso")
    println(myTest.syllables)
    println(myTest2.syllables)
}