package dev.pgm.poembox.presentation.util

import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Centraliza todos los eventos de Firebase Analytics de la app.
 * Los nombres de evento y parámetro siguen las convenciones snake_case de Firebase.
 */
object Analytics {

    private val fa: FirebaseAnalytics by lazy {
        try {
            FirebaseAnalytics.getInstance(FirebaseApp.getInstance().applicationContext)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            throw e
        }
    }

    // ── Poema ──────────────────────────────────────────────────────────────
    fun poemSaved(title: String, formId: String) = log("poem_saved") {
        putString("poem_title", title.take(100))
        putString("poetic_form", formId)
    }

    fun poemValidated(title: String, syllables: Int) = log("poem_validated") {
        putString("poem_title", title.take(100))
        putInt("syllable_count", syllables)
    }

    fun poemDeleted() = log("poem_deleted")

    // ── Compartir ──────────────────────────────────────────────────────────
    fun sharedAsText() = log("share_text")
    fun sharedAsCard(hasBgImage: Boolean) = log("share_card") {
        putBoolean("has_bg_image", hasBgImage)
    }
    fun sharedAsPdf() = log("share_pdf")
    fun sharedAsTxt() = log("share_txt")

    // ── Pantalla / UX ──────────────────────────────────────────────────────
    fun immersiveOpened() = log("immersive_opened")
    fun bgImagePicked() = log("bg_image_picked")
    fun formSelected(formId: String) = log("form_selected") {
        putString("poetic_form", formId)
    }

    // ── Inspiración ────────────────────────────────────────────────────────
    fun inspirationUsed() = log("inspiration_used")

    // ── Helper ─────────────────────────────────────────────────────────────
    private fun log(event: String, params: (Bundle.() -> Unit)? = null) {
        runCatching {
            val bundle = if (params != null) Bundle().apply(params) else null
            fa.logEvent(event, bundle)
        }.onFailure { FirebaseCrashlytics.getInstance().recordException(it) }
    }
}
