package dev.pgm.poembox

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBUtil(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    companion object {

        private const val DATABASE_NAME = "POEM_BOX"

        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "DRAFT"
        const val ID_COL = "ID_DRAFT"
        const val TITLE_COl = "poemTitle"
        const val CONTENT_COL = "draftContent"
        const val ANNOTATION_COL = "draftAnnotation"
        const val WRITER_NAME_COL = "writerName"
        const val WRITER_DATE_COL = "writerDate"
        const val DATE_VALIDATION_COL = "dateValidation"
    }

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTO_INCREMENT " +
                TITLE_COl + " VARCHAR(60)," +
                CONTENT_COL + " TEXT," +
                ANNOTATION_COL + " TEXT," +
                WRITER_NAME_COL + " VARCHAR(32)," +
                WRITER_DATE_COL + " DEFAULT CURRENT_TIMESTAMP(3)," +
                DATE_VALIDATION_COL + "DATE   DEFAULT NULL"
                + ")")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // This method is for adding data in database
    fun addDraft(draft: PoemDraft) {

        val db = this.writableDatabase
        val values = ContentValues()
        val separator = "\n"
        val poemText = draft.draftContent.joinToString(separator)

        values.put(TITLE_COl, draft.title)
        values.put(CONTENT_COL, poemText)
        values.put(ANNOTATION_COL, draft.draftAnnotation)
        values.put(WRITER_NAME_COL, draft.writer.userName)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // below method is to get
    // all data from  poem box database
    fun getName(): Cursor? {

        val db = this.readableDatabase

        return db.rawQuery(buildString {
            append("SELECT * FROM ")
            append(TABLE_NAME)
        }, null)

    }


}