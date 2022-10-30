package dev.pgm.poembox.businessLogic

class Syllable private constructor(word: CharSequence) {

    companion object {
        fun process(seq: CharSequence): Syllable {
            val syllable = Syllable(seq)
            syllable.process()
            return syllable
        }

        fun getAccentedCharacter(syllable: Syllable): Char {
            return if (syllable.letterAccent > -1) {
                syllable.word[syllable.letterAccent]
            } else '0'
        }
    }

    private val word: CharSequence
    private val positions: ArrayList<Int>
    private val wordLength: Int
    private var stressedFound = false
    private var stressedPosition = 0
    private var letterAccent: Int

    val syllables: ArrayList<CharSequence>
        get() {
            val syllables = ArrayList<CharSequence>(positions.size)

            for (index in positions.indices) {

                val start = positions[index]
                var end = wordLength

                if (positions.size > index + 1) {

                    end = positions[index + 1]
                }

                val charSequence = word.subSequence(start, end)
                syllables.add(charSequence)
            }
            return syllables
        }

    init {
        letterAccent = -1
        this.word = word
        wordLength = word.length
        positions = ArrayList()
    }

    private fun process() {
        var numSyl = 0
        // Look for syllables in the word
        var index = 0

        while (index < wordLength) {

            positions.add(numSyl++, index)
            index = onset(index)
            index = nucleus(index)
            index = coda(index)
            if (stressedFound && stressedPosition == 0) {

                stressedPosition = numSyl // it marks the stressed syllable
            }
        }

        // If the word has not written accent, the stressed syllable is determined
        // according to the stress rules:
        // Stressed penultimate syllable
        // Stressed last syllable
        // Polysyllables
        when {
            !stressedFound -> {

                stressedPosition = when {
                    isMonoSyllable(numSyl) -> {
                        numSyl
                    }
                    else -> {

                        val endLetter = toLower(wordLength - 1)
                        if (!isConsonant(wordLength - 1) || endLetter == 'y' || endLetter == 'n' || endLetter == 's' && !isConsonant(
                                wordLength - 2)
                        ) numSyl - 1 // Stressed penultimate syllable
                        else numSyl // Stressed last syllable
                    }
                }
            }
        }
    }

    private fun isMonoSyllable(numSyl: Int) = numSyl < 2

    /**
     * Determines the onset of the current syllable whose begins in pos
     * and pos is changed to the follow position after end of onset.
     *
     * @param position
     * @return pos
     */
    private fun onset(position: Int): Int {

        var indexPosition = position
        var lastConsonant = 'a'

        while (indexPosition < wordLength && isConsonant(indexPosition) && toLower(indexPosition) != 'y') {

            lastConsonant = toLower(indexPosition)
            indexPosition++
        }
        // (q | g) + u (example: queso, gueto)
        indexPosition = isDigraph(indexPosition, lastConsonant)
        return indexPosition
    }

    private fun isDigraph(pos: Int, lastConsonant: Char): Int {
        var pos1 = pos
        when {
            pos1 < wordLength - 1 -> if (toLower(pos1) == 'u') {
                when (lastConsonant) {
                    'q' -> {
                        pos1++
                    }
                    'g' -> {
                        when (toLower(pos1 + 1)) {
                            in arrayOf('e', 'é', 'i', 'í') -> {
                                pos1++
                            }
                        }
                    }
                }
            } else if (toLower(pos1) == 'ü' && lastConsonant == 'g') {
                // The 'u' with diaeresis is added to the consonant
                pos1++
            }
        }
        return pos1
    }

