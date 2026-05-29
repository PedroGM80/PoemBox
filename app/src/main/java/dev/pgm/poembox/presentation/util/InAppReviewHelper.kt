package dev.pgm.poembox.presentation.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Solicita el flujo de valoración de Google Play en el momento adecuado.
 * Google controla internamente si muestra el diálogo o no (cuotas, versión Play,
 * si el usuario ya valoró), por lo que es seguro llamarlo cada vez que el
 * usuario complete una acción significativa.
 */
object InAppReviewHelper {

    fun requestReview(context: Context) {
        val activity = context.findActivity() ?: return
        val manager = ReviewManagerFactory.create(activity)
        manager.requestReviewFlow()
            .addOnSuccessListener { reviewInfo ->
                manager.launchReviewFlow(activity, reviewInfo)
                    .addOnFailureListener { e ->
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
            }
            .addOnFailureListener { e ->
                // No es crítico: Google Play no garantiza mostrar el diálogo
                FirebaseCrashlytics.getInstance().recordException(e)
            }
    }

    private fun Context.findActivity(): Activity? {
        var ctx = this
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }
}
