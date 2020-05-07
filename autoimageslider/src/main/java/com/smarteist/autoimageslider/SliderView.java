package com.smarteist.autoimageslider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.smarteist.autoimageslider.IndicatorView.PageIndicatorView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.AnimationType;
import com.smarteist.autoimageslider.IndicatorView.animation.type.BaseAnimation;
import com.smarteist.autoimageslider.IndicatorView.animation.type.ColorAnimation;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.IndicatorView.draw.data.Orientation;
import com.smarteist.autoimageslider.IndicatorView.draw.data.RtlMode;
import com.smarteist.autoimageslider.IndicatorView.utils.DensityUtils;
import com.smarteist.autoimageslider.InfiniteAdapter.InfinitePagerAdapter;
import com.smarteist.autoimageslider.Transformations.AntiClockSpinTransformation;
import com.smarteist.autoimageslider.Transformations.Clock_SpinTransformation;
import com.smarteist.autoimageslider.Transformations.CubeInDepthTransformation;
import com.smarteist.autoimageslider.Transformations.CubeInRotationTransformation;
import com.smarteist.autoimageslider.Transformations.CubeInScalingTransformation;
import com.smarteist.autoimageslider.Transformations.CubeOutDepthTransformation;
import com.smarteist.autoimageslider.Transformations.CubeOutRotationTransformation;
import com.smarteist.autoimageslider.Transformations.CubeOutScalingTransformation;
import com.smarteist.autoimageslider.Transformations.DepthTransformation;
import com.smarteist.autoimageslider.Transformations.FadeTransformation;
import com.smarteist.autoimageslider.Transformations.FanTransformation;
import com.smarteist.autoimageslider.Transformations.FidgetSpinTransformation;
import com.smarteist.autoimageslider.Transformations.GateTransformation;
import com.smarteist.autoimageslider.Transformations.HingeTransformation;
import com.smarteist.autoimageslider.Transformations.HorizontalFlipTransformation;
import com.smarteist.autoimageslider.Transformations.PopTransformation;
import com.smarteist.autoimageslider.Transformations.SimpleTransformation;
import com.smarteist.autoimageslider.Transformations.SpinnerTransformation;
import com.smarteist.autoimageslider.Transformations.TossTransformation;
import com.smarteist.autoimageslider.Transformations.VerticalFlipTransformation;
import com.smarteist.autoimageslider.Transformations.VerticalShutTransformation;
import com.smarteist.autoimageslider.Transformations.ZoomOutTransformation;

import static com.smarteist.autoimageslider.IndicatorView.draw.controller.AttributeController.getRtlMode;

