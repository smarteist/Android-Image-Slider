package com.smarteist.autoimageslider.IndicatorView.animation.type

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.smarteist.autoimageslider.IndicatorView.animation.controller.ValueController.UpdateListener
import com.smarteist.autoimageslider.IndicatorView.animation.data.type.DropAnimationValue

class DropAnimation(listener: UpdateListener) : BaseAnimation<AnimatorSet>(listener) {
    private var widthStart = 0
    private var widthEnd = 0
    private var heightStart = 0
    private var heightEnd = 0
    private var radius = 0

    private enum class AnimationType {
        Width, Height, Radius
    }

    private val value: DropAnimationValue
    override fun createAnimator(): AnimatorSet {
        val animator = AnimatorSet()
        animator.interpolator = AccelerateDecelerateInterpolator()
        return animator
    }

    override fun progress(progress: Float): DropAnimation {
        if (animator != null) {
            val playTimeLeft = (progress * animationDuration).toLong()
            var isReverse = false
            for (anim in animator!!.childAnimations) {
                val animator = anim as ValueAnimator
                val animDuration = animator.duration
                var currPlayTime = playTimeLeft
                if (isReverse) {
                    currPlayTime -= animDuration
                }
                if (currPlayTime < 0) {
                    continue
                } else if (currPlayTime >= animDuration) {
                    currPlayTime = animDuration
                }
                if (animator.values != null && animator.values.size > 0) {
                    animator.currentPlayTime = currPlayTime
                }
                if (!isReverse && animDuration >= animationDuration) {
                    isReverse = true
                }
            }
        }
        return this
    }

    override fun duration(duration: Long): DropAnimation {
        super.duration(duration)
        return this
    }

    fun with(widthStart: Int, widthEnd: Int, heightStart: Int, heightEnd: Int, radius: Int): DropAnimation {
        if (hasChanges(widthStart, widthEnd, heightStart, heightEnd, radius)) {
            animator = createAnimator()
            this.widthStart = widthStart
            this.widthEnd = widthEnd
            this.heightStart = heightStart
            this.heightEnd = heightEnd
            this.radius = radius
            val fromRadius = radius
            val toRadius = (radius / 1.5).toInt()
            val halfDuration = animationDuration / 2
            val widthAnimator = createValueAnimation(widthStart, widthEnd, animationDuration, AnimationType.Width)
            val heightForwardAnimator = createValueAnimation(heightStart, heightEnd, halfDuration, AnimationType.Height)
            val radiusForwardAnimator = createValueAnimation(fromRadius, toRadius, halfDuration, AnimationType.Radius)
            val heightBackwardAnimator = createValueAnimation(heightEnd, heightStart, halfDuration, AnimationType.Height)
            val radiusBackwardAnimator = createValueAnimation(toRadius, fromRadius, halfDuration, AnimationType.Radius)
            animator!!.play(heightForwardAnimator)
                    .with(radiusForwardAnimator)
                    .with(widthAnimator)
                    .before(heightBackwardAnimator)
                    .before(radiusBackwardAnimator)
        }
        return this
    }

    private fun createValueAnimation(fromValue: Int, toValue: Int, duration: Long, type: AnimationType): ValueAnimator {
        val anim = ValueAnimator.ofInt(fromValue, toValue)
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.duration = duration
        anim.addUpdateListener { animation -> onAnimatorUpdate(animation, type) }
        return anim
    }

    private fun onAnimatorUpdate(animation: ValueAnimator, type: AnimationType) {
        val frameValue = animation.animatedValue as Int
        when (type) {
            AnimationType.Width -> value.width = frameValue
            AnimationType.Height -> value.height = frameValue
            AnimationType.Radius -> value.radius = frameValue
        }
        if (listener != null) {
            listener!!.onValueUpdated(value)
        }
    }

    private fun hasChanges(widthStart: Int, widthEnd: Int, heightStart: Int, heightEnd: Int, radius: Int): Boolean {
        if (this.widthStart != widthStart) {
            return true
        }
        if (this.widthEnd != widthEnd) {
            return true
        }
        if (this.heightStart != heightStart) {
            return true
        }
        if (this.heightEnd != heightEnd) {
            return true
        }
        return if (this.radius != radius) {
            true
        } else false
    }

    init {
        value = DropAnimationValue()
    }
}