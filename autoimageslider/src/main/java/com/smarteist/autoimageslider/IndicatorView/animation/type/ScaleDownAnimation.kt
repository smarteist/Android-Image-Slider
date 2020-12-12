package com.smarteist.autoimageslider.IndicatorView.animation.type

import android.animation.IntEvaluator
import android.animation.PropertyValuesHolder
import com.smarteist.autoimageslider.IndicatorView.animation.controller.ValueController.UpdateListener

class ScaleDownAnimation(listener: UpdateListener) : ScaleAnimation(listener) {
    override fun createScalePropertyHolder(isReverse: Boolean): PropertyValuesHolder {
        val propertyName: String
        val startRadiusValue: Int
        val endRadiusValue: Int
        if (isReverse) {
            propertyName = ScaleAnimation.Companion.ANIMATION_SCALE_REVERSE
            startRadiusValue = (radius * scaleFactor).toInt()
            endRadiusValue = radius
        } else {
            propertyName = ScaleAnimation.Companion.ANIMATION_SCALE
            startRadiusValue = radius
            endRadiusValue = (radius * scaleFactor).toInt()
        }
        val holder = PropertyValuesHolder.ofInt(propertyName, startRadiusValue, endRadiusValue)
        holder.setEvaluator(IntEvaluator())
        return holder
    }
}