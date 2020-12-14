package com.smarteist.autoimageslider.IndicatorView

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.database.DataSetObserver
import android.graphics.Canvas
import android.os.Build
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.IndicatorView.animation.type.ScaleAnimation
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController.ClickListener
import com.smarteist.autoimageslider.IndicatorView.draw.data.Indicator
import com.smarteist.autoimageslider.IndicatorView.draw.data.Orientation
import com.smarteist.autoimageslider.IndicatorView.draw.data.PositionSavedState
import com.smarteist.autoimageslider.IndicatorView.draw.data.RtlMode
import com.smarteist.autoimageslider.IndicatorView.utils.CoordinatesUtils.getProgress
import com.smarteist.autoimageslider.IndicatorView.utils.DensityUtils.dpToPx
import com.smarteist.autoimageslider.IndicatorView.utils.IdUtils
import com.smarteist.autoimageslider.InfiniteAdapter.InfinitePagerAdapter
import com.smarteist.autoimageslider.SliderPager

class PageIndicatorView : View, SliderPager.OnPageChangeListener, IndicatorManager.Listener, SliderPager.OnAdapterChangeListener {
    private var manager: IndicatorManager? = null
    private var setObserver: DataSetObserver? = null
    private var viewPager: SliderPager? = null
    private var isInteractionEnabled = false

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findViewPager(parent)
    }

    override fun onDetachedFromWindow() {
        unRegisterSetObserver()
        super.onDetachedFromWindow()
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val indicator = manager!!.indicator()
        val positionSavedState = PositionSavedState(super.onSaveInstanceState())
        positionSavedState.selectedPosition = indicator.selectedPosition
        positionSavedState.selectingPosition = indicator.selectingPosition
        positionSavedState.lastSelectedPosition = indicator.lastSelectedPosition
        return positionSavedState
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state is PositionSavedState) {
            val indicator = manager!!.indicator()
            val positionSavedState = state
            indicator.selectedPosition = positionSavedState.selectedPosition
            indicator.selectingPosition = positionSavedState.selectingPosition
            indicator.lastSelectedPosition = positionSavedState.lastSelectedPosition
            super.onRestoreInstanceState(positionSavedState.superState)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val pair = manager!!.drawer().measureViewSize(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(pair.first, pair.second)
    }

    override fun onDraw(canvas: Canvas) {
        manager!!.drawer().draw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        manager!!.drawer().touch(event)
        return true
    }

    override fun onIndicatorUpdated() {
        invalidate()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        onPageScroll(position, positionOffset)
    }

    override fun onPageSelected(position: Int) {
        onPageSelect(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            manager!!.indicator().isInteractiveAnimation = isInteractionEnabled
        }
    }

    override fun onAdapterChanged(viewPager: SliderPager, oldAdapter: PagerAdapter?, newAdapter: PagerAdapter?) {
        updateState()
    }

    var count: Int
        get() = manager!!.indicator().count
        set(count) {
            if (count >= 0 && manager!!.indicator().count != count) {
                manager!!.indicator().count = count
                updateVisibility()
                requestLayout()
            }
        }

    fun setDynamicCount(dynamicCount: Boolean) {
        manager!!.indicator().isDynamicCount = dynamicCount
        if (dynamicCount) {
            registerSetObserver()
        } else {
            unRegisterSetObserver()
        }
    }

    fun setRadius(radiusPx: Float) {
        var radiusPx = radiusPx
        if (radiusPx < 0) {
            radiusPx = 0f
        }
        manager!!.indicator().radius = radiusPx.toInt()
        invalidate()
    }

    var radius: Int
        get() = manager!!.indicator().radius
        set(radiusDp) {
            var radiusDp = radiusDp
            if (radiusDp < 0) {
                radiusDp = 0
            }
            val radiusPx = dpToPx(radiusDp)
            manager!!.indicator().radius = radiusPx
            invalidate()
        }

    fun setPadding(paddingPx: Float) {
        var paddingPx = paddingPx
        if (paddingPx < 0) {
            paddingPx = 0f
        }
        manager!!.indicator().padding = paddingPx.toInt()
        invalidate()
    }

    var padding: Int
        get() = manager!!.indicator().padding
        set(paddingDp) {
            var paddingDp = paddingDp
            if (paddingDp < 0) {
                paddingDp = 0
            }
            val paddingPx = dpToPx(paddingDp)
            manager!!.indicator().padding = paddingPx
            invalidate()
        }
    var scaleFactor: Float
        get() = manager!!.indicator().scaleFactor
        set(factor) {
            var factor = factor
            if (factor > ScaleAnimation.MAX_SCALE_FACTOR) {
                factor = ScaleAnimation.MAX_SCALE_FACTOR
            } else if (factor < ScaleAnimation.MIN_SCALE_FACTOR) {
                factor = ScaleAnimation.MIN_SCALE_FACTOR
            }
            manager!!.indicator().scaleFactor = factor
        }

    fun setStrokeWidth(strokePx: Float) {
        var strokePx = strokePx
        val radiusPx = manager!!.indicator().radius
        if (strokePx < 0) {
            strokePx = 0f
        } else if (strokePx > radiusPx) {
            strokePx = radiusPx.toFloat()
        }
        manager!!.indicator().strokeHere = strokePx.toInt()
        invalidate()
    }

    fun setStrokeWidth(strokeDp: Int) {
        var strokePx = dpToPx(strokeDp)
        val radiusPx = manager!!.indicator().radius
        if (strokePx < 0) {
            strokePx = 0
        } else if (strokePx > radiusPx) {
            strokePx = radiusPx
        }
        manager!!.indicator().strokeHere = (strokePx)
        invalidate()
    }

    val strokeWidth: Int get() = manager!!.indicator().strokeHere
    var selectedColor: Int
        get() = manager!!.indicator().selectedColor
        set(color) {
            manager!!.indicator().selectedColor = color
            invalidate()
        }
    var unselectedColor: Int
        get() = manager!!.indicator().unselectedColor
        set(color) {
            manager!!.indicator().unselectedColor = color
            invalidate()
        }

    fun setAutoVisibility(autoVisibility: Boolean) {
        if (!autoVisibility) {
            visibility = VISIBLE
        }
        manager!!.indicator().isAutoVisibility = autoVisibility
        updateVisibility()
    }

    fun setOrientation(orientation: Orientation?) {
        if (orientation != null) {
            manager!!.indicator().orientation = orientation
            requestLayout()
        }
    }

    var animationDuration: Long
        get() = manager!!.indicator().animationDuration
        set(duration) {
            manager!!.indicator().animationDuration = duration
        }

    fun setAnimationType(type: IndicatorAnimationType?) {
        manager!!.onValueUpdated(null)
        if (type != null) {
            manager!!.indicator().animationType = type
        } else {
            manager!!.indicator().animationType = IndicatorAnimationType.NONE
        }
        invalidate()
    }

    fun setInteractiveAnimation(isInteractive: Boolean) {
        manager!!.indicator().isInteractiveAnimation = isInteractive
        isInteractionEnabled = isInteractive
    }

    fun setViewPager(pager: SliderPager?) {
        releaseViewPager()
        if (pager == null) {
            return
        }
        viewPager = pager
        viewPager!!.addOnPageChangeListener(this)
        viewPager!!.addOnAdapterChangeListener(this)
        manager!!.indicator().viewPagerId = viewPager!!.id
        setDynamicCount(manager!!.indicator().isDynamicCount)
        updateState()
    }

    fun releaseViewPager() {
        if (viewPager != null) {
            viewPager!!.removeOnPageChangeListener(this)
            viewPager = null
        }
    }

    fun setRtlMode(mode: RtlMode?) {
        val indicator = manager!!.indicator()
        if (mode == null) {
            indicator.rtlMode = RtlMode.Off
        } else {
            indicator.rtlMode = mode
        }
        if (viewPager == null) {
            return
        }
        val selectedPosition = indicator.selectedPosition
        var position = selectedPosition
        if (isRtl) {
            position = indicator.count - 1 - selectedPosition
        } else if (viewPager != null) {
            position = viewPager!!.currentItem
        }
        indicator.lastSelectedPosition = position
        indicator.selectingPosition = position
        indicator.selectedPosition = position
        invalidate()
    }

    var selection: Int
        get() = manager!!.indicator().selectedPosition
        set(position) {
            var position = position
            val indicator = manager!!.indicator()
            position = adjustPosition(position)
            if (position == indicator.selectedPosition || position == indicator.selectingPosition) {
                return
            }
            indicator.isInteractiveAnimation = false
            indicator.lastSelectedPosition = indicator.selectedPosition
            indicator.selectingPosition = position
            indicator.selectedPosition = position
            manager!!.animate().basic()
        }

    fun setSelected(position: Int) {
        val indicator = manager!!.indicator()
        val animationType = indicator.animationType
        indicator.animationType = IndicatorAnimationType.NONE
        selection = position
        indicator.animationType = animationType
    }

    fun clearSelection() {
        val indicator = manager!!.indicator()
        indicator.isInteractiveAnimation = false
        indicator.lastSelectedPosition = Indicator.COUNT_NONE
        indicator.selectingPosition = Indicator.COUNT_NONE
        indicator.selectedPosition = Indicator.COUNT_NONE
        manager!!.animate().basic()
    }

    fun setProgress(selectingPosition: Int, progress: Float) {
        var selectingPosition = selectingPosition
        var progress = progress
        val indicator = manager!!.indicator()
        if (!indicator.isInteractiveAnimation) {
            return
        }
        val count = indicator.count
        if (count <= 0 || selectingPosition < 0) {
            selectingPosition = 0
        } else if (selectingPosition > count - 1) {
            selectingPosition = count - 1
        }
        if (progress < 0) {
            progress = 0f
        } else if (progress > 1) {
            progress = 1f
        }
        if (progress == 1f) {
            indicator.lastSelectedPosition = indicator.selectedPosition
            indicator.selectedPosition = selectingPosition
        }
        indicator.selectingPosition = selectingPosition
        manager!!.animate().interactive(progress)
    }

    fun setClickListener(listener: ClickListener?) {
        manager!!.drawer().setClickListener(listener)
    }

    private fun init(attrs: AttributeSet?) {
        setupId()
        initIndicatorManager(attrs)
    }

    private fun setupId() {
        if (id == NO_ID) {
            id = IdUtils.generateViewId()
        }
    }

    private fun initIndicatorManager(attrs: AttributeSet?) {
        manager = IndicatorManager(this)
        manager!!.drawer().initAttributes(context, attrs)
        val indicator = manager!!.indicator()
        indicator.paddingLeft = paddingLeft
        indicator.paddingTop = paddingTop
        indicator.paddingRight = paddingRight
        indicator.paddingBottom = paddingBottom
        isInteractionEnabled = indicator.isInteractiveAnimation
    }

    private fun registerSetObserver() {
        if (setObserver != null || viewPager == null || viewPager!!.adapter == null) {
            return
        }
        setObserver = object : DataSetObserver() {
            override fun onChanged() {
                updateState()
            }
        }
        try {
            viewPager!!.adapter!!.registerDataSetObserver(setObserver!!)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun unRegisterSetObserver() {
        if (setObserver == null || viewPager == null || viewPager!!.adapter == null) {
            return
        }
        try {
            viewPager!!.adapter!!.unregisterDataSetObserver(setObserver!!)
            setObserver = null
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun updateState() {
        if (viewPager == null || viewPager!!.adapter == null) {
            return
        }
        val count: Int
        val position: Int
        if (viewPager!!.adapter is InfinitePagerAdapter) {
            count = (viewPager!!.adapter as InfinitePagerAdapter?)!!.realCount
            position = if (count > 0) {
                viewPager!!.currentItem % count
            } else {
                0
            }
        } else {
            count = viewPager!!.adapter!!.count
            position = viewPager!!.currentItem
        }
        val selectedPos = if (isRtl) count - 1 - position else position
        manager!!.indicator().selectedPosition = selectedPos
        manager!!.indicator().selectingPosition = selectedPos
        manager!!.indicator().lastSelectedPosition = selectedPos
        manager!!.indicator().count = count
        manager!!.animate().end()
        updateVisibility()
        requestLayout()
    }

    private fun updateVisibility() {
        if (!manager!!.indicator().isAutoVisibility) {
            return
        }
        val count = manager!!.indicator().count
        val visibility = visibility
        if (visibility != VISIBLE && count > Indicator.MIN_COUNT) {
            setVisibility(VISIBLE)
        } else if (visibility != INVISIBLE && count <= Indicator.MIN_COUNT) {
            setVisibility(INVISIBLE)
        }
    }

    private fun onPageSelect(position: Int) {
        var position = position
        val indicator = manager!!.indicator()
        val canSelectIndicator = isViewMeasured
        val count = indicator.count
        if (canSelectIndicator) {
            if (isRtl) {
                position = count - 1 - position
            }
            selection = position
        }
    }

    private fun onPageScroll(position: Int, positionOffset: Float) {
        val indicator = manager!!.indicator()
        val animationType = indicator.animationType
        val interactiveAnimation = indicator.isInteractiveAnimation
        val canSelectIndicator = isViewMeasured && interactiveAnimation && animationType !== IndicatorAnimationType.NONE
        if (!canSelectIndicator) {
            return
        }
        val progressPair = getProgress(indicator, position, positionOffset, isRtl)
        val selectingPosition = progressPair.first
        val selectingProgress = progressPair.second
        setProgress(selectingPosition, selectingProgress)
    }

    private val isRtl: Boolean
        private get() {
            when (manager!!.indicator().rtlMode) {
                RtlMode.On -> return true
                RtlMode.Off -> return false
                RtlMode.Auto -> return TextUtilsCompat.getLayoutDirectionFromLocale(context.resources.configuration.locale) == ViewCompat.LAYOUT_DIRECTION_RTL
            }
            return false
        }
    private val isViewMeasured: Boolean
        private get() = measuredHeight != 0 || measuredWidth != 0

    private fun findViewPager(viewParent: ViewParent?) {
        val isValidParent = viewParent != null &&
                viewParent is ViewGroup && viewParent.childCount > 0
        if (!isValidParent) {
            return
        }
        val viewPagerId = manager!!.indicator().viewPagerId
        val viewPager = findViewPager((viewParent as ViewGroup?)!!, viewPagerId)
        if (viewPager != null) {
            setViewPager(viewPager)
        } else {
            findViewPager(viewParent!!.parent)
        }
    }

    private fun findViewPager(viewGroup: ViewGroup, id: Int): SliderPager? {
        if (viewGroup.childCount <= 0) {
            return null
        }
        val view = viewGroup.findViewById<View>(id)
        return if (view != null && view is SliderPager) {
            view
        } else {
            null
        }
    }

    private fun adjustPosition(position: Int): Int {
        var position = position
        val indicator = manager!!.indicator()
        val count = indicator.count
        val lastPosition = count - 1
        if (position <= 0) {
            position = 0
        } else if (position > lastPosition) {
            position = lastPosition
        }
        return position
    }
}