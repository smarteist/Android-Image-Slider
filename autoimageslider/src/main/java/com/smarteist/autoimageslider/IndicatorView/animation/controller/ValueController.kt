package com.smarteist.autoimageslider.IndicatorView.animation.controller

import com.smarteist.autoimageslider.IndicatorView.animation.data.Value
import com.smarteist.autoimageslider.IndicatorView.animation.type.*

class ValueController(private val updateListener: UpdateListener?) {
    private var colorAnimation: ColorAnimation? = null
    private var scaleAnimation: ScaleAnimation? = null
    private var wormAnimation: WormAnimation? = null
    private var slideAnimation: SlideAnimation? = null
    private var fillAnimation: FillAnimation? = null
    private var thinWormAnimation: ThinWormAnimation? = null
    private var dropAnimation: DropAnimation? = null
    private var swapAnimation: SwapAnimation? = null
    private var scaleDownAnimation: ScaleDownAnimation? = null

    interface UpdateListener {
        fun onValueUpdated(value: Value?)
    }

    fun color(): ColorAnimation {
        if (colorAnimation == null) {
            colorAnimation = ColorAnimation(updateListener)
        }
        return colorAnimation!!
    }

    fun scale(): ScaleAnimation {
        if (scaleAnimation == null) {
            scaleAnimation = ScaleAnimation(updateListener!!)
        }
        return scaleAnimation!!
    }

    fun worm(): WormAnimation {
        if (wormAnimation == null) {
            wormAnimation = WormAnimation(updateListener!!)
        }
        return wormAnimation!!
    }

    fun slide(): SlideAnimation {
        if (slideAnimation == null) {
            slideAnimation = SlideAnimation(updateListener!!)
        }
        return slideAnimation!!
    }

    fun fill(): FillAnimation {
        if (fillAnimation == null) {
            fillAnimation = FillAnimation(updateListener!!)
        }
        return fillAnimation!!
    }

    fun thinWorm(): ThinWormAnimation {
        if (thinWormAnimation == null) {
            thinWormAnimation = ThinWormAnimation(updateListener!!)
        }
        return thinWormAnimation!!
    }

    fun drop(): DropAnimation {
        if (dropAnimation == null) {
            dropAnimation = DropAnimation(updateListener!!)
        }
        return dropAnimation!!
    }

    fun swap(): SwapAnimation {
        if (swapAnimation == null) {
            swapAnimation = SwapAnimation(updateListener!!)
        }
        return swapAnimation!!
    }

    fun scaleDown(): ScaleDownAnimation {
        if (scaleDownAnimation == null) {
            scaleDownAnimation = ScaleDownAnimation(updateListener!!)
        }
        return scaleDownAnimation!!
    }
}