package com.lutech.paintV3.model

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class MarkerView(context: Context, attrs: AttributeSet) : View(context, attrs) {
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
        val penWidth = 90f
        val penHeight = penHeight2
        val gripHeight = 53f
        val tipHeight = 60f

        // Draw the pen tip
        val tipPath = Path().apply {
            moveTo(x - penWidth / 6, y - penHeight / 2 + tipHeight - 20f)
            lineTo(x - penWidth / 6, y - penHeight / 2 + tipHeight + 3f)
            lineTo(x + penWidth / 6, y - penHeight / 2 + tipHeight + 3f)
            close()
        }
        canvas.drawPath(tipPath, tipPaint)

        canvas.drawRect(
            x - penWidth / 6,
            y - penHeight / 2 + tipHeight + 3f,
            x + penWidth / 6,
            y - penHeight / 2 + tipHeight + 10f,
            tipPaint
        )

        // Draw the head grip
        canvas.drawRect(
            x - penWidth / 4,
            y - penHeight / 2 + tipHeight + 10f,
            x + penWidth / 4,
            y - penHeight / 2 + tipHeight + 27f,
            gripPaint
        )

        // Draw the body grip
        val gripPath = Path().apply {
            moveTo(x - penWidth / 4, y - penHeight / 2 + tipHeight + 25f)
            lineTo(x - penWidth / 2, y - penHeight / 2 + tipHeight + gripHeight)
            lineTo(x + penWidth / 2, y - penHeight / 2 + tipHeight + gripHeight)
            lineTo(
                x + penWidth / 4,
                y - penHeight / 2 + tipHeight + 25f
            )
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
