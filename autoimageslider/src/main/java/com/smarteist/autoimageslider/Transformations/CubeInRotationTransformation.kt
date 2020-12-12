package com.smarteist.autoimageslider.Transformations

import android.view.View
import com.smarteist.autoimageslider.SliderPager

class CubeInRotationTransformation : SliderPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.cameraDistance = 20000f
        when {
            position < -1 -> {     // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0f
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1f
                page.pivotX = page.width.toFloat()
                page.rotationY = 90 * Math.abs(position)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1f
                page.pivotX = 0f
                page.rotationY = -90 * Math.abs(position)
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0f
            }
        }
    }
}