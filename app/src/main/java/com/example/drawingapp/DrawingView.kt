package com.example.drawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    // drawing path
    private lateinit var drawPath: FingerPath

    // defines what to draw
    private lateinit var canvasPaint: Paint

    // defines how to draw
    private lateinit var drawPaint: Paint
    private var color = Color.BLACK
    private lateinit var canvas: Canvas
    private lateinit var canvasBitmap: Bitmap
    private var brushSize: Float = 0.toFloat()
    private  val paths = mutableListOf<FingerPath>()

    init {
        setUpDrawing()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap)
    }

    // this function will be called by the system when the user is going to touch the screen
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when (event?.action) {
            // this event will be fired when the user put their finger on the screen
            MotionEvent.ACTION_DOWN -> {
                drawPath.color = color
                drawPath.brushThickness = brushSize.toFloat()

                drawPath.reset() // reseting path before setting initial point
                drawPath.moveTo(touchX!!, touchY!!)
            }

            // this event will be fired when the user starts to move their finger; this will be
            // fired continuously until the user picks up the finger
            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(touchX!!, touchY!!)
            }

            // this event will be fired when the user will pick up the finger from their screen
            MotionEvent.ACTION_UP -> {
                drawPath = FingerPath(color, brushSize)
                paths.add(drawPath)
            }
            else -> return false
        }
        invalidate() // refresh layout to reflect the drawing
        return true

        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas?.drawBitmap(canvasBitmap, 0f, 0f, drawPaint)

        for (path in paths) {
            drawPaint.strokeWidth = path.brushThickness
            drawPaint.color = path.color
            canvas.drawPath(path, drawPaint)
        }
        if (!drawPath.isEmpty) {
            drawPaint.strokeWidth = drawPath.brushThickness
            drawPaint.color = drawPath.color
            canvas.drawPath(drawPath, drawPaint)
        }
    }

    private fun setUpDrawing() {
        drawPaint = Paint()
        drawPath = FingerPath(color, brushSize)
        drawPaint.color = color
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND

        canvasPaint = Paint(Paint.DITHER_FLAG)
        brushSize = 20.toFloat()
    }

    fun changeBrushSize(newSize: Float) {
        brushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize, resources.displayMetrics
        )
        drawPaint.strokeWidth = brushSize
    }

    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        drawPaint.color = Color.parseColor(newColor)
    }

    internal inner class FingerPath(var color: Int, var brushThickness: Float) : Path()
}