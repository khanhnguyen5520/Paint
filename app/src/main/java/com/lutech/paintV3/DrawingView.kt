package com.lutech.paintV3

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.os.Environment
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.hypot

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var drawPath = Path()
    var drawPaint = Paint()
    private var canvasPaint: Paint? = null
    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null
    private var paintColor = Color.BLACK
    private val paths = mutableListOf<Pair<Path, Paint>>()
    private val undonePaths = mutableListOf<Pair<Path, Paint>>()
    private var strokeWidth = 5f
    private var drawMode = DrawMode.NORMAL

    private var startX = 0f
    private var startY = 0f

    init {
        setupDrawing()
    }

    private fun setupDrawing() {
        drawPaint.color = paintColor
        drawPaint.isAntiAlias = true
        drawPaint.strokeWidth = strokeWidth
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)
        for (path in paths) {
            canvas.drawPath(path.first, path.second)
        }
        canvas.drawPath(drawPath, drawPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = touchX
                startY = touchY
                if (drawMode == DrawMode.LINE) {
                    drawPath.moveTo(touchX, touchY)
                }
                drawPath.moveTo(touchX, touchY)
            }

            MotionEvent.ACTION_MOVE -> {
                when (drawMode) {
                    DrawMode.LINE -> {
                        drawPath.reset()
                        drawPath.moveTo(startX, startY)
                        drawPath.lineTo(touchX, touchY)
                    }

                    DrawMode.CIRCLE -> {
                        drawPath.reset()
                        val radius = hypot(
                            (touchX - startX).toDouble(),
                            (touchY - startY).toDouble()
                        ).toFloat()
                        drawPath.addCircle(startX, startY, radius, Path.Direction.CW)
                    }

                    DrawMode.RECTANGLE -> {
                        drawPath.reset()
                        drawPath.addRect(startX, startY, touchX, touchY, Path.Direction.CW)
                    }

                    DrawMode.NORMAL -> {
                        drawPath.lineTo(touchX, touchY)
                    }

                }

            }

            MotionEvent.ACTION_UP -> {
                val newPaint = Paint(drawPaint)
                paths.add(Pair(drawPath, newPaint))
                drawPath = Path()
            }

            else -> return false
        }
        invalidate()
        return true
    }

    fun setColor(newColor: Int, alpha: Int) {
        invalidate()
        paintColor = newColor
        drawPaint.color = paintColor
        drawPaint.alpha = alpha
    }

    fun setStrokeWidth(newSize: Float) {
        strokeWidth = newSize
        drawPaint.strokeWidth = strokeWidth
    }

    fun setStrokeCap(newCap: String) {
        if (newCap == "ROUND") {
            drawPaint.strokeCap = Paint.Cap.ROUND
        } else {
            drawPaint.strokeCap = Paint.Cap.SQUARE
        }
    }

    fun setEraser(isEraser: Boolean) {
        if (isEraser) {
            drawPaint.color = Color.WHITE
        } else {
            drawPaint.color = paintColor
        }
    }

    fun undo() {
        if (paths.isNotEmpty()) {
            undonePaths.add(paths.removeAt(paths.size - 1))
            invalidate()
        }
    }

    fun redo() {
        if (undonePaths.isNotEmpty()) {
            paths.add(undonePaths.removeAt(undonePaths.size - 1))
            invalidate()
        }
    }

    fun loadBitmap(bitmap: Bitmap) {
        val matrix = Matrix()
        matrix.postRotate(270f)
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        val scaledBitmap = scaleBitmapToFit(rotatedBitmap, width, height)
        val left = (width - scaledBitmap.width) / 2
        val top = (height - scaledBitmap.height) / 2

        canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
        drawCanvas!!.drawBitmap(scaledBitmap, left.toFloat(), top.toFloat(), canvasPaint)

        invalidate()
    }

    private fun scaleBitmapToFit(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val widthRatio = maxWidth.toFloat() / bitmap.width.toFloat()
        val heightRatio = maxHeight.toFloat() / bitmap.height.toFloat()
        val scaleFactor = minOf(widthRatio, heightRatio)
        val scaledWidth = (bitmap.width * scaleFactor).toInt()
        val scaledHeight = (bitmap.height * scaleFactor).toInt()
        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
    }

    enum class DrawMode {
        LINE,
        CIRCLE,
        RECTANGLE,
        NORMAL
    }

    fun setMode(mode: String) {
        setEraser(false)
        drawMode = when (mode) {
            "LINE" -> {
                DrawMode.LINE
            }

            "CIRCLE" -> {
                DrawMode.CIRCLE
            }

            "RECTANGLE" -> {
                DrawMode.RECTANGLE
            }

            else -> {
                DrawMode.NORMAL
            }
        }
    }

    fun deleteAll() {
        drawPath.reset()
        drawPaint.reset()
        drawCanvas!!.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)
        undonePaths.addAll(paths)
        paths.clear()
        invalidate()
        setupDrawing()
    }

    fun saveCanvasToFile(): Boolean {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)

        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "MyPaintApp"
        )

        if (!directory.exists()) {
            directory.mkdirs()
        }
        val fileName = "drawing_${System.currentTimeMillis()}.jpg"
        val file = File(directory, fileName)

        return try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}