    /**
     * Determines the nucleus of current syllable whose onset ending on pos - 1
     * and changes pos to the follow position behind of nucleus
     */
    private fun nucleus(position: Int): Int {
        // Saves the type of previous vowel when two vowels together exists
        var indexPosition = position
        var previous = 0

        // 0 = open ,1 = close with written accent ,2 = close
        if (indexPosition >= wordLength) return indexPosition // ¡¿Doesn't it have nucleus?!

        // Jumps a letter 'y' to the starting of nucleus, it is as consonant
        if (toLower(indexPosition) == 'y') indexPosition++

        // First vowel
        if (indexPosition < wordLength) {
            when {
                strongVowelIsAccent(indexPosition) -> {

                    letterAccent = indexPosition
                    stressedFound = true
                    previous = 0
                    indexPosition++
                }
                strongVowelIsNotAccent(indexPosition) -> {

                    previous = 0
                    indexPosition++
                }
                weakVowelIsAccent(indexPosition) -> {

                    letterAccent = indexPosition
                    indexPosition++
                    stressedFound = true
                    return indexPosition
                }
                weakVowelIsNotAccent(indexPosition) -> {
                    previous = 2
                    indexPosition++
                }
            }
        }

        // If 'h' has been inserted in the nucleus then it doesn't determine diphthong neither hiatus
        val pair = notHiatusOrDiphthong(indexPosition)
        val aitch = pair.first
        indexPosition = pair.second

        // Second vowel
        if (indexPosition < wordLength) {
            when (toLower(indexPosition)) {
                'á', 'à', 'é', 'è', 'ó', 'ò' -> {
                    letterAccent = indexPosition
                    if (previous != 0) {
                        stressedFound = true
                    }
                    if (previous == 0) {    // Two open-vowels don't form syllable
                        if (aitch) indexPosition--
                        return indexPosition
                    } else {
                        indexPosition++
                    }
                }
                'a', 'e', 'o' -> if (previous == 0) {
                    if (aitch) indexPosition--
                    return indexPosition
                } else {
                    indexPosition++
                }
                'í', 'ì', 'ú', 'ù' -> {
                    letterAccent = indexPosition
                    if (previous != 0) {  // Diphthong
                        stressedFound = true
                        indexPosition++
                    } else if (aitch) indexPosition--
                    return indexPosition
                }
                'i', 'u', 'ü' -> {
                    if (indexPosition < wordLength - 1) { // ¿Is there a third vowel?
                        if (!isConsonant(indexPosition + 1)) {
                            if (toLower(indexPosition - 1) == 'h') indexPosition--
                            return indexPosition
                        }
                    }

                    // Two equals close-vowels don't form diphthong
                    if (toLower(indexPosition) != toLower(indexPosition - 1)) indexPosition++
                    return indexPosition // It is a descendant diphthong
                }
            }
        }

        // Third vowel?
        if (indexPosition < wordLength) {
            if (toLower(indexPosition) == 'i' || toLower(indexPosition) == 'u') { // Close-vowel
                indexPosition++
                return indexPosition // It is a triphthong
            }
        }
        return indexPosition
    }

    private fun notHiatusOrDiphthong(indexPosition: Int): Pair<Boolean, Int> {
        var indexPosition1 = indexPosition
        var aitch = false
        if (indexPosition1 < wordLength) {
            if (toLower(indexPosition1) == 'h') {
                indexPosition1++
                aitch = true
            }
        }
        return Pair(aitch, indexPosition1)
    }

    private fun weakVowelIsAccent(indexPosition: Int) =
        toLower(indexPosition) == 'í' || toLower(indexPosition) == 'ì' || toLower(
            indexPosition
        ) == 'ú' || toLower(indexPosition) == 'ù' || toLower(indexPosition) == 'ü'

    private fun weakVowelIsNotAccent(indexPosition: Int) =
        toLower(indexPosition) == 'i' || toLower(indexPosition) == 'I' || toLower(
            indexPosition
        ) == 'u' || toLower(indexPosition) == 'U'

    private fun strongVowelIsNotAccent(indexPosition: Int) =
        toLower(indexPosition) == 'a' || toLower(indexPosition) == 'e' || toLower(
            indexPosition
        ) == 'o'

    private fun strongVowelIsAccent(indexPosition: Int) =
        toLower(indexPosition) == 'á' || toLower(indexPosition) == 'à' || toLower(
            indexPosition
        ) == 'é' || toLower(indexPosition) == 'è' || toLower(indexPosition) == 'ó' || toLower(
            indexPosition
        ) == 'ò'

