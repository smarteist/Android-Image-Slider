package com.smarteist.autoimageslider.IndicatorView.draw.drawer.type

import android.graphics.Canvas
import android.graphics.Paint
import com.smarteist.autoimageslider.IndicatorView.animation.data.Value
import com.smarteist.autoimageslider.IndicatorView.animation.data.type.ThinWormAnimationValue
import com.smarteist.autoimageslider.IndicatorView.draw.data.Indicator
import com.smarteist.autoimageslider.IndicatorView.draw.data.Orientation

class ThinWormDrawer(paint: Paint, indicator: Indicator) : WormDrawer(paint, indicator) {
    override fun draw(
            canvas: Canvas,
            value: Value,
            coordinateX: Int,
            coordinateY: Int) {
        if (value !is ThinWormAnimationValue) {
            return
        }
        val v = value
        val rectStart = v.rectStart
        val rectEnd = v.rectEnd
        val height = v.height / 2
        val radius = indicator.radius
        val unselectedColor = indicator.unselectedColor
        val selectedColor = indicator.selectedColor
        if (indicator.orientation == Orientation.HORIZONTAL) {
            rect.left = rectStart.toFloat()
            rect.right = rectEnd.toFloat()
            rect.top = (coordinateY - height).toFloat()
            rect.bottom = (coordinateY + height).toFloat()
        } else {
            rect.left = (coordinateX - height).toFloat()
            rect.right = (coordinateX + height).toFloat()
            rect.top = rectStart.toFloat()
            rect.bottom = rectEnd.toFloat()
        }
        paint.color = unselectedColor
        canvas.drawCircle(coordinateX.toFloat(), coordinateY.toFloat(), radius.toFloat(), paint)
        paint.color = selectedColor
        canvas.drawRoundRect(rect, radius.toFloat(), radius.toFloat(), paint)
    }
}