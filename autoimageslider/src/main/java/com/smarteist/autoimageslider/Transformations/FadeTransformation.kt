package com.smarteist.autoimageslider.Transformations

import android.view.View
import com.smarteist.autoimageslider.SliderPager

class FadeTransformation : SliderPager.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        view.translationX = -position * view.width

        // Page is not an immediate sibling, just make transparent
        if (position < -1 || position > 1) {
            view.alpha = 0f
        } else if (position <= 0 || position <= 1) {

            // Calculate alpha.  Position is decimal in [-1,0] or [0,1]
            val alpha = if (position <= 0) position + 1 else 1 - position
            view.alpha = alpha
        } else if (position == 0f) {
            view.alpha = 1f
        }
    }
}