package com.smarteist.autoimageslider.IndicatorView.animation

import com.smarteist.autoimageslider.IndicatorView.animation.controller.AnimationController
import com.smarteist.autoimageslider.IndicatorView.animation.controller.ValueController.UpdateListener
import com.smarteist.autoimageslider.IndicatorView.draw.data.Indicator

class AnimationManager(indicator: Indicator, listener: UpdateListener) {
    private val animationController: AnimationController?
    fun basic() {
        if (animationController != null) {
            animationController.end()
            animationController.basic()
        }
    }

    fun interactive(progress: Float) {
        animationController?.interactive(progress)
    }

    fun end() {
        animationController?.end()
    }

    init {
        animationController = AnimationController(indicator, listener)
    }
}