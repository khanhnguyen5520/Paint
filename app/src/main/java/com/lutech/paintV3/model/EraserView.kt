package com.lutech.paintV3.model

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class EraserView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var penHeight2 = 100f
    private val cornerRadius = 15f

    private val bodyPaint = Paint().apply {
        color = Color.parseColor("#ffbc3a")
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val gripPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint().apply {
        color = Color.parseColor("#f2f2f2")
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val x = width / 2f
        val y = height / 2f
        val penWidth = 90f
        val penHeight = penHeight2
        val gripHeight = 53f
        val tipHeight = 60f

        // Draw the pen grip with rounded top corners
        val path = Path().apply {
            moveTo(x - penWidth / 2, y - penHeight / 2 + tipHeight + 10f + cornerRadius)
            arcTo(
                x - penWidth / 2,
                y - penHeight / 2 + tipHeight + 10f,
                x - penWidth / 2 + 2 * cornerRadius,
                y - penHeight / 2 + tipHeight + 10f + 2 * cornerRadius,
                180f,
                90f,
                false
            )
            lineTo(x + penWidth / 2 - cornerRadius, y - penHeight / 2 + tipHeight + 10f)
            arcTo(
                x + penWidth / 2 - 2 * cornerRadius, y - penHeight / 2 + tipHeight + 10f,
                x + penWidth / 2, y - penHeight / 2 + tipHeight + 10f + 2 * cornerRadius,
                270f, 90f, false
            )
            lineTo(x + penWidth / 2, y - penHeight / 2 + tipHeight + gripHeight)
            lineTo(x - penWidth / 2, y - penHeight / 2 + tipHeight + gripHeight)
            close()
        }

        canvas.drawPath(path, gripPaint)
        canvas.drawPath(path, borderPaint) // Draw the border

        // Draw the pen body
        canvas.drawRect(
            x - penWidth / 2,
            y - penHeight / 2 + tipHeight + gripHeight,
            x + penWidth / 2,
            y + penHeight,
            bodyPaint
        )
    }

    fun setHeight(newHeight: Float) {
        penHeight2 = newHeight
        invalidate()
    }
}