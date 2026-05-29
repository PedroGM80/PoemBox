package dev.pgm.poembox.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentWidth
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.pgm.poembox.presentation.MainActivity

class PoemBoxWidget : GlanceAppWidget() {

    companion object {
        val POEM_TITLE_KEY = stringPreferencesKey("widget_poem_title")
        val STREAK_KEY = intPreferencesKey("widget_streak")
    }

    override val stateDefinition: GlanceStateDefinition<Preferences> =
        PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        val prefs = currentState<Preferences>()
        val poemTitle = prefs[POEM_TITLE_KEY] ?: ""
        val streak = prefs[STREAK_KEY] ?: 0
        val ctx = LocalContext.current
        val openApp = actionStartActivity(Intent(ctx, MainActivity::class.java))

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .cornerRadius(16.dp)
                .clickable(openApp)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = GlanceModifier.fillMaxSize()) {
                // Header: app name + streak
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PoemBox",
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        modifier = GlanceModifier.defaultWeight()
                    )
                    if (streak > 0) {
                        Text(
                            text = "🔥 $streak",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 12.sp
                            ),
                            modifier = GlanceModifier.wrapContentWidth()
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(6.dp))

                // Poem title or hint
                Text(
                    text = if (poemTitle.isNotBlank()) "\"$poemTitle\""
                           else "Toca para empezar a escribir…",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 14.sp,
                        fontStyle = if (poemTitle.isNotBlank()) FontStyle.Italic else FontStyle.Normal
                    ),
                    maxLines = 2,
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight()
                )

                Spacer(modifier = GlanceModifier.height(8.dp))

                // Write button
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(GlanceTheme.colors.primaryContainer)
                        .cornerRadius(8.dp)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable(openApp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✏️ Escribir",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }
    }
}
