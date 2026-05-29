package dev.pgm.poembox.presentation.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class PoemBoxWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = PoemBoxWidget()
}
