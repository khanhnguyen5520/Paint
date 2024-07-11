package com.lutech.paintV3.model

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class StrokeWidthView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var paintColor = Color.BLACK
    private var brushSize = 10f

    private val paint = Paint().apply {
        color = paintColor
        strokeWidth = brushSize
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val path = Path()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawDoodleM(canvas)
    }

    fun setColor(newColor: Int){
        invalidate()
        paintColor = newColor
        paint.color = paintColor
    }

    fun setStrokeWidth(newSize: Float){
        brushSize = newSize
        paint.strokeWidth = brushSize
        invalidate()
    }

    fun setStrokeCap(newCap: String){
        if (newCap == "ROUND"){
            paint.strokeCap = Paint.Cap.ROUND
        } else {
            paint.strokeCap = Paint.Cap.SQUARE
        }
    }

    private fun drawDoodleM(canvas: Canvas) {
        val X1 = 40f
        val Y1 = 100f
        val X2 = 120f
        val Y3 = 50f
        val X3 = 275f
        val Y2 = 125f
        val X4 = 430f
        val X5 = 510f
        val mX1 = 198f
        val mX2 = 353f
        val mY = 90f
        path.reset()
        path.moveTo(X1, Y1)
        path.cubicTo(X2, Y3, X2, Y3, mX1, mY)
        path.cubicTo(X3, Y2, X3, Y2, mX2, mY)
        path.cubicTo(X4, Y3, X4, Y3, X5, Y1)

        canvas.drawPath(path, paint)
    }
}
