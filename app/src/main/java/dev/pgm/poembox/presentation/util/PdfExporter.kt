package dev.pgm.poembox.presentation.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import dev.pgm.poembox.R

object PdfExporter {

    private const val PAGE_WIDTH = 595   // A4 width in points (72dpi)
    private const val PAGE_HEIGHT = 842  // A4 height in points
    private const val MARGIN = 72f       // 1 inch margin

    fun exportToPdf(
        context: Context,
        uri: Uri,
        title: String,
        body: String,
        syllablesAnalysis: String,
        versesAnalysis: String,
        rhymeAnalysis: String,
        enjambmentAnalysis: String
    ) {
        val document = PdfDocument()
        val titlePaint = Paint().apply {
            textSize = 24f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val bodyPaint = Paint().apply {
            textSize = 14f
            isAntiAlias = true
        }
        val labelPaint = Paint().apply {
            textSize = 12f
            isFakeBoldText = true
            isAntiAlias = true
            color = android.graphics.Color.DKGRAY
        }
        val smallPaint = Paint().apply {
            textSize = 11f
            isAntiAlias = true
            color = android.graphics.Color.DKGRAY
        }

        val contentWidth = PAGE_WIDTH - MARGIN * 2

        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        var y = MARGIN + 30f

        // Title
        y = drawWrappedText(canvas, title, titlePaint, MARGIN, y, contentWidth, 30f)
        y += 16f

        // Divider
        val linePaint = Paint().apply { color = android.graphics.Color.LTGRAY; strokeWidth = 1f }
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 20f

        // Body
        y = drawWrappedText(canvas, body, bodyPaint, MARGIN, y, contentWidth, 22f)
        y += 24f

        // Analysis section
        fun addAnalysisLine(label: String, value: String) {
            if (value.isBlank()) return
            canvas.drawText(label, MARGIN, y, labelPaint)
            // intentionally captured; y is updated after call
        }

        if (syllablesAnalysis.isNotBlank() || versesAnalysis.isNotBlank()) {
            canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
            y += 20f
            canvas.drawText(context.getString(R.string.monitor_export_section_metrical), MARGIN, y, labelPaint)
            y += 18f
            if (syllablesAnalysis.isNotBlank()) {
                y = drawWrappedText(canvas, syllablesAnalysis, smallPaint, MARGIN, y, contentWidth, 16f)
                y += 4f
            }
            if (versesAnalysis.isNotBlank()) {
                y = drawWrappedText(canvas, versesAnalysis, smallPaint, MARGIN, y, contentWidth, 16f)
                y += 4f
            }
            if (rhymeAnalysis.isNotBlank() || enjambmentAnalysis.isNotBlank()) {
                y += 8f
                canvas.drawText(context.getString(R.string.monitor_export_section_structure), MARGIN, y, labelPaint)
                y += 18f
                if (rhymeAnalysis.isNotBlank()) {
                    y = drawWrappedText(canvas, rhymeAnalysis, smallPaint, MARGIN, y, contentWidth, 16f)
                    y += 4f
                }
                if (enjambmentAnalysis.isNotBlank()) {
                    y = drawWrappedText(canvas, enjambmentAnalysis, smallPaint, MARGIN, y, contentWidth, 16f)
                }
            }
        }

        document.finishPage(page)

        context.contentResolver.openOutputStream(uri)?.use { stream ->
            document.writeTo(stream)
        }
        document.close()
    }

    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        paint: Paint,
        x: Float,
        startY: Float,
        maxWidth: Float,
        lineHeight: Float
    ): Float {
        var y = startY
        text.split("\n").forEach { paragraph ->
            val words = paragraph.split(" ")
            var line = ""
            for (word in words) {
                val candidate = if (line.isEmpty()) word else "$line $word"
                if (paint.measureText(candidate) <= maxWidth) {
                    line = candidate
                } else {
                    if (line.isNotEmpty()) {
                        canvas.drawText(line, x, y, paint)
                        y += lineHeight
                    }
                    line = word
                }
            }
            if (line.isNotEmpty()) {
                canvas.drawText(line, x, y, paint)
                y += lineHeight
            }
        }
        return y
    }
}
