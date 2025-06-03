package com.smarteist.autoimageslider

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.smarteist.autoimageslider.IndicatorView.PageIndicatorView
import com.smarteist.autoimageslider.IndicatorView.animation.type.BaseAnimation
import com.smarteist.autoimageslider.IndicatorView.animation.type.ColorAnimation
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController
import com.smarteist.autoimageslider.IndicatorView.draw.data.Orientation
import com.smarteist.autoimageslider.IndicatorView.draw.data.RtlMode
import com.smarteist.autoimageslider.IndicatorView.utils.DensityUtils
import com.smarteist.autoimageslider.InfiniteAdapter.InfinitePagerAdapter
import com.smarteist.autoimageslider.SliderViewAdapter.DataSetListener
import com.smarteist.autoimageslider.Transformations.*

/**
 * A custom FrameLayout that wraps a SliderPager (ViewPager) and an optional PageIndicatorView.
 * Supports infinite paging (via InfinitePagerAdapter), auto-cycling, touch-pause, and customizable
 * indicator animations & placement.
 *
 * The API has been refactored so that every “getX()/setX(…)” pair is exposed as a Kotlin property
 * (`var` or `val`). Under the hood, Kotlin still generates Java‐friendly `getX()`/`setX()` bytecode.
 */
class SliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    Runnable,
    View.OnTouchListener,
    DataSetListener,
    ViewPager.OnPageChangeListener,
    SliderPager.OnPageChangeListener {

    companion object {
        const val AUTO_CYCLE_DIRECTION_RIGHT = 0
        const val AUTO_CYCLE_DIRECTION_LEFT = 1
        const val AUTO_CYCLE_DIRECTION_BACK_AND_FORTH = 2
        private const val TAG = "SliderView"
    }

    // ------------------------------------------------------------------------
    // Internal state & backing fields
    // ------------------------------------------------------------------------

    private val handler = Handler(Looper.getMainLooper())

    private var flagBackAndForth = false

    /** Backing field for autoCycleEnabled.  */
    private var _autoCycleEnabled: Boolean = false

    /** Backing field for autoCycleDirection.  */
    private var _autoCycleDirection: Int = AUTO_CYCLE_DIRECTION_RIGHT

    /** Backing field (in milliseconds) for scroll time.  */
    private var _scrollTimeInMillis: Int = 2000

    // The pager, its adapter(s), and indicator
    private var pagerIndicator: PageIndicatorView? = null
    private var pagerAdapter: SliderViewAdapter<*>? = null
    private val sliderPager: SliderPager = SliderPager(context).apply {
        overScrollMode = OVER_SCROLL_IF_CONTENT_SCROLLS
        id = ViewCompat.generateViewId()
        setOnTouchListener(this@SliderView)
        addOnPageChangeListener(this@SliderView)
    }
    private var infinitePagerAdapter: InfinitePagerAdapter? = null

    // Listener for page changes
    private var pageListener: OnSliderPageListener? = null

    // Flags & state
    private var isInfiniteAdapter = true
    private var isIndicatorEnabledInternal = true
    private var previousPosition = -1

    init {
        // 1. Add the SliderPager at index 0
        addView(
            sliderPager,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        )

        // 2. If XML attributes were provided, read them
        attrs?.let { setUpAttributes(context, it) }
    }

    // ------------------------------------------------------------------------
    // XML Attribute Parsing (unchanged except uses the new properties)
    // ------------------------------------------------------------------------

    private fun setUpAttributes(context: Context, attrs: AttributeSet) {
        val ta: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.SliderView, 0, 0)
        try {
            // 1. Indicator enabled?
            isIndicatorEnabled = ta.getBoolean(
                R.styleable.SliderView_sliderIndicatorEnabled,
                true
            )

            // 2. Slider animation duration (for internal ScrollDuration)
            val sliderAnimationDuration = ta.getInt(
                R.styleable.SliderView_sliderAnimationDuration,
                SliderPager.DEFAULT_SCROLL_DURATION
            )

            // 3. AutoCycle: scroll time in seconds
            val sliderScrollTimeInSec = ta.getInt(
                R.styleable.SliderView_sliderScrollTimeInSec,
                2
            )

            // 4. AutoCycle enabled?
            val sliderAutoCycleEnabled = ta.getBoolean(
                R.styleable.SliderView_sliderAutoCycleEnabled,
                true
            )

            // 5. Start auto cycle immediately?
            val sliderStartAutoCycle = ta.getBoolean(
                R.styleable.SliderView_sliderStartAutoCycle,
                false
            )

            // 6. AutoCycle direction
            val sliderAutoCycleDirection = ta.getInt(
                R.styleable.SliderView_sliderAutoCycleDirection,
                AUTO_CYCLE_DIRECTION_RIGHT
            )

            // Apply core settings via the new properties:
            sliderAnimationDurationMillis(sliderAnimationDuration)
            scrollTimeInSeconds = sliderScrollTimeInSec
            autoCycleEnabled = sliderAutoCycleEnabled
            autoCycleDirection = sliderAutoCycleDirection
            if (sliderStartAutoCycle) startAutoCycle()
            isIndicatorEnabled = isIndicatorEnabled

            // If indicator is enabled, read further indicator attributes:
            if (isIndicatorEnabled) {
                initIndicatorIfNeeded()

                // Orientation
                val orientationIndex = ta.getInt(
                    R.styleable.SliderView_sliderIndicatorOrientation,
                    Orientation.HORIZONTAL.ordinal
                )
                val orientation = Orientation.entries.getOrNull(orientationIndex)
                    ?: Orientation.HORIZONTAL
                setIndicatorOrientation(orientation)

                // Radius & padding (in dp-to-px)
                val defaultRadius = DensityUtils.dpToPx(2)
                val defaultPadding = DensityUtils.dpToPx(3)
                val indicatorRadius = ta.getDimensionPixelSize(
                    R.styleable.SliderView_sliderIndicatorRadius,
                    defaultRadius
                )
                val indicatorPadding = ta.getDimensionPixelSize(
                    R.styleable.SliderView_sliderIndicatorPadding,
                    defaultPadding
                )
                this.indicatorRadius = indicatorRadius
                indicatorPaddingPx = indicatorPadding

                // Margins (uniform or custom)
                val defaultMargin = DensityUtils.dpToPx(12)
                val indicatorMargin = ta.getDimensionPixelSize(
                    R.styleable.SliderView_sliderIndicatorMargin,
                    defaultMargin
                )
                indicatorMarginPx = indicatorMargin

                val marginLeft = ta.getDimensionPixelSize(
                    R.styleable.SliderView_sliderIndicatorMarginLeft,
                    defaultMargin
                )
                val marginTop = ta.getDimensionPixelSize(
                    R.styleable.SliderView_sliderIndicatorMarginTop,
                    defaultMargin
                )
                val marginRight = ta.getDimensionPixelSize(
                    R.styleable.SliderView_sliderIndicatorMarginRight,
                    defaultMargin
                )
                val marginBottom = ta.getDimensionPixelSize(
                    R.styleable.SliderView_sliderIndicatorMarginBottom,
                    defaultMargin
                )
                setIndicatorMargins(marginLeft, marginTop, marginRight, marginBottom)

                // Gravity
                val defaultGravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                val indicatorGravity = ta.getInt(
                    R.styleable.SliderView_sliderIndicatorGravity,
                    defaultGravity
                )
                this.indicatorGravity = indicatorGravity

                // Selected / Unselected colors
                val defaultUnselectedColor =
                    Color.parseColor(ColorAnimation.DEFAULT_UNSELECTED_COLOR)
                val defaultSelectedColor =
                    Color.parseColor(ColorAnimation.DEFAULT_SELECTED_COLOR)
                val unselectedColor = ta.getColor(
                    R.styleable.SliderView_sliderIndicatorUnselectedColor,
                    defaultUnselectedColor
                )
                val selectedColor = ta.getColor(
                    R.styleable.SliderView_sliderIndicatorSelectedColor,
                    defaultSelectedColor
                )
                indicatorUnselectedColor = unselectedColor
                indicatorSelectedColor = selectedColor

                // Animation duration
                val animationDuration = ta.getInt(
                    R.styleable.SliderView_sliderIndicatorAnimationDuration,
                    BaseAnimation.DEFAULT_ANIMATION_TIME
                ).toLong()
                indicatorAnimationDuration = animationDuration

                // RTL mode
                val rtlIndex = ta.getInt(
                    R.styleable.SliderView_sliderIndicatorRtlMode,
                    RtlMode.Off.ordinal
                )
                val rtlMode = RtlMode.values().getOrNull(rtlIndex) ?: RtlMode.Off
                indicatorRtlMode = rtlMode
            }
        } finally {
            ta.recycle()
        }
    }

    // ------------------------------------------------------------------------
    // Indicator Initialization & Configuration
    // ------------------------------------------------------------------------

    private fun initIndicatorIfNeeded() {
        if (pagerIndicator == null) {
            pagerIndicator = PageIndicatorView(context).also { indicator ->
                val params = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                    setMargins(20, 20, 20, 20)
                }
                addView(indicator, /* index = */ 1, params)
                indicator.setViewPager(sliderPager)
                indicator.setDynamicCount(true)
            }
        } else {
            pagerIndicator?.setViewPager(sliderPager)
        }
    }

    /**
     * Provide a custom PageIndicatorView (e.g. if user inflated from XML).
     */
    fun setPageIndicatorView(indicator: PageIndicatorView) {
        pagerIndicator = indicator
        initIndicatorIfNeeded()
    }

    // Expose the underlying PageIndicatorView (if any). Read-only.
    val pageIndicatorView: PageIndicatorView?
        get() = pagerIndicator

    /**
     * Set a click listener on the indicator dots.
     */
    fun setOnIndicatorClickListener(listener: DrawController.ClickListener) {
        pagerIndicator?.setClickListener(listener)
    }

    // When somebody writes `isIndicatorEnabled = false`, hide. When `true`, create/init.
    var isIndicatorEnabled: Boolean
        get() = isIndicatorEnabledInternal
        set(value) {
            isIndicatorEnabledInternal = value
            if (value) {
                initIndicatorIfNeeded()
                pagerIndicator?.visibility = View.VISIBLE
            } else {
                pagerIndicator?.visibility = View.GONE
            }
        }

    // Animation duration for the indicator dots (Kotlin property).
    var indicatorAnimationDuration: Long
        get() = pagerIndicator?.animationDuration ?: BaseAnimation.DEFAULT_ANIMATION_TIME.toLong()
        set(value) {
            pagerIndicator?.animationDuration = value
        }

    // Gravity of the indicator container (e.g. BOTTOM | CENTER_HORIZONTAL).
    var indicatorGravity: Int
        get() = (pagerIndicator?.layoutParams as? LayoutParams)?.gravity
            ?: (Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
        set(value) {
            pagerIndicator?.let { indicator ->
                (indicator.layoutParams as? LayoutParams)?.apply {
                    gravity = value
                    indicator.layoutParams = this
                }
            }
        }

    // Padding around each indicator dot, in pixels.
    var indicatorPaddingPx: Int
        get() = pagerIndicator?.paddingLeft ?: 0 // assuming uniform padding
        set(padding) {
            pagerIndicator?.setPadding(padding, padding, padding, padding)
        }

    // Uniform margin around the indicator container, in pixels.
    var indicatorMarginPx: Int
        get() {
            return (pagerIndicator?.layoutParams as? LayoutParams)?.let { params ->
                // assume uniform margins; return left margin as representative
                params.leftMargin
            } ?: 0
        }
        set(value) {
            pagerIndicator?.let { indicator ->
                (indicator.layoutParams as? LayoutParams)?.apply {
                    setMargins(value, value, value, value)
                    indicator.layoutParams = this
                }
            }
        }

    // Individual margins around the indicator: (left, top, right, bottom)
    fun setIndicatorMargins(left: Int, top: Int, right: Int, bottom: Int) {
        pagerIndicator?.let { indicator ->
            (indicator.layoutParams as? LayoutParams)?.apply {
                setMargins(left, top, right, bottom)
                indicator.layoutParams = this
            }
        }
    }

    // Orientation of the indicator dots (HORIZONTAL vs VERTICAL).
    fun setIndicatorOrientation(value: Orientation) {
        pagerIndicator?.setOrientation(value)
    }

    // Animation type for when the selected dot changes.
    fun setIndicatorAnimationType(indicatorAnimationType: IndicatorAnimationType) {
        pagerIndicator?.setAnimationType(indicatorAnimationType)
    }

    // Visibility (visible/gone) of the indicator container.
    var isIndicatorVisible: Boolean
        get() = pagerIndicator?.visibility == View.VISIBLE
        set(visible) {
            pagerIndicator?.visibility = if (visible) View.VISIBLE else View.GONE
        }

    // Dot radius (in pixels).
    var indicatorRadius: Int
        get() = pagerIndicator?.radius ?: 0
        set(value) {
            pagerIndicator?.radius = value
        }

    // Dot color when selected.
    var indicatorSelectedColor: Int
        get() = pagerIndicator?.selectedColor ?: Color.TRANSPARENT
        set(color) {
            pagerIndicator?.selectedColor = color
        }

    // Dot color when unselected.
    var indicatorUnselectedColor: Int
        get() = pagerIndicator?.unselectedColor ?: Color.TRANSPARENT
        set(color) {
            pagerIndicator?.unselectedColor = color
        }

    // Right‐to‐left mode for indicators (Off, On, Auto).
    var indicatorRtlMode: RtlMode
        get() = pagerIndicator?.getRtlMode() ?: RtlMode.Off
        set(mode) {
            pagerIndicator?.setRtlMode(mode)
        }

    // ------------------------------------------------------------------------
    // Adapter Setup & Infinite Mode
    // ------------------------------------------------------------------------

    /**
     * Provide a [SliderViewAdapter] and automatically wrap it in an [InfinitePagerAdapter].
     * If `infiniteAdapter = false`, we attach the adapter directly to the internal SliderPager.
     */
    fun setSliderAdapter(adapter: SliderViewAdapter<*>, infiniteAdapter: Boolean = true) {
        isInfiniteAdapter = infiniteAdapter
        pagerAdapter = adapter

        if (infiniteAdapter) {
            infinitePagerAdapter = InfinitePagerAdapter(adapter)
            sliderPager.adapter = infinitePagerAdapter
            adapter.setDataSetListener(this)
            // start at “zero” position in the infinite adapter
            sliderPager.setCurrentItem(0, false)
        } else {
            sliderPager.adapter = adapter as PagerAdapter
            adapter.setDataSetListener(this)
            sliderPager.setCurrentItem(0, false)
        }

        if (isIndicatorEnabled) {
            initIndicatorIfNeeded()
        }
    }

    /** Read-only access to the internal SliderPager. */
    val sliderPagerInstance: SliderPager
        get() = sliderPager

    /** Read-only access to the currently attached PagerAdapter (if any). */
    val sliderAdapter: PagerAdapter?
        get() = pagerAdapter as? PagerAdapter

    // ------------------------------------------------------------------------
    // Auto-Cycle Controls (as Kotlin properties)
    // ------------------------------------------------------------------------

    /** Whether auto-cycling through pages is enabled.  */
    var autoCycleEnabled: Boolean
        get() = _autoCycleEnabled
        set(value) {
            _autoCycleEnabled = value
        }

    /** Direction for auto-cycle: RIGHT, LEFT, or BACK_AND_FORTH. */
    var autoCycleDirection: Int
        get() = _autoCycleDirection
        set(value) {
            _autoCycleDirection = value
        }

    /** Scroll time, expressed in seconds. */
    var scrollTimeInSeconds: Int
        get() = _scrollTimeInMillis / 1000
        set(seconds) {
            _scrollTimeInMillis = seconds * 1000
        }

    /** Scroll time, expressed in milliseconds. */
    var scrollTimeInMillis: Int
        get() = _scrollTimeInMillis
        set(millis) {
            _scrollTimeInMillis = millis
        }

    /**
     * Starts the auto-cycle. Will launch the first run() after [scrollTimeInMillis].
     */
    fun startAutoCycle() {
        handler.removeCallbacks(this)
        handler.postDelayed(this, _scrollTimeInMillis.toLong())
    }

    /**
     * Stops any pending auto-cycle callbacks immediately.
     */
    fun stopAutoCycle() {
        handler.removeCallbacks(this)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (_autoCycleEnabled) {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> stopAutoCycle()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Resume after 2 seconds
                    handler.postDelayed({ startAutoCycle() }, 2000)
                }
            }
        }
        // Return false so that the pager still responds to touch events
        return false
    }

    /**
     * Advances to the next position based on [autoCycleDirection].
     * Called by `run()`.
     */
    private fun slideToNextPosition() {
        val currentPosition = sliderPager.currentItem
        val adapterCount = adapterItemCount
        if (adapterCount > 1) {
            when (_autoCycleDirection) {
                AUTO_CYCLE_DIRECTION_BACK_AND_FORTH -> {
                    // If at an endpoint, flip direction
                    val atFirst = (currentPosition % (adapterCount - 1) == 0) &&
                            previousPosition !in listOf(adapterCount - 1, 0)
                    if (atFirst) flagBackAndForth = !flagBackAndForth

                    if (flagBackAndForth) {
                        sliderPager.setCurrentItem(currentPosition + 1, true)
                    } else {
                        sliderPager.setCurrentItem(currentPosition - 1, true)
                    }
                }

                AUTO_CYCLE_DIRECTION_LEFT -> {
                    sliderPager.setCurrentItem(currentPosition - 1, true)
                }

                AUTO_CYCLE_DIRECTION_RIGHT -> {
                    sliderPager.setCurrentItem(currentPosition + 1, true)
                }
            }
        }
        previousPosition = currentPosition
    }

    /**
     * Moves one page backward (used if user explicitly wants “previous”).
     */
    fun slideToPreviousPosition() {
        val currentPosition = sliderPager.currentItem
        val adapterCount = adapterItemCount
        if (adapterCount > 1) {
            when (_autoCycleDirection) {
                AUTO_CYCLE_DIRECTION_BACK_AND_FORTH -> {
                    val atFirst = (currentPosition % (adapterCount - 1) == 0) &&
                            previousPosition !in listOf(adapterCount - 1, 0)
                    if (atFirst) flagBackAndForth = !flagBackAndForth

                    if (flagBackAndForth && currentPosition < previousPosition) {
                        sliderPager.setCurrentItem(currentPosition - 1, true)
                    } else {
                        sliderPager.setCurrentItem(currentPosition + 1, true)
                    }
                }

                AUTO_CYCLE_DIRECTION_LEFT -> {
                    sliderPager.setCurrentItem(currentPosition + 1, true)
                }

                AUTO_CYCLE_DIRECTION_RIGHT -> {
                    sliderPager.setCurrentItem(currentPosition - 1, true)
                }
            }
        }
        previousPosition = currentPosition
    }

    /** Internal helper to retrieve the “count” of the currently attached adapter. */
    private val adapterItemCount: Int
        get() = try {
            sliderAdapter?.count ?: 0
        } catch (e: Exception) {
            Log.e(TAG, "adapterItemCount: Adapter is null; returning 0")
            0
        }

    // ------------------------------------------------------------------------
    // Pager callbacks
    // ------------------------------------------------------------------------

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        // no-op
    }

    override fun onPageSelected(position: Int) {
        pageListener?.onSliderPageChanged(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
        // no-op
    }

    /**
     * Exposes a way to set a page-change listener. Whenever a new page becomes selected,
     * onSliderPageChanged(position) is called.
     */
    fun setCurrentPageListener(listener: OnSliderPageListener) {
        pageListener = listener
    }

    // ------------------------------------------------------------------------
    // Runnable (auto-cycle)
    // ------------------------------------------------------------------------

    override fun run() {
        try {
            slideToNextPosition()
        } finally {
            if (_autoCycleEnabled) {
                handler.postDelayed(this, _scrollTimeInMillis.toLong())
            }
        }
    }

    // ------------------------------------------------------------------------
    // DataSetListener (for notifying infinite adapter)
    // ------------------------------------------------------------------------

    override fun dataSetChanged() {
        if (isInfiniteAdapter) {
            infinitePagerAdapter?.notifyDataSetChanged()
            sliderPager.setCurrentItem(0, false)
        }
    }

    // ------------------------------------------------------------------------
    // Transformations
    // ------------------------------------------------------------------------

    /**
     * Convenience: set one of the predefined transformations via enum.
     */
    fun setSliderTransformAnimation(animation: SliderAnimations) {
        val transformer = when (animation) {
            SliderAnimations.ANTICLOCKSPINTRANSFORMATION -> AntiClockSpinTransformation()
            SliderAnimations.CLOCK_SPINTRANSFORMATION -> Clock_SpinTransformation()
            SliderAnimations.CUBEINDEPTHTRANSFORMATION -> CubeInDepthTransformation()
            SliderAnimations.CUBEINROTATIONTRANSFORMATION -> CubeInRotationTransformation()
            SliderAnimations.CUBEINSCALINGTRANSFORMATION -> CubeInScalingTransformation()
            SliderAnimations.CUBEOUTDEPTHTRANSFORMATION -> CubeOutDepthTransformation()
            SliderAnimations.CUBEOUTROTATIONTRANSFORMATION -> CubeOutRotationTransformation()
            SliderAnimations.CUBEOUTSCALINGTRANSFORMATION -> CubeOutScalingTransformation()
            SliderAnimations.DEPTHTRANSFORMATION -> DepthTransformation()
            SliderAnimations.FADETRANSFORMATION -> FadeTransformation()
            SliderAnimations.FANTRANSFORMATION -> FanTransformation()
            SliderAnimations.FIDGETSPINTRANSFORMATION -> FidgetSpinTransformation()
            SliderAnimations.GATETRANSFORMATION -> GateTransformation()
            SliderAnimations.HINGETRANSFORMATION -> HingeTransformation()
            SliderAnimations.HORIZONTALFLIPTRANSFORMATION -> HorizontalFlipTransformation()
            SliderAnimations.POPTRANSFORMATION -> PopTransformation()
            SliderAnimations.SPINNERTRANSFORMATION -> SpinnerTransformation()
            SliderAnimations.TOSSTRANSFORMATION -> TossTransformation()
            SliderAnimations.VERTICALFLIPTRANSFORMATION -> VerticalFlipTransformation()
            SliderAnimations.VERTICALSHUTTRANSFORMATION -> VerticalShutTransformation()
            SliderAnimations.ZOOMOUTTRANSFORMATION -> ZoomOutTransformation()
            SliderAnimations.SIMPLETRANSFORMATION, null -> SimpleTransformation()
        }
        sliderPager.setPageTransformer(false, transformer)
    }

    /**
     * Allow users to set any custom PageTransformer.
     */
    fun setCustomSliderTransformAnimation(transformer: SliderPager.PageTransformer) {
        sliderPager.setPageTransformer(false, transformer)
    }

    // ------------------------------------------------------------------------
    // Scroll Duration (overloads unchanged)
    // ------------------------------------------------------------------------

    /**
     * Change the internal ViewPager’s scroll duration (in milliseconds).
     */
    fun sliderAnimationDurationMillis(value: Int) {
        sliderPager.setScrollDuration(value)
    }

    /**
     * Overload: Change internal scroll duration AND interpolator.
     */
    fun setSliderAnimationDuration(millis: Int, interpolator: Interpolator) {
        sliderPager.setScrollDuration(millis, interpolator)
    }

    // ------------------------------------------------------------------------
    // Page Navigation
    // ------------------------------------------------------------------------

    /**
     * Set the current page index (animated).
     */
    var currentPagePosition: Int
        get() = pagerAdapter?.let { sliderPager.currentItem }
            ?: throw IllegalStateException("Adapter not set")
        set(position) {
            sliderPager.setCurrentItem(position, true)
        }

    // ------------------------------------------------------------------------
    // Listener interface
    // ------------------------------------------------------------------------

    /**
     * Callback interface for page-change events.
     */
    interface OnSliderPageListener {
        /**
         * Called whenever a new page becomes selected. The animation may not be fully complete.
         * @param position the new selected page index
         */
        fun onSliderPageChanged(position: Int)
    }
}
