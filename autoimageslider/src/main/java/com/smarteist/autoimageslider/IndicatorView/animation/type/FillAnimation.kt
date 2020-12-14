package com.smarteist.autoimageslider.IndicatorView.animation.type

import android.animation.IntEvaluator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.smarteist.autoimageslider.IndicatorView.animation.controller.ValueController.UpdateListener
import com.smarteist.autoimageslider.IndicatorView.animation.data.type.FillAnimationValue

class FillAnimation(listener: UpdateListener) : ColorAnimation(listener) {
    private val value: FillAnimationValue
    private var radius = 0
    private var stroke = 0
    override fun createAnimator(): ValueAnimator {
        val animator = ValueAnimator()
        animator.duration = BaseAnimation.Companion.DEFAULT_ANIMATION_TIME.toLong()
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation -> onAnimateUpdated(animation) }
        return animator
    }

    fun with(colorStart: Int, colorEnd: Int, radius: Int, stroke: Int): FillAnimation {
        if (animator != null && hasChanges(colorStart, colorEnd, radius, stroke)) {
            this.colorStart = colorStart
            this.colorEnd = colorEnd
            this.radius = radius
            this.stroke = stroke
            val colorHolder = createColorPropertyHolder(false)
            val reverseColorHolder = createColorPropertyHolder(true)
            val radiusHolder = createRadiusPropertyHolder(false)
            val radiusReverseHolder = createRadiusPropertyHolder(true)
            val strokeHolder = createStrokePropertyHolder(false)
            val strokeReverseHolder = createStrokePropertyHolder(true)
            animator!!.setValues(
                    colorHolder,
                    reverseColorHolder,
                    radiusHolder,
                    radiusReverseHolder,
                    strokeHolder,
                    strokeReverseHolder)
        }
        return this
    }

    private fun createRadiusPropertyHolder(isReverse: Boolean): PropertyValuesHolder {
        val propertyName: String
        val startRadiusValue: Int
        val endRadiusValue: Int
        if (isReverse) {
            propertyName = ANIMATION_RADIUS_REVERSE
            startRadiusValue = radius / 2
            endRadiusValue = radius
        } else {
            propertyName = ANIMATION_RADIUS
            startRadiusValue = radius
            endRadiusValue = radius / 2
        }
        val holder = PropertyValuesHolder.ofInt(propertyName, startRadiusValue, endRadiusValue)
        holder.setEvaluator(IntEvaluator())
        return holder
    }

    private fun createStrokePropertyHolder(isReverse: Boolean): PropertyValuesHolder {
        val propertyName: String
        val startStrokeValue: Int
        val endStrokeValue: Int
        if (isReverse) {
            propertyName = ANIMATION_STROKE_REVERSE
            startStrokeValue = radius
            endStrokeValue = 0
        } else {
            propertyName = ANIMATION_STROKE
            startStrokeValue = 0
            endStrokeValue = radius
        }
        val holder = PropertyValuesHolder.ofInt(propertyName, startStrokeValue, endStrokeValue)
        holder.setEvaluator(IntEvaluator())
        return holder
    }

    private fun onAnimateUpdated(animation: ValueAnimator) {
        val color = animation.getAnimatedValue(ColorAnimation.Companion.ANIMATION_COLOR) as Int
        val colorReverse = animation.getAnimatedValue(ColorAnimation.Companion.ANIMATION_COLOR_REVERSE) as Int
        val radius = animation.getAnimatedValue(ANIMATION_RADIUS) as Int
        val radiusReverse = animation.getAnimatedValue(ANIMATION_RADIUS_REVERSE) as Int
        val stroke = animation.getAnimatedValue(ANIMATION_STROKE) as Int
        val strokeReverse = animation.getAnimatedValue(ANIMATION_STROKE_REVERSE) as Int
        value.color = color
        value.colorReverse = colorReverse
        value.radius = radius
        value.radiusReverse = radiusReverse
        value.stroke = stroke
        value.strokeReverse = strokeReverse
        if (listener != null) {
            listener!!.onValueUpdated(value)
        }
    }

    private fun hasChanges(colorStart: Int, colorEnd: Int, radiusValue: Int, strokeValue: Int): Boolean {
        if (this.colorStart != colorStart) {
            return true
        }
        if (this.colorEnd != colorEnd) {
            return true
        }
        if (radius != radiusValue) {
            return true
        }
        return if (stroke != strokeValue) {
            true
        } else false
    }

    companion object {
        private const val ANIMATION_RADIUS_REVERSE = "ANIMATION_RADIUS_REVERSE"
        private const val ANIMATION_RADIUS = "ANIMATION_RADIUS"
        private const val ANIMATION_STROKE_REVERSE = "ANIMATION_STROKE_REVERSE"
        private const val ANIMATION_STROKE = "ANIMATION_STROKE"
        const val DEFAULT_STROKE_DP = 1
    }

    init {
        value = FillAnimationValue()
    }
}