package com.smarteist.autoimageslider.IndicatorView.draw.controller

import android.graphics.Canvas
import android.view.MotionEvent
import com.smarteist.autoimageslider.IndicatorView.animation.data.Value
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.IndicatorView.draw.data.Indicator
import com.smarteist.autoimageslider.IndicatorView.draw.drawer.Drawer
import com.smarteist.autoimageslider.IndicatorView.utils.CoordinatesUtils

class DrawController(private val indicator: Indicator) {
    private var value: Value? = null
    private val drawer: Drawer
    private var listener: ClickListener? = null

    interface ClickListener {
        fun onIndicatorClicked(position: Int)
    }

    fun updateValue(value: Value?) {
        this.value = value
    }

    fun setClickListener(listener: ClickListener?) {
        this.listener = listener
    }

    fun touch(event: MotionEvent?) {
        if (event == null) {
            return
        }
        if (event.action == MotionEvent.ACTION_UP) {
            onIndicatorTouched(event.x, event.y)
        }
    }

    private fun onIndicatorTouched(x: Float, y: Float) {
        if (listener != null) {
            val position = CoordinatesUtils.getPosition(indicator, x, y)
            if (position >= 0) {
                listener!!.onIndicatorClicked(position)
            }
        }
    }

    fun draw(canvas: Canvas) {
        val count = indicator.count
        for (position in 0 until count) {
            val coordinateX = CoordinatesUtils.getXCoordinate(indicator, position)
            val coordinateY = CoordinatesUtils.getYCoordinate(indicator, position)
            drawIndicator(canvas, position, coordinateX, coordinateY)
        }
    }

    private fun drawIndicator(
            canvas: Canvas,
            position: Int,
            coordinateX: Int,
            coordinateY: Int) {
        val interactiveAnimation = indicator.isInteractiveAnimation
        val selectedPosition = indicator.selectedPosition
        val selectingPosition = indicator.selectingPosition
        val lastSelectedPosition = indicator.lastSelectedPosition
        val selectedItem = !interactiveAnimation && (position == selectedPosition || position == lastSelectedPosition)
        val selectingItem = interactiveAnimation && (position == selectedPosition || position == selectingPosition)
        val isSelectedItem = selectedItem or selectingItem
        drawer.setup(position, coordinateX, coordinateY)
        if (value != null && isSelectedItem) {
            drawWithAnimation(canvas)
        } else {
            drawer.drawBasic(canvas, isSelectedItem)
        }
    }

    private fun drawWithAnimation(canvas: Canvas) {
        val animationType = indicator.animationType
        when (animationType) {
            IndicatorAnimationType.NONE -> drawer.drawBasic(canvas, true)
            IndicatorAnimationType.COLOR -> drawer.drawColor(canvas, value!!)
            IndicatorAnimationType.SCALE -> drawer.drawScale(canvas, value!!)
            IndicatorAnimationType.WORM -> drawer.drawWorm(canvas, value!!)
            IndicatorAnimationType.SLIDE -> drawer.drawSlide(canvas, value!!)
            IndicatorAnimationType.FILL -> drawer.drawFill(canvas, value!!)
            IndicatorAnimationType.THIN_WORM -> drawer.drawThinWorm(canvas, value!!)
            IndicatorAnimationType.DROP -> drawer.drawDrop(canvas, value!!)
            IndicatorAnimationType.SWAP -> drawer.drawSwap(canvas, value!!)
            IndicatorAnimationType.SCALE_DOWN -> drawer.drawScaleDown(canvas, value!!)
        }
    }

    init {
        drawer = Drawer(indicator)
    }
}