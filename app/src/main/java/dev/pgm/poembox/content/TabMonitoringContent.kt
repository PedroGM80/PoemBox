package dev.pgm.poembox.content

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
import dev.pgm.poembox.businessLogic.UtilitySyllables
import dev.pgm.poembox.roomUtils.PoemBoxDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun MonitoringScreen() {
    val consonant = arrayOf(
        'b',
        'c',
        'd',
        'f',
        'g',
        'h',
        'i',
        'j',
        'k',
        'l',
        'm',
        'n',
        'ñ',
        'p',
        'q',
        'r',
        's',
        't',
        'v',
        'w',
        'x',
        'y',
        'z'
    )
    val showDialog = remember { mutableStateOf(false) }
    val numberSyllablesInPoem = mutableListOf<String>()
    var higherCountVowel = 65
    var minorCountVowel = 97
    var higherCountConsonant = 0
    var minorCountConsonant = 0
    var countLineBreak = 0
    val bodyDialog = remember { mutableStateOf("") }

    fun getRimeLetter(poemLine: String): String {
//tengo que hacer una lista con las letras de rimas asociadas a las vocales
        //para que si contiene una rima en a la siguiente ponga a
        if (poemLine.isEmpty()) {
            countLineBreak++
        } else {
            val utilitySyllables = UtilitySyllables()
            val poemLineSyllables = utilitySyllables.getSyllables(poemLine)
            val numberSyllablesInThePoemLine = poemLineSyllables.size
            val higherArt = (numberSyllablesInThePoemLine > 8)
            val minorArt = (numberSyllablesInThePoemLine <= 8)
            val lastSyllable = poemLineSyllables.last()
            val lastLetter = lastSyllable.last()
            countLineBreak = 0
            when {
                utilitySyllables.isVowel(lastLetter) && higherArt -> {
                    val check = higherCountVowel.toChar().toString()
                    higherCountVowel++
                    numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
                    return buildString {
                        append(numberSyllablesInThePoemLine)
                        append(check)
                    }
                }
                !utilitySyllables.isVowel(lastLetter) && higherArt -> {
                    val check = consonant[higherCountConsonant].toString()
                    numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
                    higherCountConsonant++
                    return buildString {
                        append(numberSyllablesInThePoemLine)
                        append(check)
                    }
                }
                utilitySyllables.isVowel(lastLetter) && minorArt -> {
                    val check = minorCountVowel.toChar().toString()
                    numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
                    minorCountVowel++
                    return buildString {
                        append(numberSyllablesInThePoemLine)
                        append(check)
                    }
                }
                !utilitySyllables.isVowel(lastLetter) && minorArt -> {
                    val check = consonant[minorCountConsonant].uppercase()
                    numberSyllablesInPoem.add(numberSyllablesInThePoemLine.toString())
                    minorCountConsonant++
                    return buildString {
                        append(numberSyllablesInThePoemLine)
                        append(check)
                    }
                }
            }
        }
        return if (countLineBreak > 1) ", " else ""
    }

    var poem by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    fun poemRimeIterator(poem: String): String {
        var rimeChecks = ""
        val poemLines = poem.split("\n")
        for (line in poemLines) {
            rimeChecks += getRimeLetter(line)
        }
        return rimeChecks
    }

    fun <T> findDuplicates(list: List<T>): Set<T> {
        val seen: MutableSet<T> = mutableSetOf()
        return list.filter { !seen.add(it) }.toSet()
    }

    fun getPredominateNumberSyllables(): String = findDuplicates(numberSyllablesInPoem).toString()
        .replace("[", "")
        .replace("]", "")
    Log.i(":::POEM", poem)
    Surface(color = MaterialTheme.colors.primary) {

        Box(Modifier.wrapContentSize(Alignment.Center)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.white))
                    .align(Alignment.TopCenter)
            ) {
                val poemLines = poem.split("\n")
                val poemTitle = poemLines[0]
                val bodyPoem = poem.removePrefix(poemLines[0])
                Text(
                    text = poemTitle,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .background(
                            Color(Color.Yellow.value)
                        ),

                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
                Text(
                    text = bodyPoem,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .background(
                            Color(Color.Yellow.value)
                        ),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Button(
                    onClick = {
                        Log.i(":::POEMTCORRU", POEM_TITLE)
                        scope.launch {
                            withContext(Dispatchers.IO) {

                                val draft = PoemBoxDatabase.getDatabase()?.draftDao()
                                    ?.findByTitle(POEM_TITLE)
                                if (draft != null) {
                                    poem = draft.title + "\n" + draft.draftContent
                                }
                            }
                            Log.i(":::POEMRUN", poem)
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(5.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Transparent)
                ) { Text(text = "Analyze your poem $POEM_TITLE") }


                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Button(
                        onClick = {
                            Log.i(":::SHOW", "show dialogA")
                            val result = poemRimeIterator(bodyPoem)
                            val numberPredominate = getPredominateNumberSyllables()
                            bodyDialog.value = "$numberPredominate syllable verses predominate," +
                                    " following the schema: $result \n"
                            Log.i(":::SHOWB", bodyDialog.value)
                            showDialog.value = true

                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .width(200.dp)
                            .height(100.dp)
                            .padding(5.dp)
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
                        modifier = Modifier
                            .width(200.dp)
                            .height(100.dp)
                            .padding(5.dp)
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
                        modifier = Modifier
                            .width(200.dp)
                            .height(100.dp)
                            .padding(5.dp)
                    ) {
                        Text(
                            text = """Type of rhyme
                            | and meter""".trimMargin()
                        )
                    }
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(100.dp)
                            .width(200.dp)
                            .padding(5.dp)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .padding(10.dp)
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
        AlertDialogSample("Classification by number of syllables", bodyDialog.value)
        Log.i(":::SHOW", "show dialog")
    }
}

@Composable
fun AlertDialogSample(body: String, title: String) {
    MaterialTheme {
        Column {
            val openDialog = remember { mutableStateOf(false) }
            Button(onClick = {
                openDialog.value = true
            }) {
                Text("Click me")
            }

            if (openDialog.value) {

                AlertDialog(
                    onDismissRequest = {
                        openDialog.value = false
                    },
                    title = {
                        Text(text = title)
                    },
                    text = {
                        Text(text = body)
                    },
                    confirmButton = {
                        Button(

                            onClick = {
                                openDialog.value = false
                            }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        Button(

                            onClick = {
                                openDialog.value = false
                            }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MonitoringScreenPreview() {
    MonitoringScreen()
}
