package dev.pgm.poembox.presentation.content

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.pgm.poembox.R
import dev.pgm.poembox.domain.PoemUtils
import dev.pgm.poembox.domain.UseCase
import dev.pgm.poembox.domain.UtilitySyllables
import dev.pgm.poembox.presentation.MainActivity.Companion.POEM_TITLE
import dev.pgm.poembox.presentation.MainActivity.Companion.VALIDATE_STATUS
import dev.pgm.poembox.repository.PoemBoxDatabase
import dev.pgm.poembox.repository.Sheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Get key
 *
 * @param hashMap
 * @param target
 * @param K
 * @param V
 * @return
 */
fun <K, V> getKey(hashMap: Map<K, V>, target: V): K {
    return hashMap.filter { target == it.value }.keys.first()
}

/**
 * Find duplicates
 *
 * @param list
 * @param T
 * @return
 */
fun <T> findDuplicates(list: List<T>): Set<T> {
    val seen: MutableSet<T> = mutableSetOf()
    return list.filter { !seen.add(it) }.toSet()
}

/** Monitoring screen */
@Composable
fun MonitoringScreen() {
    val custom = remember { mutableStateOf(Color.Blue) }
    var countAssonant = 0
    var countConsonant = 0
    val poemTitle = remember { mutableStateOf("Poem don't charge title.") }
    val poemBody = remember { mutableStateOf("Poem don't charge body.") }
    val showDialog = remember { mutableStateOf(false) }
    val numberSyllablesInPoem = mutableListOf<String>()
    val consonantRime = mutableMapOf<String, String>()
    val assonantRime = mutableMapOf<String, String>()
    var poem by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var higherCount = 65
    var minorCount = 97
    var countLineBreak = 0
    val bodyDialog = remember { mutableStateOf("") }
    val titleDialog = remember { mutableStateOf("") }
    fun getMarkedRimeHigherArt(
        objectiveConsonant: String,
        objectiveAssonant: String,
        numberSyllablesInThePoemLine: Int
    ): String {
        if (!consonantRime.containsValue(objectiveConsonant) &&
            !assonantRime.containsValue(objectiveAssonant)
        ) {
            consonantRime[higherCount.toChar().toString()] = objectiveConsonant
            assonantRime[higherCount.toChar().toString()] = objectiveAssonant
            val check = higherCount.toChar().toString()
            higherCount++
            numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
            return buildString {
                append(numberSyllablesInThePoemLine)
                append(check)
            }
        } else if (consonantRime.containsValue(objectiveConsonant) &&
            !assonantRime.containsValue(objectiveAssonant)
        ) {
            assonantRime[higherCount.toChar().toString()] = objectiveAssonant
            countConsonant++
            val check = getKey(consonantRime, objectiveConsonant)
            numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
            return buildString {
                append(numberSyllablesInThePoemLine)
                append(check)
            }
        } else if (!consonantRime.containsValue(objectiveConsonant) &&
            assonantRime.containsValue(objectiveAssonant)
        ) {
            consonantRime[minorCount.toChar().toString()] = objectiveConsonant
            countAssonant++
            val check: String = getKey(assonantRime, objectiveAssonant)
            numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
            return buildString {
                append(numberSyllablesInThePoemLine)
                append(check)
            }
        } else {
            getKey(assonantRime, objectiveAssonant)
            val checkC: String = getKey(consonantRime, objectiveConsonant)
            numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
            return buildString {
                append(numberSyllablesInThePoemLine)
                append(checkC)
            }
        }
    }

    /**
     * Get marked rime minor art
     *
     * @param objectiveConsonant
     * @param objectiveAssonant
     * @param numberSyllablesInThePoemLine
     * @return marked rime minor art
     */
    fun getMarkedRimeMinorArt(
        objectiveConsonant: String,
        objectiveAssonant: String,
        numberSyllablesInThePoemLine: Int
    ): String {
        if (!consonantRime.containsValue(objectiveConsonant) &&
            !assonantRime.containsValue(objectiveAssonant)
        ) {
            consonantRime[minorCount.toChar().toString()] = objectiveConsonant
            assonantRime[minorCount.toChar().toString()] = objectiveAssonant
            val check = minorCount.toChar().toString()
            minorCount++
            numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
            return buildString {
                append(numberSyllablesInThePoemLine)
                append(check)
            }
        } else if (consonantRime.containsValue(objectiveConsonant) &&
            !assonantRime.containsValue(objectiveAssonant)
        ) {
            assonantRime[minorCount.toChar().toString()] = objectiveAssonant
            countConsonant++
            val check = getKey(consonantRime, objectiveConsonant)
            numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
            return buildString {
                append(numberSyllablesInThePoemLine)
                append(check)
            }
        } else if (!consonantRime.containsValue(objectiveConsonant) &&
            assonantRime.containsValue(objectiveAssonant)
        ) {
            consonantRime[minorCount.toChar().toString()] = objectiveConsonant
            countAssonant++
            val check: String = getKey(assonantRime, objectiveAssonant)
            numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
            return buildString {
                append(numberSyllablesInThePoemLine)
                append(check)
            }
        } else {
            getKey(assonantRime, objectiveAssonant)
            val checkC: String = getKey(consonantRime, objectiveConsonant)
            numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
            return buildString {
                append(numberSyllablesInThePoemLine)
                append(checkC)
            }
        }
    }

    /**
     * Get rime letter
     *
     * @param poemLine
     * @return rime letter
     */
    fun getRimeLetter(poemLine: String): String {

        if (poemLine.isEmpty()) {
            countLineBreak++
        } else {
            val utilitySyllables = UtilitySyllables()
            val poemUtils = PoemUtils()
            val cleanPoemLine = cleanPunctuationMarks(poemLine)
            val poemLineSyllables = utilitySyllables.getSyllables(cleanPoemLine)
            val countSinhalese = poemUtils.hasSinhalese(cleanPoemLine)
            val isAcute = poemUtils.isAcute(cleanPoemLine.split(" ").last())
            val isProparoxytone = poemUtils.isProparoxytone(cleanPoemLine.split(" ").last())
            val numberSyllablesInThePoemLine =
                poemLineSyllables.size + isAcute + isProparoxytone + countSinhalese
            val higherArt = (numberSyllablesInThePoemLine > 8)
            val minorArt = (numberSyllablesInThePoemLine <= 8)
            val words = cleanPoemLine.split(" ")
            val lastWord = words.last()
            val objectiveConsonant = utilitySyllables.getLastSyllable(lastWord)
            val objectiveAssonant = utilitySyllables.getLastVowel(lastWord)
            countLineBreak = 0
            when {
                higherArt -> {
                    return getMarkedRimeHigherArt(
                        objectiveConsonant,
                        objectiveAssonant,
                        numberSyllablesInThePoemLine
                    )
                }
                minorArt -> {
                    return getMarkedRimeMinorArt(
                        objectiveConsonant,
                        objectiveAssonant,
                        numberSyllablesInThePoemLine
                    )
                }
            }
        }
        return if (countLineBreak > 1) {
            ", "
        } else ""
    }

    /**
     * Poem rime iterator
     *
     * @param poem
     * @return All rime marked
     */
    fun poemRimeIterator(poem: String): String {
        var rimeChecks = ""
        val poemLines = poem.split("\n")
        for (line in poemLines) {
            rimeChecks += getRimeLetter(line)
        }
        return rimeChecks
    }

    /**
     * Get predominate number syllables
     *
     * @return predominate number syllables
     */
    fun getPredominateNumberSyllables(): String = findDuplicates(numberSyllablesInPoem).toString()
        .replace("[", "")
        .replace("]", "")

    Surface(color = MaterialTheme.colors.primary) {
        Box(Modifier.wrapContentSize(Alignment.Center)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.white))
                    .align(Alignment.TopCenter)
            ) {
                Text(
                    text = poemTitle.value,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .background(Color(Color.Yellow.value)),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
                Text(
                    text = poemBody.value,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(bottom = 20.dp, start = 10.dp, end = 10.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .background(Color(Color.Yellow.value)),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Button(
                    onClick = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                val draft = PoemBoxDatabase.getDatabase()?.draftDao()
                                    ?.findByTitle(POEM_TITLE)
                                if (draft != null) {
                                    poem = draft.title + "\n" + draft.draftContent
                                    val poemLines = poem.split("\n")
                                    poemTitle.value = poemLines[0]
                                    poemBody.value = poem.removePrefix(poemLines[0])
                                }
                            }

                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(5.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Transparent)
                ) { Text(text = "Charge poem") }
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Button(
                        onClick = {
                            val result = poemRimeIterator(poemBody.value)
                            val numberPredominate = getPredominateNumberSyllables()
                            bodyDialog.value = "$numberPredominate syllable verses predominate," +
                                    " following the schema: $result \n"
                            titleDialog.value = "Classification by number of syllables"
                            showDialog.value = true

                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = modifierButton()
                    ) {
                        Text(
                            text = """Classification by
                            | number of syllables""".trimMargin()
                        )
                    }
                    Button(
                        onClick = {
                            val numberStanza = PoemUtils().getNumberStanza(poemBody.value)
                            val numberVerse = PoemUtils().getNumberOfVerse(poemBody.value)
                            val numberVerseInStanza = numberVerse / numberStanza
                            titleDialog.value = "Classification by number of verses"
                            bodyDialog.value = "The poem is made up of $numberStanza stanzas.\n" +
                                    "$numberVerse verses, and has $numberVerseInStanza per stanza."
                            showDialog.value = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = modifierButton()
                    ) {
                        Text(
                            text = """Classification by
                            | number of verses""".trimMargin()
                        )
                    }

                }
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Button(
                        onClick = {
                            poemRimeIterator(poemBody.value)
                            Log.i("Assonant", assonantRime.toString())
                            Log.i("Consonant", consonantRime.toString())
                            val sizeAssonance = assonantRime.size
                            val sizeConsonant = consonantRime.size
                            var typeRime = "Undefined"
                            if (sizeAssonance < sizeConsonant) {
                                typeRime = "Assonance"
                            } else if (sizeAssonance > sizeConsonant) {
                                typeRime = "Consonance"
                            }
                            titleDialog.value = "Type of rhyme and meter"
                            bodyDialog.value = "The rime is $typeRime"
                            showDialog.value = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = modifierButton()
                    ) {
                        Text(
                            text = """Type of rhyme
                            | and meter""".trimMargin()
                        )
                    }
                    Button(
                        onClick = {
                            val enjambment = PoemUtils().getEnjambment(poemBody.value)
                            titleDialog.value = "Enjambment or stichomytia"
                            bodyDialog.value = "The rime is $enjambment"
                            showDialog.value = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = modifierButtonEnOrStc()
                    ) {
                        Text(
                            text = """Enjambment
                            | or stichomytia""".trimMargin()
                        )
                    }
                }
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Button(

                        onClick = {
                            if (custom.value == Color.Blue) {
                                scope.launch {
                                    val sheet = Sheet(poemTitle.value)
                                    withContext(Dispatchers.IO) {
                                        UseCase().addSheet(sheet)
                                    }
                                }
                                VALIDATE_STATUS++
                                custom.value = Color.Green
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = custom.value),
                        shape = RoundedCornerShape(10.dp),
                        modifier = modifierButtonValidate()
                    ) {
                        Text(
                            text = "Validate poem",
                            fontSize = 15.sp
                        )
                    }
                }
            }

        }
    }

    if (showDialog.value) {
        showDialog.value = alertDialogSample(titleDialog.value, bodyDialog.value)
    }
}

/**
 * Clean punctuation marks
 *
 * @param poemLine
 * @return poemLine cleaned
 */
fun cleanPunctuationMarks(poemLine: String): String {
    val cleanPoemLine = poemLine.replace(".", "")
    val cleanB = cleanPoemLine.replace(",", "")
    val cleanC = cleanB.replace(";", "")
    return cleanC.replace(":", "")
}

@SuppressLint("ComposableModifierFactory", "ModifierFactoryExtensionFunction")
@Composable
private fun modifierButton(): Modifier {
    return Modifier
        .width(200.dp)
        .height(100.dp)
        .padding(5.dp)
}

@SuppressLint("ComposableModifierFactory", "ModifierFactoryExtensionFunction")
@Composable
private fun modifierButtonEnOrStc(): Modifier {
    return Modifier
        .height(100.dp)
        .width(200.dp)
        .padding(5.dp)
}

@SuppressLint("ComposableModifierFactory", "ModifierFactoryExtensionFunction")
@Composable
private fun modifierButtonValidate(): Modifier {
    return Modifier
        .fillMaxWidth()
        .height(75.dp)
        .padding(10.dp)
}

/** Monitoring screen preview */
@Preview(showBackground = true)
@Composable
fun MonitoringScreenPreview() {
    MonitoringScreen()
}
