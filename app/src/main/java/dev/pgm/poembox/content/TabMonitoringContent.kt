package dev.pgm.poembox.content

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
import dev.pgm.poembox.MainActivity.Companion.POEM_TITLE
import dev.pgm.poembox.R
import dev.pgm.poembox.businessLogic.PoemUtils
import dev.pgm.poembox.businessLogic.UtilitySyllables
import dev.pgm.poembox.roomUtils.PoemBoxDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun <K, V> getKey(hashMap: Map<K, V>, target: V): K {
    return hashMap.filter { target == it.value }.keys.first()
}

fun <T> findDuplicates(list: List<T>): Set<T> {
    val seen: MutableSet<T> = mutableSetOf()
    return list.filter { !seen.add(it) }.toSet()
}

@Composable
fun MonitoringScreen() {
    val poemTitle = remember { mutableStateOf("Poem don't charge title.") }
    var poemBody = remember { mutableStateOf("Poem don't charge body.") }
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
        } else {

            val checkA: String? = getKey(assonantRime, objectiveAssonant) ?: null
            val checkC = getKey(consonantRime, objectiveConsonant) ?: checkA
            numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
            return buildString {
                append(numberSyllablesInThePoemLine)
                append(checkC)
            }
        }
    }

    fun getMarkedRimeMinorArt(
        objectiveConsonant: String,
        objectiveAssonant: String,
        numberSyllablesInThePoemLine: Int
    ): String {
        if (!consonantRime.containsValue(objectiveConsonant)
            && !assonantRime.containsValue(objectiveAssonant)
        ) {
            consonantRime[higherCount.toChar().toString()] = objectiveConsonant
            if (!assonantRime.containsValue(objectiveAssonant)) {

                assonantRime[higherCount.toChar().toString()] = objectiveAssonant
            }
            minorCount++
            val check = higherCount.toChar().toString()
            numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
            return buildString {
                append(numberSyllablesInThePoemLine)
                append(check)
            }
        } else {

            val checkA = assonantRime[objectiveAssonant]
            val checkC = consonantRime[objectiveConsonant] ?: checkA
            numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
            return buildString {
                append(numberSyllablesInThePoemLine)
                append(checkC)
            }
        }
    }

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
        return if (countLineBreak > 1) ", " else ""
    }

    fun poemRimeIterator(poem: String): String {
        var rimeChecks = ""
        val poemLines = poem.split("\n")
        for (line in poemLines) {
            rimeChecks += getRimeLetter(line)
        }
        return rimeChecks
    }

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
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp)
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
                        .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .background(Color(Color.Yellow.value)),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Button(
                    onClick = {
                        val poemLines = poem.split("\n")
                        poemTitle.value = poemLines[0]
                        poemBody.value = poem.removePrefix(poemLines[0])

                        scope.launch {
                            withContext(Dispatchers.IO) {
                                val draft = PoemBoxDatabase.getDatabase()?.draftDao()
                                    ?.findByTitle(POEM_TITLE)
                                if (draft != null) {
                                    poem = draft.title + "\n" + draft.draftContent
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
                        onClick = { },
                        shape = RoundedCornerShape(10.dp),
                        modifier = modifierButton()
                    ) {
                        Text(
                            text = """Type of rhyme
                            | and meter""".trimMargin()
                        )
                    }
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(10.dp),
                        modifier = modifierButtonEnOrStic()
                    ) {
                        Text(
                            text = """Enjambment
                            | or stichomytia""".trimMargin()
                        )
                    }
                }
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Button(
                        onClick = { },
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
        showDialog.value =
            AlertDialogSample("Classification by number of syllables", bodyDialog.value)
        Log.i(":::SHOW", "show dialog")
    }
}

fun cleanPunctuationMarks(poemLine: String): String {
    val cleanPoemLine = poemLine.replace(".", "")
    val cleanB = cleanPoemLine.replace(",", "")
    val cleanC = cleanB.replace(";", "")
    val cleanD = cleanC.replace(":", "")
    return cleanD
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
private fun modifierButtonEnOrStic(): Modifier {
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

@Composable
private fun AlertDialogSample(title: String, body: String): Boolean {
    val openDialog = remember { mutableStateOf(true) }

    MaterialTheme {
        Column {

            if (openDialog.value) {

                AlertDialog(onDismissRequest = {

                    openDialog.value = false
                },
                    title = { Text(text = title) },
                    text = { Text(text = body) },
                    confirmButton = {
                        Button(onClick = { openDialog.value = false }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { openDialog.value = false }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
    return openDialog.value
}

@Preview(showBackground = true)
@Composable
fun MonitoringScreenPreview() {
    MonitoringScreen()
}
