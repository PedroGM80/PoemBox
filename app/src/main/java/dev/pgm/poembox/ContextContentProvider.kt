package dev.pgm.poembox

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

class ContextContentProvider : ContentProvider() {
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?) = 0

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun onCreate(): Boolean {
        applicationContext = context
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? = null

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ) = 0

    companion object {
        private var applicationContext: Context? = null

        @JvmStatic
        fun applicationContext() = applicationContext
    }
}