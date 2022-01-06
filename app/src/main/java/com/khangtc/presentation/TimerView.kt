package com.khangtc.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.khangtc.R
import java.lang.StrictMath.min


class TimerView(context: Context, attrs: AttributeSet): AppCompatTextView(context, attrs) {
    private val processPaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.STROKE
    }
    private val circlePaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.STROKE
    }
    private var angle = 0f

    init {
        context.obtainStyledAttributes(attrs, R.styleable.TimerView, 0, 0).apply {
            try {
                val processColor: Int = this.getColor(
                    R.styleable.TimerView_processColor, ContextCompat.getColor(context,
                        R.color.orange
                    ))
                val processThickness = this.getDimension(R.styleable.TimerView_processThickness, 20.0f)
                processPaint.color = processColor
                processPaint.strokeWidth = processThickness
                circlePaint.strokeWidth = processThickness
                circlePaint.color = ContextCompat.getColor(context, R.color.gray_40)
            } finally {
                this.recycle()
            }
        }
    }

    fun setCurrent(current: Float) {
        angle = current * 360f
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        val r = min(width, height) / 2.0f - circlePaint.strokeWidth
        val centerX = width / 2.0f
        val centerY = height / 2.0f
        canvas.drawCircle(centerX, centerY, r, circlePaint)
        canvas.drawArc(
            centerX - r,
            centerY - r,
            centerX + r,
            centerY + r,
            270f,
            angle,
            false,
            processPaint
        )
    }
}