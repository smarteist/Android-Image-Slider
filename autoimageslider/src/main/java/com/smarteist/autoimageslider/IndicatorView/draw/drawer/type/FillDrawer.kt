package com.smarteist.autoimageslider.IndicatorView.draw.drawer.type

import android.graphics.Canvas
import android.graphics.Paint
import com.smarteist.autoimageslider.IndicatorView.animation.data.Value
import com.smarteist.autoimageslider.IndicatorView.animation.data.type.FillAnimationValue
import com.smarteist.autoimageslider.IndicatorView.draw.data.Indicator

class FillDrawer(paint: Paint, indicator: Indicator) : BaseDrawer(paint, indicator) {
    private val strokePaint: Paint
    fun draw(
            canvas: Canvas,
            value: Value,
            position: Int,
            coordinateX: Int,
            coordinateY: Int) {
        if (value !is FillAnimationValue) {
            return
        }
        val v = value
        var color = indicator.unselectedColor
        var radius = indicator.radius.toFloat()
        var stroke = indicator.strokeHere
        val selectedPosition = indicator.selectedPosition
        val selectingPosition = indicator.selectingPosition
        val lastSelectedPosition = indicator.lastSelectedPosition
        if (indicator.isInteractiveAnimation) {
            if (position == selectingPosition) {
                color = v.color
                radius = v.radius.toFloat()
                stroke = v.stroke
            } else if (position == selectedPosition) {
                color = v.colorReverse
                radius = v.radiusReverse.toFloat()
                stroke = v.strokeReverse
            }
        } else {
            if (position == selectedPosition) {
                color = v.color
                radius = v.radius.toFloat()
                stroke = v.stroke
            } else if (position == lastSelectedPosition) {
                color = v.colorReverse
                radius = v.radiusReverse.toFloat()
                stroke = v.strokeReverse
            }
        }
        strokePaint.color = color
        strokePaint.strokeWidth = indicator.strokeHere.toFloat()
        canvas.drawCircle(coordinateX.toFloat(), coordinateY.toFloat(), indicator.radius.toFloat(), strokePaint)
        strokePaint.strokeWidth = stroke.toFloat()
        canvas.drawCircle(coordinateX.toFloat(), coordinateY.toFloat(), radius, strokePaint)
    }

    init {
        strokePaint = Paint()
        strokePaint.style = Paint.Style.STROKE
        strokePaint.isAntiAlias = true
    }
}