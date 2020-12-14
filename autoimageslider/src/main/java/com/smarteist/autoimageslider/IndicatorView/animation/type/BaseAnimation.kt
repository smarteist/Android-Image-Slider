package com.smarteist.autoimageslider.IndicatorView.animation.type

import android.animation.Animator
import android.animation.ValueAnimator
import com.smarteist.autoimageslider.IndicatorView.animation.controller.ValueController.UpdateListener

abstract class BaseAnimation<T : Animator?>(protected var listener: UpdateListener?) {
    protected var animationDuration = DEFAULT_ANIMATION_TIME.toLong()
    protected var animator: T?
    abstract fun createAnimator(): T
    abstract fun progress(progress: Float): BaseAnimation<*>
    open fun duration(duration: Long): BaseAnimation<*> {
        animationDuration = duration
        if (animator is ValueAnimator) {
            animator!!.duration = animationDuration
        }
        return this
    }

    fun start() {
        if (animator != null && !animator!!.isRunning) {
            animator!!.start()
        }
    }

    fun end() {
        if (animator != null && animator!!.isStarted) {
            animator!!.end()
        }
    }

    companion object {
        const val DEFAULT_ANIMATION_TIME = 350
    }

    init {
        animator = createAnimator()
    }
}