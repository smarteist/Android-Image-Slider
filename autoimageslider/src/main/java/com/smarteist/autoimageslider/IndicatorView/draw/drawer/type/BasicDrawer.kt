package com.smarteist.autoimageslider.IndicatorView.draw.drawer.type

import android.graphics.Canvas
import android.graphics.Paint
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.IndicatorView.draw.data.Indicator

class BasicDrawer(paint: Paint, indicator: Indicator) : BaseDrawer(paint, indicator) {
    private val strokePaint: Paint
    fun draw(
            canvas: Canvas,
            position: Int,
            isSelectedItem: Boolean,
            coordinateX: Int,
            coordinateY: Int) {
        var radius = indicator.radius.toFloat()
        val strokePx = indicator.strokeHere
        val scaleFactor = indicator.scaleFactor
        val selectedColor = indicator.selectedColor
        val unselectedColor = indicator.unselectedColor
        val selectedPosition = indicator.selectedPosition
        val animationType = indicator.animationType
        if (animationType === IndicatorAnimationType.SCALE && !isSelectedItem) {
            radius *= scaleFactor
        } else if (animationType === IndicatorAnimationType.SCALE_DOWN && isSelectedItem) {
            radius *= scaleFactor
        }
        var color = unselectedColor
        if (position == selectedPosition) {
            color = selectedColor
        }
        val paint: Paint?
        if (animationType === IndicatorAnimationType.FILL && position != selectedPosition) {
            paint = strokePaint
            paint.strokeWidth = strokePx.toFloat()
        } else {
            paint = this.paint
        }
        paint.setColor(color)
        canvas.drawCircle(coordinateX.toFloat(), coordinateY.toFloat(), radius, paint)
    }

    init {
        strokePaint = Paint()
        strokePaint.style = Paint.Style.STROKE
        strokePaint.isAntiAlias = true
        strokePaint.strokeWidth = indicator.strokeHere.toFloat()
    }
}