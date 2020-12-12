package com.smarteist.autoimageslider.IndicatorView.animation.type

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.smarteist.autoimageslider.IndicatorView.animation.controller.ValueController.UpdateListener
import com.smarteist.autoimageslider.IndicatorView.animation.data.type.WormAnimationValue

open class WormAnimation(listener: UpdateListener) : BaseAnimation<AnimatorSet>(listener) {
    var coordinateStart = 0
    var coordinateEnd = 0
    var radius = 0
    var isRightSide = false
    var rectLeftEdge = 0
    var rectRightEdge = 0
    private val value: WormAnimationValue
    override fun createAnimator(): AnimatorSet {
        val animator = AnimatorSet()
        animator.interpolator = AccelerateDecelerateInterpolator()
        return animator
    }

    override fun duration(duration: Long): WormAnimation {
        super.duration(duration)
        return this
    }

    open fun with(coordinateStart: Int, coordinateEnd: Int, radius: Int, isRightSide: Boolean): WormAnimation {
        if (hasChanges(coordinateStart, coordinateEnd, radius, isRightSide)) {
            animator = createAnimator()
            this.coordinateStart = coordinateStart
            this.coordinateEnd = coordinateEnd
            this.radius = radius
            this.isRightSide = isRightSide
            rectLeftEdge = coordinateStart - radius
            rectRightEdge = coordinateStart + radius
            value.rectStart = rectLeftEdge
            value.rectEnd = rectRightEdge
            val rect = createRectValues(isRightSide)
            val duration = animationDuration / 2
            val straightAnimator = createWormAnimator(rect.fromX, rect.toX, duration, false, value)
            val reverseAnimator = createWormAnimator(rect.reverseFromX, rect.reverseToX, duration, true, value)
            animator!!.playSequentially(straightAnimator, reverseAnimator)
        }
        return this
    }

    override fun progress(progress: Float): WormAnimation {
        if (animator == null) {
            return this
        }
        var progressDuration = (progress * animationDuration).toLong()
        for (anim in animator!!.childAnimations) {
            val animator = anim as ValueAnimator
            val duration = animator.duration
            var setDuration = progressDuration
            if (setDuration > duration) {
                setDuration = duration
            }
            animator.currentPlayTime = setDuration
            progressDuration -= setDuration
        }
        return this
    }

    fun createWormAnimator(
            fromValue: Int,
            toValue: Int,
            duration: Long,
            isReverse: Boolean,
            value: WormAnimationValue): ValueAnimator {
        val anim = ValueAnimator.ofInt(fromValue, toValue)
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.duration = duration
        anim.addUpdateListener { animation -> onAnimateUpdated(value, animation, isReverse) }
        return anim
    }

    private fun onAnimateUpdated(value: WormAnimationValue, animation: ValueAnimator, isReverse: Boolean) {
        val rectEdge = animation.animatedValue as Int
        if (isRightSide) {
            if (!isReverse) {
                value.rectEnd = rectEdge
            } else {
                value.rectStart = rectEdge
            }
        } else {
            if (!isReverse) {
                value.rectStart = rectEdge
            } else {
                value.rectEnd = rectEdge
            }
        }
        if (listener != null) {
            listener!!.onValueUpdated(value)
        }
    }

    fun hasChanges(coordinateStart: Int, coordinateEnd: Int, radius: Int, isRightSide: Boolean): Boolean {
        if (this.coordinateStart != coordinateStart) {
            return true
        }
        if (this.coordinateEnd != coordinateEnd) {
            return true
        }
        if (this.radius != radius) {
            return true
        }
        return if (this.isRightSide != isRightSide) {
            true
        } else false
    }

    fun createRectValues(isRightSide: Boolean): RectValues {
        val fromX: Int
        val toX: Int
        val reverseFromX: Int
        val reverseToX: Int
        if (isRightSide) {
            fromX = coordinateStart + radius
            toX = coordinateEnd + radius
            reverseFromX = coordinateStart - radius
            reverseToX = coordinateEnd - radius
        } else {
            fromX = coordinateStart - radius
            toX = coordinateEnd - radius
            reverseFromX = coordinateStart + radius
            reverseToX = coordinateEnd + radius
        }
        return RectValues(fromX, toX, reverseFromX, reverseToX)
    }

    inner class RectValues(val fromX: Int, val toX: Int, val reverseFromX: Int, val reverseToX: Int)

    init {
        value = WormAnimationValue()
    }
}