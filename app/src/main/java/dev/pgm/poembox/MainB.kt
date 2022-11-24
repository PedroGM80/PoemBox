package dev.pgm.poembox

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import dev.pgm.poembox.roomUtils.Draft
import dev.pgm.poembox.roomUtils.PoemBoxDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

var allPoemDraft: MutableList<Draft>? = null

class MainB : ComponentActivity() {
    // creating variables for our edittext, button and dbhandler
    private val poemBoxDatabase by lazy { PoemBoxDatabase.getDatabase(this).draftDao() }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_database_test)
        val draftTitle: EditText? = findViewById(R.id.editTextTextTiTle)
        val draftUserWrote: EditText? = findViewById(R.id.editTextUserName)
        val draftContent: EditText? = findViewById(R.id.editTextDraftContent)
        val draftAnnotation: EditText? = findViewById(R.id.editTextNotes)
        val addDraft: Button? = findViewById(R.id.button)

        addDraft?.setOnClickListener {
            val draftTitleText = draftTitle?.text.toString()
            val draftUserText = draftUserWrote?.text.toString()
            val draftLines = draftContent?.text.toString()
            val draftUserNOTE = draftAnnotation?.text.toString()

            if (draftLines.isEmpty() && draftTitleText.isEmpty() && draftUserText.isEmpty() && draftUserNOTE.isEmpty()) {
                Toast.makeText(
                    this@MainB,
                    "Please enter all the data..",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            val userText = User("1", "pet", "Test@gmail.es")
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = Date()
            val current = formatter.format(date)
            val draft = Draft(
                "titulo", "contenido", "anotación", "pedro", current, null
            )
            GlobalScope.launch {
                poemBoxDatabase.addDraft(draft)
            }
        }
    }
}
