package com.lutech.paintV3.model

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class MarkerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val bodyPaint = Paint().apply {
        color = Color.RED
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val gripPaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val tipPaint = Paint().apply {
        color = Color.RED
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val x = width / 2f
        val y = height / 2f
        val penWidth = 100f
        val penHeight = 500f
        val gripHeight = 80f
        val tipHeight = 100f

        // Draw the pen body (rectangle)
        canvas.drawRect(
            x - penWidth / 2,
            y - penHeight / 2,
            x + penWidth / 2,
            y + penHeight / 2 - gripHeight - tipHeight,
            bodyPaint
        )

        // Draw the grip (trapezoid)
        val gripPath = Path().apply {
            moveTo(x - penWidth / 2, y + penHeight / 2 - gripHeight - tipHeight)
            lineTo(x + penWidth / 2, y + penHeight / 2 - gripHeight - tipHeight)
            lineTo(x + penWidth / 4, y + penHeight / 2 - tipHeight)
            lineTo(x - penWidth / 4, y + penHeight / 2 - tipHeight)
            close()
        }
        canvas.drawPath(gripPath, gripPaint)

        // Draw the tip (triangle with curved tail)
        val tipPath = Path().apply {
            moveTo(x - penWidth / 4, y + penHeight / 2 - tipHeight)
            lineTo(x + penWidth / 4, y + penHeight / 2 - tipHeight)
            quadTo(x, y + penHeight / 2, x - penWidth / 4, y + penHeight / 2 - tipHeight) // Curved tail
            lineTo(x, y + penHeight / 2)
            close()
        }
        canvas.drawPath(tipPath, tipPaint)
    }
}