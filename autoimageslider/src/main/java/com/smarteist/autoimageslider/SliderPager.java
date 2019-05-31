package com.smarteist.autoimageslider;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import androidx.viewpager.widget.ViewPager;
import java.lang.reflect.Field;

public class SliderPager extends ViewPager {

    public static final int DEFAULT_SCROLL_DURATION = 250;

    public SliderPager (Context context) {
        super(context);
    }

    public SliderPager (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollDuration(int millis) {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new OwnScroller(getContext(), millis));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class OwnScroller extends Scroller {

        private int durationScrollMillis;

        OwnScroller(Context context, int durationScroll) {
            super(context, new DecelerateInterpolator());
            this.durationScrollMillis = durationScroll;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, durationScrollMillis);
        }
    }
}
