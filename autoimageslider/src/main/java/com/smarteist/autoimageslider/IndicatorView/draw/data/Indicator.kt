package com.smarteist.autoimageslider.IndicatorView.draw.data

import android.view.View
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType

class Indicator {
    var height = 0
    var width = 0
    var radius = 0
    var padding = 0
    var paddingLeft = 0
    var paddingTop = 0
    var paddingRight = 0
    var paddingBottom = 0
    var strokeHere = 0 //For "Fill" animation only
    var scaleFactor = 0f //For "Scale" animation only
    var unselectedColor = 0
    var selectedColor = 0
    var isInteractiveAnimation = false
    var isAutoVisibility = false
    var isDynamicCount = false
    var animationDuration: Long = 0
    var count = DEFAULT_COUNT
    var selectedPosition = 0
    var selectingPosition = 0
    var lastSelectedPosition = 0
    var viewPagerId = View.NO_ID
    var orientation: Orientation? = null
        get() {
            if (field == null) {
                field = Orientation.HORIZONTAL
            }
            return field
        }
    var animationType: IndicatorAnimationType? = null
        get() {
            if (field == null) {
                field = IndicatorAnimationType.NONE
            }
            return field
        }
    var rtlMode: RtlMode? = null
        get() {
            if (field == null) {
                field = RtlMode.Off
            }
            return field
        }

    companion object {
        const val DEFAULT_COUNT = 3
        const val MIN_COUNT = 1
        const val COUNT_NONE = -1
        const val DEFAULT_RADIUS_DP = 6
        const val DEFAULT_PADDING_DP = 8
    }
}