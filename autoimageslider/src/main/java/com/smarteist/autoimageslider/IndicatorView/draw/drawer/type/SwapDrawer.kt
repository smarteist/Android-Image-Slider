package com.smarteist.autoimageslider.IndicatorView.draw.drawer.type

import android.graphics.Canvas
import android.graphics.Paint
import com.smarteist.autoimageslider.IndicatorView.animation.data.Value
import com.smarteist.autoimageslider.IndicatorView.animation.data.type.SwapAnimationValue
import com.smarteist.autoimageslider.IndicatorView.draw.data.Indicator
import com.smarteist.autoimageslider.IndicatorView.draw.data.Orientation

class SwapDrawer(paint: Paint, indicator: Indicator) : BaseDrawer(paint, indicator) {
    fun draw(
            canvas: Canvas,
            value: Value,
            position: Int,
            coordinateX: Int,
            coordinateY: Int) {
        if (value !is SwapAnimationValue) {
            return
        }
        val v = value
        val selectedColor = indicator.selectedColor
        val unselectedColor = indicator.unselectedColor
        val radius = indicator.radius
        val selectedPosition = indicator.selectedPosition
        val selectingPosition = indicator.selectingPosition
        val lastSelectedPosition = indicator.lastSelectedPosition
        var coordinate = v.coordinate
        var color = unselectedColor
        if (indicator.isInteractiveAnimation) {
            if (position == selectingPosition) {
                coordinate = v.coordinate
                color = selectedColor
            } else if (position == selectedPosition) {
                coordinate = v.coordinateReverse
                color = unselectedColor
            }
        } else {
            if (position == lastSelectedPosition) {
                coordinate = v.coordinate
                color = selectedColor
            } else if (position == selectedPosition) {
                coordinate = v.coordinateReverse
                color = unselectedColor
            }
        }
        paint.color = color
        if (indicator.orientation == Orientation.HORIZONTAL) {
            canvas.drawCircle(coordinate.toFloat(), coordinateY.toFloat(), radius.toFloat(), paint)
        } else {
            canvas.drawCircle(coordinateX.toFloat(), coordinate.toFloat(), radius.toFloat(), paint)
        }
    }
}