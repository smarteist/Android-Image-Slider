package com.smarteist.autoimageslider.IndicatorView.draw.drawer.type

import android.graphics.Canvas
import android.graphics.Paint
import com.smarteist.autoimageslider.IndicatorView.animation.data.Value
import com.smarteist.autoimageslider.IndicatorView.animation.data.type.ScaleAnimationValue
import com.smarteist.autoimageslider.IndicatorView.draw.data.Indicator

class ScaleDrawer(paint: Paint, indicator: Indicator) : BaseDrawer(paint, indicator) {
    fun draw(
            canvas: Canvas,
            value: Value,
            position: Int,
            coordinateX: Int,
            coordinateY: Int) {
        if (value !is ScaleAnimationValue) {
            return
        }
        val v = value
        var radius = indicator.radius.toFloat()
        var color = indicator.selectedColor
        val selectedPosition = indicator.selectedPosition
        val selectingPosition = indicator.selectingPosition
        val lastSelectedPosition = indicator.lastSelectedPosition
        if (indicator.isInteractiveAnimation) {
            if (position == selectingPosition) {
                radius = v.radius.toFloat()
                color = v.color
            } else if (position == selectedPosition) {
                radius = v.radiusReverse.toFloat()
                color = v.colorReverse
            }
        } else {
            if (position == selectedPosition) {
                radius = v.radius.toFloat()
                color = v.color
            } else if (position == lastSelectedPosition) {
                radius = v.radiusReverse.toFloat()
                color = v.colorReverse
            }
        }
        paint.color = color
        canvas.drawCircle(coordinateX.toFloat(), coordinateY.toFloat(), radius, paint)
    }
}