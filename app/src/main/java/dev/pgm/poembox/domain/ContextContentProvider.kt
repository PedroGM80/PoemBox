package dev.pgm.poembox.domain

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

/**
 * Context content provider
 *
 * @constructor Create empty Context content provider
 */
class ContextContentProvider : ContentProvider() {
    /**
     * Delete
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?) = 0

    /**
     * Get type
     *
     * @param uri
     * @return
     */
    override fun getType(uri: Uri): String? = null

    /**
     * Insert
     *
     * @param uri
     * @param values
     * @return
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    /**
     * On create
     *
     * @return
     */
    override fun onCreate(): Boolean {
        applicationContext = context
        return true
    }

    /**
     * Query
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? = null

    /**
     * Update
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     */
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