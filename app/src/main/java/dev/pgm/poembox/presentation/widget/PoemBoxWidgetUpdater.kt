package dev.pgm.poembox.presentation.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Actualiza el estado del widget de inicio con el título del último poema
 * y la racha actual. Si no hay ningún widget añadido, no hace nada.
 */
object PoemBoxWidgetUpdater {

    suspend fun update(context: Context, poemTitle: String, streak: Int) {
        runCatching {
            val manager = GlanceAppWidgetManager(context)
            val ids = manager.getGlanceIds(PoemBoxWidget::class.java)
            for (id in ids) {
                updateAppWidgetState(context, id) { prefs ->
                    prefs[PoemBoxWidget.POEM_TITLE_KEY] = poemTitle
                    prefs[PoemBoxWidget.STREAK_KEY] = streak
                }
                PoemBoxWidget().update(context, id)
            }
        }.onFailure { e ->
            runCatching { FirebaseCrashlytics.getInstance().recordException(e) }
        }
    }
}
