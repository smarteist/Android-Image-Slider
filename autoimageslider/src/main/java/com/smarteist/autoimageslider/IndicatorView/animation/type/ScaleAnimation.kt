package com.smarteist.autoimageslider.IndicatorView.animation.type

import android.animation.IntEvaluator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.smarteist.autoimageslider.IndicatorView.animation.controller.ValueController.UpdateListener
import com.smarteist.autoimageslider.IndicatorView.animation.data.type.ScaleAnimationValue

open class ScaleAnimation(listener: UpdateListener) : ColorAnimation(listener) {
    var radius = 0
    var scaleFactor = 0f
    private val value: ScaleAnimationValue
    override fun createAnimator(): ValueAnimator {
        val animator = ValueAnimator()
        animator.duration = BaseAnimation.Companion.DEFAULT_ANIMATION_TIME.toLong()
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation -> onAnimateUpdated(animation) }
        return animator
    }

    fun with(colorStart: Int, colorEnd: Int, radius: Int, scaleFactor: Float): ScaleAnimation {
        if (animator != null && hasChanges(colorStart, colorEnd, radius, scaleFactor)) {
            this.colorStart = colorStart
            this.colorEnd = colorEnd
            this.radius = radius
            this.scaleFactor = scaleFactor
            val colorHolder = createColorPropertyHolder(false)
            val reverseColorHolder = createColorPropertyHolder(true)
            val scaleHolder = createScalePropertyHolder(false)
            val scaleReverseHolder = createScalePropertyHolder(true)
            animator!!.setValues(colorHolder, reverseColorHolder, scaleHolder, scaleReverseHolder)
        }
        return this
    }

    private fun onAnimateUpdated(animation: ValueAnimator) {
        val color = animation.getAnimatedValue(ColorAnimation.Companion.ANIMATION_COLOR) as Int
        val colorReverse = animation.getAnimatedValue(ColorAnimation.Companion.ANIMATION_COLOR_REVERSE) as Int
        val radius = animation.getAnimatedValue(ANIMATION_SCALE) as Int
        val radiusReverse = animation.getAnimatedValue(ANIMATION_SCALE_REVERSE) as Int
        value.color = color
        value.colorReverse = colorReverse
        value.radius = radius
        value.radiusReverse = radiusReverse
        if (listener != null) {
            listener!!.onValueUpdated(value)
        }
    }

    protected open fun createScalePropertyHolder(isReverse: Boolean): PropertyValuesHolder {
        val propertyName: String
        val startRadiusValue: Int
        val endRadiusValue: Int
        if (isReverse) {
            propertyName = ANIMATION_SCALE_REVERSE
            startRadiusValue = radius
            endRadiusValue = (radius * scaleFactor).toInt()
        } else {
            propertyName = ANIMATION_SCALE
            startRadiusValue = (radius * scaleFactor).toInt()
            endRadiusValue = radius
        }
        val holder = PropertyValuesHolder.ofInt(propertyName, startRadiusValue, endRadiusValue)
        holder.setEvaluator(IntEvaluator())
        return holder
    }

    private fun hasChanges(colorStart: Int, colorEnd: Int, radiusValue: Int, scaleFactorValue: Float): Boolean {
        if (this.colorStart != colorStart) {
            return true
        }
        if (this.colorEnd != colorEnd) {
            return true
        }
        if (radius != radiusValue) {
            return true
        }
        return if (scaleFactor != scaleFactorValue) {
            true
        } else false
    }

    companion object {
        const val DEFAULT_SCALE_FACTOR = 0.7f
        const val MIN_SCALE_FACTOR = 0.3f
        const val MAX_SCALE_FACTOR = 1f
        const val ANIMATION_SCALE_REVERSE = "ANIMATION_SCALE_REVERSE"
        const val ANIMATION_SCALE = "ANIMATION_SCALE"
    }

    init {
        value = ScaleAnimationValue()
    }
}