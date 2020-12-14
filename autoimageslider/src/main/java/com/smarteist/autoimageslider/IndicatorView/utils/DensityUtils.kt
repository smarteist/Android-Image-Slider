package com.smarteist.autoimageslider.IndicatorView.utils

import android.content.res.Resources
import android.util.TypedValue

object DensityUtils {
    @kotlin.jvm.JvmStatic
    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), Resources.getSystem().displayMetrics).toInt()
    }

    fun pxToDp(px: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, Resources.getSystem().displayMetrics).toInt()
    }
}