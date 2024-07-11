package com.lutech.paintV3.model

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class PenView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var paintColor = Color.RED
    private var penHeight2 = 100f

    private val bodyPaint = Paint().apply {
        color = paintColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val gripPaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val tipPaint = Paint().apply {
        color = paintColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val x = width / 2f
        val y = height / 2f
        val penWidth = 55f
        val penHeight = penHeight2
        val gripHeight = 53f
        val tipHeight = 60f

        // Draw the pen tip (triangle with curved tail)
        val tipPath = Path().apply {
            moveTo(x - penWidth / 5, y - penHeight / 2 + tipHeight)
            quadTo(
                x,
                y - penHeight / 2,
                x + penWidth / 5,
                y - penHeight / 2 + tipHeight
            ) // Curved head
            lineTo(x, y - penHeight / 2 + tipHeight + 20) // Connect back to base of tip
            close()
        }
        canvas.drawPath(tipPath, tipPaint)

        // Draw the grip (trapezoid with slightly curved inward)
        val gripPath = Path().apply {
            moveTo(x - penWidth / 4, y - penHeight / 2 + tipHeight)
            quadTo(
                x - penWidth / 3,
                y - penHeight / 2 + tipHeight + gripHeight,
                x - penWidth / 2,
                y - penHeight / 2 + tipHeight + gripHeight
            ) // Curved inward

            lineTo(x + penWidth / 2, y - penHeight / 2 + tipHeight + gripHeight)
            quadTo(
                x + penWidth / 3,
                y - penHeight / 2 + tipHeight + gripHeight,
                x + penWidth / 4,
                y - penHeight / 2 + tipHeight
            ) // Curved inward
            close()
        }
        canvas.drawPath(gripPath, gripPaint)

        // Draw the pen body
        canvas.drawRect(
            x - penWidth / 2,
            y - penHeight / 2 + tipHeight + gripHeight,
            x + penWidth / 2,
            y + penHeight,
            bodyPaint
        )
    }

    fun setColor(newColor: Int) {
        invalidate()
        paintColor = newColor
        bodyPaint.color = paintColor
        tipPaint.color = paintColor
    }

    fun setHeight(newHeight: Float) {
        penHeight2 = newHeight
        invalidate()
    }
}