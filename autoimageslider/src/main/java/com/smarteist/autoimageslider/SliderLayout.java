package com.smarteist.autoimageslider;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.smarteist.autoimageslider.IndicatorView.PageIndicatorView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.AnimationType;
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

import java.util.Timer;
import java.util.TimerTask;

public class SliderLayout extends FrameLayout implements CircularSliderHandle.CurrentPageListener {


    private static final long DELAY_MS = 500;
    private static PagerAdapter mFlippingPagerAdapter;
    private int currentPage = 0;
    private CircularSliderHandle circularSliderHandle;
    private ViewPager mSliderPager;
    private PageIndicatorView pagerIndicator;
    private int scrollTimeInSec = 2;
    private Handler handler = new Handler();
    private Timer flippingTimer;
    private boolean autoScrolling = true;


    public SliderLayout(Context context) {
        super(context);
        setLayout(context);
    }

    public SliderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayout(context);
    }

    public SliderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayout(context);
    }

    public boolean isAutoScrolling() {
        return autoScrolling;
    }

    public void setAutoScrolling(boolean autoScrolling) {
        this.autoScrolling = autoScrolling;
        startAutoCycle();
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
            case SIMPLETRANSFORMATION:
                mSliderPager.setPageTransformer(false, new SimpleTransformation());
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

    public void setCustomSliderTransformAnimation(ViewPager.PageTransformer animation) {
        mSliderPager.setPageTransformer(false, animation);
    }

    public int getCurrentPagePosition() {
        if (getFlippingPagerAdapter() != null) {
            return mSliderPager.getCurrentItem() % mFlippingPagerAdapter.getCount();
        } else {
            throw new NullPointerException("Adapter not set");
        }
    }

    public void setIndicatorAnimation(IndicatorAnimations animations) {
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

    public void setPagerIndicatorVisibility(boolean visibility) {
        if (visibility) {
            pagerIndicator.setVisibility(VISIBLE);
        } else {
            pagerIndicator.setVisibility(GONE);
        }
    }

    private void setLayout(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.slider_layout, this, true);
        mSliderPager = view.findViewById(R.id.vp_slider_layout);
        pagerIndicator = view.findViewById(R.id.pager_indicator);
        pagerIndicator.setDynamicCount(true);

        mFlippingPagerAdapter = new SliderAdapter(context);

        mSliderPager.setAdapter(mFlippingPagerAdapter);

        // Handler for onPageChangeListener
        circularSliderHandle = new CircularSliderHandle(mSliderPager);
        circularSliderHandle.setCurrentPageListener(this);
        mSliderPager.addOnPageChangeListener(circularSliderHandle);

        //Starting auto cycle at the time of setting up of layout
        startAutoCycle();
    }

    public void clearSliderViews() {
        ((SliderAdapter) mFlippingPagerAdapter).removeAllSliderViews();
    }

    public void addSliderView(SliderView sliderView) {
        ((SliderAdapter) mFlippingPagerAdapter).addSliderView(sliderView);
        if (pagerIndicator != null && mSliderPager != null) {
            pagerIndicator.setViewPager(mSliderPager);
        }
    }

    private void startAutoCycle() {

        if (flippingTimer != null) {
            flippingTimer.cancel();
            flippingTimer.purge();
        }

        if (!autoScrolling) return;

        //Cancel If Thread is Running
        final Runnable scrollingThread = new Runnable() {
            public void run() {
                if (currentPage == getFlippingPagerAdapter().getCount()) {
                    currentPage = 0;
                }
                // true set for smooth transition between pager
                mSliderPager.setCurrentItem(currentPage++, true);
            }

        };

        flippingTimer = new Timer();
        flippingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(scrollingThread);
            }
        }, DELAY_MS, scrollTimeInSec * 1000);
    }

    @Override
    public void onCurrentPageChanged(int currentPosition) {
        this.currentPage = currentPosition;
    }
}
