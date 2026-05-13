package dev.pgm.poembox.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object PoemCardRenderer {

    private const val WIDTH = 1080
    private const val MARGIN = 80f
    private const val LINE_HEIGHT_TITLE = 72f
    private const val LINE_HEIGHT_BODY = 52f

    fun createAndShare(context: Context, title: String, body: String, darkMode: Boolean): Uri {
        val height = estimateHeight(title, body)
        val bitmap = Bitmap.createBitmap(WIDTH, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgColor = if (darkMode) Color.parseColor("#1A1A2E") else Color.parseColor("#FAF3E0")
        val textColor = if (darkMode) Color.parseColor("#F5F0E8") else Color.parseColor("#3E2723")
        val accentColor = if (darkMode) Color.parseColor("#9E77D0") else Color.parseColor("#8B6347")

        // Background
        canvas.drawColor(bgColor)

        // Top accent bar
        val accentPaint = Paint().apply { color = accentColor; isAntiAlias = true }
        canvas.drawRect(0f, 0f, WIDTH.toFloat(), 12f, accentPaint)

        // App name watermark at bottom
        val watermarkPaint = Paint().apply {
            color = textColor
            alpha = 60
            textSize = 28f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("PoemBox", WIDTH / 2f, height - 32f, watermarkPaint)

        val titlePaint = Paint().apply {
            color = textColor
            textSize = 52f
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        val bodyPaint = Paint().apply {
            color = textColor
            textSize = 38f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        val dividerPaint = Paint().apply {
            color = accentColor
            alpha = 120
            strokeWidth = 2f
            isAntiAlias = true
        }

        var y = MARGIN + LINE_HEIGHT_TITLE

        // Title (centered, wrapped)
        y = drawCenteredWrapped(canvas, title, titlePaint, WIDTH / 2f, y, WIDTH - MARGIN * 2, LINE_HEIGHT_TITLE)
        y += 24f

        // Divider
        canvas.drawLine(MARGIN * 3, y, WIDTH - MARGIN * 3, y, dividerPaint)
        y += 40f

        // Body
        drawCenteredWrapped(canvas, body, bodyPaint, WIDTH / 2f, y, WIDTH - MARGIN * 2, LINE_HEIGHT_BODY)

        // Save to cache
        val file = File(context.cacheDir, "poem_card_${System.currentTimeMillis()}.png")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        bitmap.recycle()

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun drawCenteredWrapped(
        canvas: Canvas,
        text: String,
        paint: Paint,
        cx: Float,
        startY: Float,
        maxWidth: Float,
        lineHeight: Float
    ): Float {
        var y = startY
        text.split("\n").forEach { paragraph ->
            if (paragraph.isBlank()) {
                y += lineHeight * 0.5f
                return@forEach
            }
            val words = paragraph.split(" ")
            var line = ""
            for (word in words) {
                val candidate = if (line.isEmpty()) word else "$line $word"
                if (paint.measureText(candidate) <= maxWidth) {
                    line = candidate
                } else {
                    if (line.isNotEmpty()) {
                        canvas.drawText(line, cx, y, paint)
                        y += lineHeight
                    }
                    line = word
                }
            }
            if (line.isNotEmpty()) {
                canvas.drawText(line, cx, y, paint)
                y += lineHeight
            }
        }
        return y
    }

    private fun estimateHeight(title: String, body: String): Int {
        val titleLines = title.length / 18 + 1
        val bodyLines = body.split("\n").size + body.length / 22
        return ((titleLines * LINE_HEIGHT_TITLE) + (bodyLines * LINE_HEIGHT_BODY) + MARGIN * 4).toInt()
            .coerceAtLeast(800)
    }
}
