package com.smarteist.autoimageslider.IndicatorView

import com.smarteist.autoimageslider.IndicatorView.animation.AnimationManager
import com.smarteist.autoimageslider.IndicatorView.animation.controller.ValueController.UpdateListener
import com.smarteist.autoimageslider.IndicatorView.animation.data.Value
import com.smarteist.autoimageslider.IndicatorView.draw.DrawManager
import com.smarteist.autoimageslider.IndicatorView.draw.data.Indicator

class IndicatorManager internal constructor(private val listener: Listener?) : UpdateListener {
    private val drawManager: DrawManager = DrawManager()
    private val animationManager: AnimationManager

    internal interface Listener {
        fun onIndicatorUpdated()
    }

    fun animate(): AnimationManager {
        return animationManager
    }

    fun indicator(): Indicator {
        return drawManager.indicator()
    }

    fun drawer(): DrawManager {
        return drawManager
    }

    override fun onValueUpdated(value: Value?) {
        drawManager.updateValue(value)
        listener?.onIndicatorUpdated()
    }

    init {
        animationManager = AnimationManager(drawManager.indicator(), this)
    }
}