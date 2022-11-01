package dev.pgm.poembox

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivityDatabaseTest : AppCompatActivity() {
    // creating variables for our edittext, button and dbhandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_database_test)
/*

        val draftTitle: EditText? = findViewById(R.id.editTextTextTiTle)
        val draftUserWrote: EditText? = findViewById(R.id.editTextUserName)
        val draftContent: EditText? = findViewById(R.id.editTextDraftContent)
        val draftAnnotation: EditText? = findViewById(R.id.editTextNotes)
        val addDraft: Button? = findViewById(R.id.button)
        val dbPoem: DBUtil?
        dbPoem = DBUtil(this@MainActivityDatabaseTest)

        addDraft?.setOnClickListener {
            val draftTitleText = draftTitle?.text.toString()
            val draftUserText = draftUserWrote?.text.toString()
            val draftLines = draftContent?.text.toString()
            val draftUserNOTE = draftAnnotation?.text.toString()

            if (draftLines.isEmpty() && draftTitleText.isEmpty() && draftUserText.isEmpty() && draftUserNOTE.isEmpty()) {
                Toast.makeText(
                    this@MainActivityDatabaseTest,
                    "Please enter all the data..",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            val userText = User("pet", "Test@gmail.es")
            val draft = PoemDraft(
                draftTitleText,
                draftLines,
                draftUserNOTE,
                userText
            )
            dbPoem.addDraft(draft)

            Toast.makeText(
                this@MainActivityDatabaseTest,
                "Draft has been added.",
                Toast.LENGTH_SHORT
            ).show()
            draftTitle?.setText("")
            draftUserWrote?.setText("")
            draftAnnotation?.setText("")
            draftContent?.setText("")
        }
*/
    }
}