public class SliderView extends FrameLayout
        implements Runnable, View.OnTouchListener,
        SliderViewAdapter.DataSetListener, SliderPager.OnPageChangeListener {

    public static final int AUTO_CYCLE_DIRECTION_RIGHT = 0;
    public static final int AUTO_CYCLE_DIRECTION_LEFT = 1;
    public static final int AUTO_CYCLE_DIRECTION_BACK_AND_FORTH = 2;
    public static final String TAG = "Slider View : ";

    private final Handler mHandler = new Handler();
    private boolean mFlagBackAndForth;
    private boolean mIsAutoCycle;
    private int mAutoCycleDirection;
    private int mScrollTimeInMillis;
    private PageIndicatorView mPagerIndicator;
    private SliderViewAdapter mPagerAdapter;
    private SliderPager mSliderPager;
    private InfinitePagerAdapter mInfinitePagerAdapter;
    private boolean mPausedSliding = false;
    private OnSliderPageListener mPageListener;
    private boolean mIsInfiniteAdapter = true;

    /*Constructor*/
    public SliderView(Context context) {
        super(context);
        setupSlideView(context);
    }

    public SliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupSlideView(context);
        setUpAttributes(context, attrs);
    }

    public SliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupSlideView(context);
        setUpAttributes(context, attrs);
    }
    /*Constructor*/

    /**
     * This class syncs all attributes from xml tag for this slider.
     *
     * @param context its android main context which is needed.
     * @param attrs   attributes from xml slider tags.
     */
    private void setUpAttributes(@NonNull Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SliderView, 0, 0);

        int indicatorOrientation = typedArray.getInt(R.styleable.SliderView_sliderIndicatorOrientation, Orientation.HORIZONTAL.ordinal());
        Orientation orientation;
        if (indicatorOrientation == 0) {
            orientation = Orientation.HORIZONTAL;
        } else {
            orientation = Orientation.VERTICAL;
        }
        int indicatorRadius = (int) typedArray.getDimension(R.styleable.SliderView_sliderIndicatorRadius, DensityUtils.dpToPx(2));
        int indicatorPadding = (int) typedArray.getDimension(R.styleable.SliderView_sliderIndicatorPadding, DensityUtils.dpToPx(3));
        int indicatorMargin = (int) typedArray.getDimension(R.styleable.SliderView_sliderIndicatorMargin, DensityUtils.dpToPx(12));
        int indicatorMarginLeft = (int) typedArray.getDimension(R.styleable.SliderView_sliderIndicatorMarginLeft, DensityUtils.dpToPx(12));
        int indicatorMarginTop = (int) typedArray.getDimension(R.styleable.SliderView_sliderIndicatorMarginTop, DensityUtils.dpToPx(12));
        int indicatorMarginRight = (int) typedArray.getDimension(R.styleable.SliderView_sliderIndicatorMarginRight, DensityUtils.dpToPx(12));
        int indicatorMarginBottom = (int) typedArray.getDimension(R.styleable.SliderView_sliderIndicatorMarginBottom, DensityUtils.dpToPx(12));
        int indicatorGravity = typedArray.getInt(R.styleable.SliderView_sliderIndicatorGravity, Gravity.CENTER | Gravity.BOTTOM);
        int indicatorUnselectedColor = typedArray.getColor(R.styleable.SliderView_sliderIndicatorUnselectedColor, Color.parseColor(ColorAnimation.DEFAULT_UNSELECTED_COLOR));
        int indicatorSelectedColor = typedArray.getColor(R.styleable.SliderView_sliderIndicatorSelectedColor, Color.parseColor(ColorAnimation.DEFAULT_SELECTED_COLOR));
        int indicatorAnimationDuration = typedArray.getInt(R.styleable.SliderView_sliderIndicatorAnimationDuration, BaseAnimation.DEFAULT_ANIMATION_TIME);
        int indicatorRtlMode = typedArray.getInt(R.styleable.SliderView_sliderIndicatorRtlMode, RtlMode.Off.ordinal());
        RtlMode rtlMode = getRtlMode(indicatorRtlMode);
        int sliderAnimationDuration = typedArray.getInt(R.styleable.SliderView_sliderAnimationDuration, SliderPager.DEFAULT_SCROLL_DURATION);
        int sliderScrollTimeInSec = typedArray.getInt(R.styleable.SliderView_sliderScrollTimeInSec, 2);
        boolean sliderAutoCycleEnabled = typedArray.getBoolean(R.styleable.SliderView_sliderAutoCycleEnabled, true);
        boolean sliderStartAutoCycle = typedArray.getBoolean(R.styleable.SliderView_sliderStartAutoCycle, false);
        int sliderAutoCycleDirection = typedArray.getInt(R.styleable.SliderView_sliderAutoCycleDirection, AUTO_CYCLE_DIRECTION_RIGHT);

        setIndicatorOrientation(orientation);
        setIndicatorRadius(indicatorRadius);
        setIndicatorPadding(indicatorPadding);
        setIndicatorMargin(indicatorMargin);
        setIndicatorGravity(indicatorGravity);
        setIndicatorMargins(indicatorMarginLeft, indicatorMarginTop, indicatorMarginRight, indicatorMarginBottom);
        setIndicatorUnselectedColor(indicatorUnselectedColor);
        setIndicatorSelectedColor(indicatorSelectedColor);
        setIndicatorAnimationDuration(indicatorAnimationDuration);
        setIndicatorRtlMode(rtlMode);
        setSliderAnimationDuration(sliderAnimationDuration);
        setScrollTimeInSec(sliderScrollTimeInSec);
        setAutoCycle(sliderAutoCycleEnabled);
        setAutoCycleDirection(sliderAutoCycleDirection);
        setAutoCycle(sliderStartAutoCycle);

        typedArray.recycle();
    }

    /**
     * This method fires initialization jobs for
     * slider view.
     *
     * @param context its android main context which is needed.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setupSlideView(Context context) {

        View wrapperView = LayoutInflater
                .from(context)
                .inflate(R.layout.slider_view, this, true);

        mSliderPager = wrapperView.findViewById(R.id.vp_slider_layout);
        mSliderPager.setOnTouchListener(this);
        mSliderPager.addOnPageChangeListener(this);

        mPagerIndicator = wrapperView.findViewById(R.id.pager_indicator);
        mPagerIndicator.setViewPager(mSliderPager);


    }

    /**
     * @param listener for indicator dots clicked.
     */
    public void setOnIndicatorClickListener(DrawController.ClickListener listener) {
        mPagerIndicator.setClickListener(listener);
    }

    /**
     * @param listener is a callback of current item in sliderView.
     */
    public void setCurrentPageListener(OnSliderPageListener listener) {
        this.mPageListener = listener;
    }

    /**
     * @param pagerAdapter Set a SliderAdapter that will supply views
     *                     for this slider as needed.
     */
    public void setSliderAdapter(@NonNull SliderViewAdapter pagerAdapter) {
        mPagerAdapter = pagerAdapter;
        //set slider adapter
        mInfinitePagerAdapter = new InfinitePagerAdapter(pagerAdapter);
        //registerAdapterDataObserver();
        mSliderPager.setAdapter(mInfinitePagerAdapter);
        mPagerAdapter.dataSetChangedListener(this);
        //setup with indicator
        mPagerIndicator.setCount(getAdapterItemsCount());
        mPagerIndicator.setDynamicCount(true);
        setCurrentPagePosition(0);
    }

    /**
     * @param pagerAdapter Set a SliderAdapter that will supply views
     *                     for this slider as needed.
     */
    public void setSliderAdapter(@NonNull SliderViewAdapter pagerAdapter, boolean infiniteAdapter) {
        this.mIsInfiniteAdapter = infiniteAdapter;
        if (!infiniteAdapter) {
            this.mPagerAdapter = pagerAdapter;
            mSliderPager.setAdapter(pagerAdapter);
            mPagerIndicator.setCount(getAdapterItemsCount());
            mPagerIndicator.setDynamicCount(true);
        } else {
            setSliderAdapter(pagerAdapter);
        }
    }

    /**
     * @return Sliders Pager
     */
    public SliderPager getSliderPager() {
        return mSliderPager;
    }

    /**
     * @return adapter of current slider.
     */
    public PagerAdapter getSliderAdapter() {
        return mPagerAdapter;
    }

    /**
     * @return if is slider auto cycling or not?
     */
    public boolean isAutoCycle() {
        return mIsAutoCycle;
    }

    public void setAutoCycle(boolean autoCycle) {
        this.mIsAutoCycle = autoCycle;
    }

    /**
     * @param limit How many pages will be kept offscreen in an idle state.
     *              <p>You should keep this limit low, especially if your pages have complex layouts.
     *              * This setting defaults to 1.</p>
     */
    public void setOffscreenPageLimit(int limit) {
        mSliderPager.setOffscreenPageLimit(limit);
    }

    /**
     * @return sliding delay in seconds.
     */
    public int getScrollTimeInSec() {
        return mScrollTimeInMillis / 1000;
    }

    /**
     * @param time of sliding delay in seconds.
     */
    public void setScrollTimeInSec(int time) {
        mScrollTimeInMillis = time * 1000;
    }

    public int getScrollTimeInMillis() {
        return mScrollTimeInMillis;
    }

    public void setScrollTimeInMillis(int millis) {
        this.mScrollTimeInMillis = millis;
    }

    /**
     * @param animation changes pre defined animations for slider.
     */
    public void setSliderTransformAnimation(SliderAnimations animation) {

        switch (animation) {
            case ANTICLOCKSPINTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new AntiClockSpinTransformation());
                break;
            case CLOCK_SPINTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new Clock_SpinTransformation());
                break;
            case CUBEINDEPTHTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new CubeInDepthTransformation());
                break;
            case CUBEINROTATIONTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new CubeInRotationTransformation());
                break;
            case CUBEINSCALINGTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new CubeInScalingTransformation());
                break;
            case CUBEOUTDEPTHTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new CubeOutDepthTransformation());
                break;
            case CUBEOUTROTATIONTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new CubeOutRotationTransformation());
                break;
            case CUBEOUTSCALINGTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new CubeOutScalingTransformation());
                break;
            case DEPTHTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new DepthTransformation());
                break;
            case FADETRANSFORMATION:
                mSliderPager.setPageTransformer(false, new FadeTransformation());
                break;
            case FANTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new FanTransformation());
                break;
            case FIDGETSPINTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new FidgetSpinTransformation());
                break;
            case GATETRANSFORMATION:
                mSliderPager.setPageTransformer(false, new GateTransformation());
                break;
            case HINGETRANSFORMATION:
                mSliderPager.setPageTransformer(false, new HingeTransformation());
                break;
            case HORIZONTALFLIPTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new HorizontalFlipTransformation());
                break;
            case POPTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new PopTransformation());
                break;
            case SPINNERTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new SpinnerTransformation());
                break;
            case TOSSTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new TossTransformation());
                break;
            case VERTICALFLIPTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new VerticalFlipTransformation());
                break;
            case VERTICALSHUTTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new VerticalShutTransformation());
                break;
            case ZOOMOUTTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new ZoomOutTransformation());
                break;
            default:
                mSliderPager.setPageTransformer(false, new SimpleTransformation());

        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mPausedSliding = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mPausedSliding = false;
        }
        return false;
    }

    /**
     * @param animation set slider animation manually .
     *                  it accepts {@link ##PageTransformer} animation classes.
     */
    public void setCustomSliderTransformAnimation(SliderPager.PageTransformer animation) {
        mSliderPager.setPageTransformer(false, animation);
    }

    /**
     * @param duration changes slider animation duration.
     */
    public void setSliderAnimationDuration(int duration) {
        mSliderPager.setScrollDuration(duration);
    }

    /**
     * @param duration     changes slider animation duration.
     * @param interpolator its animation duration accelerator
     *                     An interpolator defines the rate of change of an animation
     */
    public void setSliderAnimationDuration(int duration, Interpolator interpolator) {
        mSliderPager.setScrollDuration(duration, interpolator);
    }

    /**
     * @param position changes position of slider
     *                 items manually.
     */
    public void setCurrentPagePosition(int position) {

        if (getSliderAdapter() != null) {
            int midpoint = (getAdapterItemsCount() - 1) * (InfinitePagerAdapter.INFINITE_SCROLL_LIMIT / 2);
            mSliderPager.setCurrentItem(midpoint + position, true);
        } else {
            throw new NullPointerException("Adapter not set");
        }
    }

    /**
     * @return Nullable position of current sliding item.
     */
    public int getCurrentPagePosition() {

        if (getSliderAdapter() != null) {
            return getSliderPager().getCurrentItem() % mPagerAdapter.getCount();
        } else {
            throw new NullPointerException("Adapter not set");
        }
    }

    /**
     * @param duration modifies indicator animation duration.
     */
    public void setIndicatorAnimationDuration(long duration) {
        mPagerIndicator.setAnimationDuration(duration);
    }

    /**
     * @param gravity {@link #View} integer gravity of indicator dots.
     */
    public void setIndicatorGravity(int gravity) {
        FrameLayout.LayoutParams layoutParams = (LayoutParams) mPagerIndicator.getLayoutParams();
        layoutParams.gravity = gravity;
        mPagerIndicator.setLayoutParams(layoutParams);
    }

    /**
     * @param padding changes indicator padding.
     */
    public void setIndicatorPadding(int padding) {
        mPagerIndicator.setPadding(padding);
    }

    /**
     * Sets the indicator margins, in pixels.
     *
     * @param left   the left margin size
     * @param top    the top margin size
     * @param right  the right margin size
     * @param bottom the bottom margin size
     */
    public void setIndicatorMargins(int left, int top, int right, int bottom) {
        FrameLayout.LayoutParams layoutParams = (LayoutParams) mPagerIndicator.getLayoutParams();
        layoutParams.setMargins(left, top, right, bottom);
        mPagerIndicator.setLayoutParams(layoutParams);
    }

    /**
     * @param orientation changes orientation of indicator dots.
     */
    public void setIndicatorOrientation(Orientation orientation) {
        mPagerIndicator.setOrientation(orientation);
    }

    /**
     * @param animations {@link #SliderView#IndicatorAnimations} of indicator dots
     */
    public void setIndicatorAnimation(IndicatorAnimations animations) {

        switch (animations) {
            case DROP:
                mPagerIndicator.setAnimationType(AnimationType.DROP);
                break;
            case FILL:
                mPagerIndicator.setAnimationType(AnimationType.FILL);
                break;
            case NONE:
                mPagerIndicator.setAnimationType(AnimationType.NONE);
                break;
            case SWAP:
                mPagerIndicator.setAnimationType(AnimationType.SWAP);
                break;
            case WORM:
                mPagerIndicator.setAnimationType(AnimationType.WORM);
                break;
            case COLOR:
                mPagerIndicator.setAnimationType(AnimationType.COLOR);
                break;
            case SCALE:
                mPagerIndicator.setAnimationType(AnimationType.SCALE);
                break;
            case SLIDE:
                mPagerIndicator.setAnimationType(AnimationType.SLIDE);
                break;
            case SCALE_DOWN:
                mPagerIndicator.setAnimationType(AnimationType.SCALE_DOWN);
                break;
            case THIN_WORM:
                mPagerIndicator.setAnimationType(AnimationType.THIN_WORM);
                break;
        }
    }

    /**
     * @param visibility this method changes indicator visibility
     */
    public void setIndicatorVisibility(boolean visibility) {
        if (visibility) {
            mPagerIndicator.setVisibility(VISIBLE);
        } else {
            mPagerIndicator.setVisibility(GONE);
        }
    }

    /**
     * @return number of items in {@link #SliderView#SliderViewAdapter)}
     */
    private int getAdapterItemsCount() {
        try {
            return getSliderAdapter().getCount();
        } catch (NullPointerException e) {
            Log.e(TAG, "getAdapterItemsCount: Slider Adapter is null so," +
                    " it can't get count of items");
            return 0;
        }
    }

    /**
     * This method stars the auto cycling
     */
    public void startAutoCycle() {
        //clean previous callbacks
        mHandler.removeCallbacks(this);

        //Run the loop for the first time
        mHandler.postDelayed(this, mScrollTimeInMillis);
    }

    /**
     * This method cancels the auto cycling
     */
    public void stopAutoCycle() {
        //clean callback
        mHandler.removeCallbacks(this);
    }

    /**
     * This method setting direction of sliders auto cycling
     * accepts constant values defined in {@link #SliderView} class
     * {@value AUTO_CYCLE_DIRECTION_LEFT}
     * {@value AUTO_CYCLE_DIRECTION_RIGHT}
     * {@value AUTO_CYCLE_DIRECTION_BACK_AND_FORTH}
     */
    public void setAutoCycleDirection(int direction) {
        mAutoCycleDirection = direction;
    }

    /**
     * @return direction of auto cycling
     * {@value AUTO_CYCLE_DIRECTION_LEFT}
     * {@value AUTO_CYCLE_DIRECTION_RIGHT}
     * {@value AUTO_CYCLE_DIRECTION_BACK_AND_FORTH}
     */
    public int getAutoCycleDirection() {
        return mAutoCycleDirection;
    }

    /**
     * @return size of indicator dot
     */
    public int getIndicatorRadius() {
        return mPagerIndicator.getRadius();
    }

    /**
     * @param rtlMode for indicator sliding direction
     */
    public void setIndicatorRtlMode(RtlMode rtlMode) {
        mPagerIndicator.setRtlMode(rtlMode);
    }

    /**
     * @param pagerIndicatorRadius modifies size of indicator dots
     */
    public void setIndicatorRadius(int pagerIndicatorRadius) {
        this.mPagerIndicator.setRadius(pagerIndicatorRadius);
    }

    /**
     * @param margin modifies indicator margin
     */
    public void setIndicatorMargin(int margin) {
        FrameLayout.LayoutParams layoutParams = (LayoutParams) mPagerIndicator.getLayoutParams();
        layoutParams.setMargins(margin, margin, margin, margin);
        mPagerIndicator.setLayoutParams(layoutParams);
    }

    /**
     * @param color setting color of selected dot
     */
    public void setIndicatorSelectedColor(int color) {
        this.mPagerIndicator.setSelectedColor(color);
    }

    /**
     * @return color of selected dot
     */
    public int getIndicatorSelectedColor() {
        return this.mPagerIndicator.getSelectedColor();
    }

    public void setIndicatorUnselectedColor(int color) {
        this.mPagerIndicator.setUnselectedColor(color);
    }

    /**
     * @return color of unselected dots
     */
    public int getIndicatorUnselectedColor() {
        return this.mPagerIndicator.getUnselectedColor();
    }

    /**
     * This method handles sliding behaviors
     * which passed into {@link #SliderView#mHandler}
     * <p>
     * see {@link #SliderView#startAutoCycle()}
     */
    @Override
    public void run() {
        try {
            if (!mPausedSliding) {
                // slide to next if not paused
                slideToNextPosition();
            }
        } finally {
            if (mIsAutoCycle) {
                // continue the loop
                mHandler.postDelayed(this, mScrollTimeInMillis);
            }
        }
    }

    public void slideToNextPosition() {

        int currentPosition = mSliderPager.getCurrentItem();
        int adapterItemsCount = getAdapterItemsCount();

        if (mAutoCycleDirection == AUTO_CYCLE_DIRECTION_BACK_AND_FORTH && adapterItemsCount > 1) {
            if (currentPosition % (adapterItemsCount - 1) == 0) {
                mFlagBackAndForth = !mFlagBackAndForth;
            }
            if (mFlagBackAndForth) {
                mSliderPager.setCurrentItem(++currentPosition, true);
            } else {
                mSliderPager.setCurrentItem(--currentPosition, true);
            }
        } else if (mAutoCycleDirection == AUTO_CYCLE_DIRECTION_LEFT) {
            mSliderPager.setCurrentItem(--currentPosition, true);
        } else {
            mSliderPager.setCurrentItem(++currentPosition, true);
        }
    }


    public void slideToPreviousPosition() {

        int currentPosition = mSliderPager.getCurrentItem();
        int adapterItemsCount = getAdapterItemsCount();

        if (mAutoCycleDirection == AUTO_CYCLE_DIRECTION_BACK_AND_FORTH && adapterItemsCount > 1) {
            if (currentPosition % (adapterItemsCount - 1) == 0) {
                mFlagBackAndForth = !mFlagBackAndForth;
            }
            if (mFlagBackAndForth) {
                mSliderPager.setCurrentItem(--currentPosition, true);
            } else {
                mSliderPager.setCurrentItem(++currentPosition, true);
            }
        } else if (mAutoCycleDirection == AUTO_CYCLE_DIRECTION_LEFT) {
            mSliderPager.setCurrentItem(++currentPosition, true);
        } else {
            mSliderPager.setCurrentItem(--currentPosition, true);
        }
    }

    //sync infinite pager adapter with real one
    @Override
    public void dataSetChanged() {
        if (mIsInfiniteAdapter) {
            mInfinitePagerAdapter.notifyDataSetChanged();
            mSliderPager.setCurrentItem((getAdapterItemsCount() - 1) * (InfinitePagerAdapter.INFINITE_SCROLL_LIMIT / 2), false);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // nothing to do
    }

    @Override
    public void onPageSelected(int position) {
        if (mPageListener != null) {
            mPageListener.onSliderPageChanged(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // nothing to do
    }

    public interface OnSliderPageListener {

        /**
         * This method will be invoked when a new page becomes selected. Animation is not
         * necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        void onSliderPageChanged(int position);

    }
}
