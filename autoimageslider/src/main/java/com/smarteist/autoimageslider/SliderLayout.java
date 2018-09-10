package com.smarteist.autoimageslider;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;


import com.smarteist.autoimageslider.IndicatorView.PageIndicatorView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.AnimationType;

import java.util.Timer;
import java.util.TimerTask;

public class SliderLayout extends FrameLayout implements CircularSliderHandle.CurrentPageListener {


    private static final long DELAY_MS = 500;


    private static PagerAdapter mFlippingPagerAdapter;

    int currentPage = 0;
    CircularSliderHandle circularSliderHandle;

    private ViewPager mFlippingPager;

    private PageIndicatorView pagerIndicator;

    private int scrollTimeInSec = 2;
    private boolean scrollingEnabled = true;

    private Handler handler = new Handler();
    private Timer flippingTimer;

    public SliderLayout(Context context) {
        super(context);
        setLayout(context, null);
    }

    public SliderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayout(context, attrs);
    }

    public SliderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayout(context, attrs);
    }

    public enum Animations {
        WORM,
        THIN_WORM,
        COLOR,
        DROP,
        FILL,
        NONE,
        SCALE,
        SCALE_DOWN,
        SLIDE,
        SWAP,
    }


    private static PagerAdapter getFlippingPagerAdapter() {
        return mFlippingPagerAdapter;
    }

    public int getScrollTimeInSec() {
        return scrollTimeInSec;
    }


    public void setScrollTimeInSec(int time) {
        scrollTimeInSec = time;
        startAutoCycle();
    }

    public int getCurrentPagePosition() {
        if (getFlippingPagerAdapter() != null) {
            return mFlippingPager.getCurrentItem() % mFlippingPagerAdapter.getCount();
        } else {
            throw new NullPointerException("Adapter not set");
        }
    }

    public void setIndicatorAnimation(Animations animations) {
        switch (animations) {
            case DROP:
                pagerIndicator.setAnimationType(AnimationType.DROP);
                break;
            case FILL:
                pagerIndicator.setAnimationType(AnimationType.FILL);
                break;
            case NONE:
                pagerIndicator.setAnimationType(AnimationType.NONE);
                break;
            case SWAP:
                pagerIndicator.setAnimationType(AnimationType.SWAP);
                break;
            case WORM:
                pagerIndicator.setAnimationType(AnimationType.WORM);
                break;
            case COLOR:
                pagerIndicator.setAnimationType(AnimationType.COLOR);
                break;
            case SCALE:
                pagerIndicator.setAnimationType(AnimationType.SCALE);
                break;
            case SLIDE:
                pagerIndicator.setAnimationType(AnimationType.SLIDE);
                break;
            case SCALE_DOWN:
                pagerIndicator.setAnimationType(AnimationType.SCALE_DOWN);
                break;
            case THIN_WORM:
                pagerIndicator.setAnimationType(AnimationType.THIN_WORM);
                break;
        }
    }


    private void setLayout(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PageIndicatorView, 0, 0);
        scrollingEnabled = typedArray.getBoolean(R.styleable.PageIndicatorView_piv_enable_auto_scrolling, true);
        typedArray.recycle();
        View view = LayoutInflater.from(context).inflate(R.layout.slider_layout, this, true);
        mFlippingPager = view.findViewById(R.id.vp_slider_layout);
        pagerIndicator = view.findViewById(R.id.pager_indicator);
        pagerIndicator.setDynamicCount(true);

        mFlippingPagerAdapter = new SliderAdapter(context);

        mFlippingPager.setAdapter(mFlippingPagerAdapter);

        // Handler for onPageChangeListener
        circularSliderHandle = new CircularSliderHandle(mFlippingPager);
        circularSliderHandle.setCurrentPageListener(this);
        mFlippingPager.addOnPageChangeListener(circularSliderHandle);

        //Starting auto cycle at the time of setting up of layout
        startAutoCycle();
    }

    public void enableAutoScrolling(boolean scroll) {
        scrollingEnabled = scroll;
        startAutoCycle();
    }

    public void addSliderView(SliderView sliderView) {
        ((SliderAdapter) mFlippingPagerAdapter).addSliderView(sliderView);
        if (pagerIndicator != null && mFlippingPager != null) {
            pagerIndicator.setViewPager(mFlippingPager);
        }
    }


    private void startAutoCycle() {

        if (!(flippingTimer == null)) {
            flippingTimer.cancel();
        }
        //Cancel If Thread is Running
        if (scrollingEnabled) {
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == getFlippingPagerAdapter().getCount()) {
                        currentPage = 0;
                    }
                    // true set for smooth transition between pager
                    mFlippingPager.setCurrentItem(currentPage++, true);
                }
            };

            flippingTimer = new Timer();
            flippingTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, DELAY_MS, scrollTimeInSec * 1000);
        }
    }

    @Override
    public void onCurrentPageChanged(int currentPosition) {
        this.currentPage = currentPosition;
    }
}
