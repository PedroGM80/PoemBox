package dev.pgm.poembox

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import dev.pgm.poembox.roomUtils.PoemDraft
import dev.pgm.poembox.roomUtils.PoemDraftDb
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

var allPoemDraft: MutableList<PoemDraft>? = null

class MainB :ComponentActivity()  {
    // creating variables for our edittext, button and dbhandler

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
            val userText = User("1","pet", "Test@gmail.es")
            val room: PoemDraftDb = Room
                .databaseBuilder(this@MainB, PoemDraftDb::class.java, "poemDraft")
                .build()

            val draft = PoemDraft(
                1,
                draftTitleText,
                draftLines,
                draftUserNOTE,
                userText,
                "20/02/2022"
            )
            GlobalScope.launch {
                allPoemDraft = room.poemDraftDao().getAll()
                allPoemDraft!!.add(draft)
            }





            Toast.makeText(
                baseContext,
                "Draft has been added.",
                Toast.LENGTH_SHORT
            ).show()
            draftTitle?.setText("")
            draftUserWrote?.setText("")
            draftAnnotation?.setText("")
            draftContent?.setText("")
        }

    }
}
