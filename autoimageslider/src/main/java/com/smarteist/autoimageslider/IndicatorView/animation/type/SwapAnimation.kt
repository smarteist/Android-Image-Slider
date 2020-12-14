package com.smarteist.autoimageslider.IndicatorView.animation.type

import android.animation.IntEvaluator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.smarteist.autoimageslider.IndicatorView.animation.controller.ValueController.UpdateListener
import com.smarteist.autoimageslider.IndicatorView.animation.data.type.SwapAnimationValue

class SwapAnimation(listener: UpdateListener) : BaseAnimation<ValueAnimator>(listener) {
    private var coordinateStart = COORDINATE_NONE
    private var coordinateEnd = COORDINATE_NONE
    private val value: SwapAnimationValue
    override fun createAnimator(): ValueAnimator {
        val animator = ValueAnimator()
        animator.duration = BaseAnimation.Companion.DEFAULT_ANIMATION_TIME.toLong()
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation -> onAnimateUpdated(animation) }
        return animator
    }

    override fun progress(progress: Float): SwapAnimation {
        if (animator != null) {
            val playTime = (progress * animationDuration).toLong()
            if (animator!!.values != null && animator!!.values.size > 0) {
                animator!!.currentPlayTime = playTime
            }
        }
        return this
    }

    fun with(coordinateStart: Int, coordinateEnd: Int): SwapAnimation {
        if (animator != null && hasChanges(coordinateStart, coordinateEnd)) {
            this.coordinateStart = coordinateStart
            this.coordinateEnd = coordinateEnd
            val holder = createColorPropertyHolder(ANIMATION_COORDINATE, coordinateStart, coordinateEnd)
            val holderReverse = createColorPropertyHolder(ANIMATION_COORDINATE_REVERSE, coordinateEnd, coordinateStart)
            animator!!.setValues(holder, holderReverse)
        }
        return this
    }

    private fun createColorPropertyHolder(propertyName: String, startValue: Int, endValue: Int): PropertyValuesHolder {
        val holder = PropertyValuesHolder.ofInt(propertyName, startValue, endValue)
        holder.setEvaluator(IntEvaluator())
        return holder
    }

    private fun onAnimateUpdated(animation: ValueAnimator) {
        val coordinate = animation.getAnimatedValue(ANIMATION_COORDINATE) as Int
        val coordinateReverse = animation.getAnimatedValue(ANIMATION_COORDINATE_REVERSE) as Int
        value.coordinate = coordinate
        value.coordinateReverse = coordinateReverse
        if (listener != null) {
            listener!!.onValueUpdated(value)
        }
    }

    private fun hasChanges(coordinateStart: Int, coordinateEnd: Int): Boolean {
        if (this.coordinateStart != coordinateStart) {
            return true
        }
        return if (this.coordinateEnd != coordinateEnd) {
            true
        } else false
    }

    companion object {
        private const val ANIMATION_COORDINATE = "ANIMATION_COORDINATE"
        private const val ANIMATION_COORDINATE_REVERSE = "ANIMATION_COORDINATE_REVERSE"
        private const val COORDINATE_NONE = -1
    }

    init {
        value = SwapAnimationValue()
    }
}