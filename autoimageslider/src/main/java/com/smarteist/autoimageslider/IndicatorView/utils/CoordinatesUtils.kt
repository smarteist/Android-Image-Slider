package com.smarteist.autoimageslider.IndicatorView.utils

import android.util.Pair
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.IndicatorView.draw.data.Indicator
import com.smarteist.autoimageslider.IndicatorView.draw.data.Orientation

object CoordinatesUtils {
    fun getCoordinate(indicator: Indicator?, position: Int): Int {
        if (indicator == null) {
            return 0
        }
        return if (indicator.orientation == Orientation.HORIZONTAL) {
            getXCoordinate(indicator, position)
        } else {
            getYCoordinate(indicator, position)
        }
    }

    fun getXCoordinate(indicator: Indicator?, position: Int): Int {
        if (indicator == null) {
            return 0
        }
        var coordinate: Int = if (indicator.orientation == Orientation.HORIZONTAL) {
            getHorizontalCoordinate(indicator, position)
        } else {
            getVerticalCoordinate(indicator)
        }
        coordinate += indicator.paddingLeft
        return coordinate
    }

    fun getYCoordinate(indicator: Indicator?, position: Int): Int {
        if (indicator == null) {
            return 0
        }
        var coordinate: Int = if (indicator.orientation == Orientation.HORIZONTAL) {
            getVerticalCoordinate(indicator)
        } else {
            getHorizontalCoordinate(indicator, position)
        }
        coordinate += indicator.paddingTop
        return coordinate
    }

    fun getPosition(indicator: Indicator?, x: Float, y: Float): Int {
        if (indicator == null) {
            return -1
        }
        val lengthCoordinate: Float
        val heightCoordinate: Float
        if (indicator.orientation == Orientation.HORIZONTAL) {
            lengthCoordinate = x
            heightCoordinate = y
        } else {
            lengthCoordinate = y
            heightCoordinate = x
        }
        return getFitPosition(indicator, lengthCoordinate, heightCoordinate)
    }

    private fun getFitPosition(indicator: Indicator, lengthCoordinate: Float, heightCoordinate: Float): Int {
        val count = indicator.count
        val radius = indicator.radius
        val stroke = indicator.strokeHere
        val padding = indicator.padding
        val height = if (indicator.orientation == Orientation.HORIZONTAL) indicator.height else indicator.width
        var length = 0
        for (i in 0 until count) {
            val indicatorPadding = if (i > 0) padding else padding / 2
            val startValue = length
            length += radius * 2 + stroke / 2 + indicatorPadding
            val endValue = length
            val fitLength = lengthCoordinate >= startValue && lengthCoordinate <= endValue
            val fitHeight = heightCoordinate >= 0 && heightCoordinate <= height
            if (fitLength && fitHeight) {
                return i
            }
        }
        return -1
    }

    private fun getHorizontalCoordinate(indicator: Indicator, position: Int): Int {
        val count = indicator.count
        val radius = indicator.radius
        val stroke = indicator.strokeHere
        val padding = indicator.padding
        var coordinate = 0
        for (i in 0 until count) {
            coordinate += radius + stroke / 2
            if (position == i) {
                return coordinate
            }
            coordinate += radius + padding + stroke / 2
        }
        if (indicator.animationType === IndicatorAnimationType.DROP) {
            coordinate += radius * 2
        }
        return coordinate
    }

    private fun getVerticalCoordinate(indicator: Indicator): Int {
        val radius = indicator.radius
        val coordinate: Int
        coordinate = if (indicator.animationType === IndicatorAnimationType.DROP) {
            radius * 3
        } else {
            radius
        }
        return coordinate
    }

    fun getProgress(indicator: Indicator, position: Int, positionOffset: Float, isRtl: Boolean): Pair<Int, Float> {
        var position = position
        val count = indicator.count
        var selectedPosition = indicator.selectedPosition
        if (isRtl) {
            position = count - 1 - position
        }
        if (position < 0) {
            position = 0
        } else if (position > count - 1) {
            position = count - 1
        }
        val isRightOverScrolled = position > selectedPosition
        val isLeftOverScrolled: Boolean
        isLeftOverScrolled = if (isRtl) {
            position - 1 < selectedPosition
        } else {
            position + 1 < selectedPosition
        }
        if (isRightOverScrolled || isLeftOverScrolled) {
            selectedPosition = position
            indicator.selectedPosition = selectedPosition
        }
        val slideToRightSide = selectedPosition == position && positionOffset != 0f
        val selectingPosition: Int
        var selectingProgress: Float
        if (slideToRightSide) {
            selectingPosition = if (isRtl) position - 1 else position + 1
            selectingProgress = positionOffset
        } else {
            selectingPosition = position
            selectingProgress = 1 - positionOffset
        }
        if (selectingProgress > 1) {
            selectingProgress = 1f
        } else if (selectingProgress < 0) {
            selectingProgress = 0f
        }
        return Pair(selectingPosition, selectingProgress)
    }
}