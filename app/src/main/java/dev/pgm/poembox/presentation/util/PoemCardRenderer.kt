package dev.pgm.poembox.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object PoemCardRenderer {

    private const val WIDTH = 1080
    private const val MARGIN = 80f
    private const val LINE_HEIGHT_TITLE = 72f
    private const val LINE_HEIGHT_BODY = 52f

    fun createAndShare(
        context: Context,
        title: String,
        body: String,
        darkMode: Boolean,
        backgroundImageUri: Uri? = null
    ): Uri {
        val height = estimateHeight(title, body)
        val bitmap = Bitmap.createBitmap(WIDTH, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val textColor: Int
        val accentColor: Int

        if (backgroundImageUri != null) {
            drawBackgroundImage(context, canvas, backgroundImageUri, WIDTH, height)
            // Semi-transparent dark overlay so text is readable over any photo
            val overlayPaint = Paint().apply { color = Color.argb(150, 0, 0, 0) }
            canvas.drawRect(0f, 0f, WIDTH.toFloat(), height.toFloat(), overlayPaint)
            textColor = Color.parseColor("#F5F0E8")
            accentColor = Color.parseColor("#C9A8E8")
        } else {
            val bgColor = if (darkMode) Color.parseColor("#1A1A2E") else Color.parseColor("#FAF3E0")
            canvas.drawColor(bgColor)
            textColor = if (darkMode) Color.parseColor("#F5F0E8") else Color.parseColor("#3E2723")
            accentColor = if (darkMode) Color.parseColor("#9E77D0") else Color.parseColor("#8B6347")
        }

        // Top accent bar
        val accentPaint = Paint().apply { color = accentColor; isAntiAlias = true }
        canvas.drawRect(0f, 0f, WIDTH.toFloat(), 12f, accentPaint)

        // Watermark
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
            if (backgroundImageUri != null) setShadowLayer(10f, 0f, 2f, Color.argb(200, 0, 0, 0))
        }
        val bodyPaint = Paint().apply {
            color = textColor
            textSize = 38f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            if (backgroundImageUri != null) setShadowLayer(8f, 0f, 1f, Color.argb(180, 0, 0, 0))
        }
        val dividerPaint = Paint().apply {
            color = accentColor
            alpha = 120
            strokeWidth = 2f
            isAntiAlias = true
        }

        var y = MARGIN + LINE_HEIGHT_TITLE
        y = drawCenteredWrapped(canvas, title, titlePaint, WIDTH / 2f, y, WIDTH - MARGIN * 2, LINE_HEIGHT_TITLE)
        y += 24f
        canvas.drawLine(MARGIN * 3, y, WIDTH - MARGIN * 3, y, dividerPaint)
        y += 40f
        drawCenteredWrapped(canvas, body, bodyPaint, WIDTH / 2f, y, WIDTH - MARGIN * 2, LINE_HEIGHT_BODY)

        val file = File(context.cacheDir, "poem_card_${System.currentTimeMillis()}.png")
        file.outputStream().use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
        bitmap.recycle()

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun drawBackgroundImage(
        context: Context,
        canvas: Canvas,
        uri: Uri,
        targetWidth: Int,
        targetHeight: Int
    ) {
        try {
            val source = context.contentResolver.openInputStream(uri)
                ?.use { BitmapFactory.decodeStream(it) } ?: return

            val srcRatio = source.width.toFloat() / source.height
            val tgtRatio = targetWidth.toFloat() / targetHeight

            val (scaledW, scaledH) = if (srcRatio > tgtRatio) {
                Pair(targetHeight * source.width / source.height, targetHeight)
            } else {
                Pair(targetWidth, targetWidth * source.height / source.width)
            }

            val offsetX = (scaledW - targetWidth) / 2
            val offsetY = (scaledH - targetHeight) / 2

            val scaled = Bitmap.createScaledBitmap(source, scaledW, scaledH, true)
            source.recycle()
            val cropped = Bitmap.createBitmap(scaled, offsetX, offsetY, targetWidth, targetHeight)
            if (scaled !== cropped) scaled.recycle()

            canvas.drawBitmap(cropped, 0f, 0f, null)
            cropped.recycle()
        } catch (_: Exception) {
            canvas.drawColor(Color.parseColor("#1A1A2E"))
        }
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
        wrapTextIntoLines(text, paint, maxWidth).forEach { line ->
            if (line.isEmpty()) {
                y += lineHeight * 0.5f
            } else {
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