    private fun coda(position: Int): Int {
        var indexPosition = position

        if (indexPosition >= wordLength || !isConsonant(indexPosition)) {
            return indexPosition // Syllable hasn't coda
        } else if (indexPosition == wordLength - 1) { // End of word
            indexPosition++
            return indexPosition
        }

        // If there is only a consonant between vowels, it belongs to the following syllable
        if (getIndexSyllableOwnsConsonant(indexPosition)) return indexPosition

        val firstConsonant = toLower(indexPosition)
        val secondConsonant = toLower(indexPosition + 1)

        // Has the syllable a third consecutive consonant?
        if (indexPosition < (wordLength - 2)) {
            val thirdConsonant = toLower(indexPosition + 2)
            if (!isConsonant(indexPosition + 2)) { // There isn't third consonant

                // The groups ll, ch and rr begin a syllable
                if (isDoubleL(firstConsonant, secondConsonant)) return indexPosition
                if (isCH(firstConsonant, secondConsonant)) return indexPosition
                if (isDoubleR(firstConsonant, secondConsonant)) return indexPosition

                // A consonant + 'h' begins a syllable, except for groups sh and rh
                if (firstConsonant != 's' && firstConsonant != 'r' && secondConsonant == 'h') return indexPosition

                /*            If the letter 'y' is preceded by the some
                                  letter 's', 'l', 'r', 'n' or 'c' then
                                  a new syllable begins in the previous consonant
                             else it begins in the letter 'y'*/

                if (secondConsonant == 'y') {
                    if (firstConsonant in arrayOf('s', 'l', 'r', 'n', 'c')) return indexPosition
                    indexPosition++
                    return indexPosition
                }

                // groups: gl - kl - bl - vl - pl - fl - tl
                if (firstConsonant in arrayOf(
                        'b',
                        'v',
                        'c',
                        'k',
                        'f',
                        'g',
                        'p',
                        't'
                    ) && secondConsonant == 'l'
                ) {
                    return indexPosition
                }

                // groups: gr - kr - dr - tr - br - vr - pr - fr

                if (firstConsonant in arrayOf(
                        'b',
                        'd',
                        'f',
                        'g',
                        'k',
                        'p',
                        't',
                        'v'
                    ) && (secondConsonant == 'r')
                ) {
                    return indexPosition
                }
                indexPosition++
                return indexPosition
            } else {
                // There is a third consonant
                if (indexPosition + 3 == wordLength) { // Three consonants to the end, foreign words?
                    if (secondConsonant == 'y') {  // 'y' as vowel
                        if (firstConsonant in arrayOf('c', 'l', 'n', 'r', 's')) return indexPosition
                    }
                    if (thirdConsonant == 'y') { // 'y' at the end as vowel with c2
                        indexPosition++
                    } else {  // Three consonants to the end, foreign words?
                        indexPosition += 3
                    }
                    return indexPosition
                }
                if (secondConsonant == 'y') { // 'y' as vowel
                    if (firstConsonant in arrayOf('s', 'l', 'r', 'n', 'c')) return indexPosition
                    indexPosition++
                    return indexPosition
                }

                // The groups pt, ct, cn, ps, mn, gn, ft, pn, cz, tz and ts begin a syllable
                // when preceded by other consonant
                if (secondConsonant == 'p' && thirdConsonant == 't' || secondConsonant == 'c' && thirdConsonant == 't' || secondConsonant == 'c' && thirdConsonant == 'n' || secondConsonant == 'p' && thirdConsonant == 's' || secondConsonant == 'm' && thirdConsonant == 'n' || secondConsonant == 'g' && thirdConsonant == 'n' || secondConsonant == 'f' && thirdConsonant == 't' || secondConsonant == 'p' && thirdConsonant == 'n' || secondConsonant == 'c' && thirdConsonant == 'z' || secondConsonant == 't' && thirdConsonant == 's') {
                    indexPosition++
                    return indexPosition
                }
                if ((thirdConsonant == 'l' || thirdConsonant == 'r') || ((secondConsonant == 'c') && (thirdConsonant == 'h')) || (thirdConsonant == 'y')) {                   // 'y' as vowel
                    indexPosition++ // Following syllable begins in c2
                } else indexPosition += 2 // c3 begins the following syllable
            }
            return indexPosition
        } else {
            if (secondConsonant == 'y') return indexPosition
            indexPosition += 2 // The word ends with two consonants
            return indexPosition
        }
    }

    private fun getIndexSyllableOwnsConsonant(indexPosition: Int): Boolean {
        if (!isConsonant(indexPosition + 1)) {
            return true
        }
        return false
    }

    private fun isDoubleR(c1: Char, c2: Char): Boolean {
        if (c1 == 'r' && c2 == 'r') return true
        return false
    }

    private fun isCH(c1: Char, c2: Char): Boolean {
        if (c1 == 'c' && c2 == 'h') return true
        return false
    }

    private fun isDoubleL(c1: Char, c2: Char): Boolean {
        if (c1 == 'l' && c2 == 'l') return true
        return false
    }

    private fun toLower(pos: Int): Char {
        return word[pos].lowercaseChar()
    }

    private fun isConsonant(pos: Int): Boolean {
        return when (word[pos]) {
            'a', 'á', 'A', 'Á', 'à', 'À', 'e', 'é', 'E', 'É', 'è', 'È', 'í', 'Í', 'ì', 'Ì', 'o', 'ó', 'O', 'Ó', 'ò', 'Ò', 'ú', 'Ú', 'ù', 'Ù', 'i', 'I', 'u', 'U', 'ü', 'Ü' -> false
            else -> true
        }
    }

}

fun main() {
    val myTest = Syllable.process("esternocleidomastoideo")
    println(myTest.syllables)
}