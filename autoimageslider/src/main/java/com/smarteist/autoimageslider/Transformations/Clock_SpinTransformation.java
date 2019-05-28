package com.smarteist.autoimageslider.Transformations;

import androidx.viewpager.widget.ViewPager;

import android.view.View;

public class Clock_SpinTransformation implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {

        page.setTranslationX(-position * page.getWidth());

        if (Math.abs(position) <= 0.5) {
            page.setVisibility(View.VISIBLE);
            page.setScaleX(1 - Math.abs(position));
            page.setScaleY(1 - Math.abs(position));
        } else if (Math.abs(position) > 0.5) {
            page.setVisibility(View.GONE);
        }


        if (position < -1){  // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(0);

        }
        else if (position <= 0) {   // [-1,0]
            page.setAlpha(1);
            page.setRotation(360 * Math.abs(position));

        }
        else if (position <= 1) {   // (0,1]
            page.setAlpha(1);
            page.setRotation(-360 * Math.abs(position));

        }
        else {  // (1,+Infinity]
            // This page is way off-screen to the right.
            page.setAlpha(0);

        }


    }
}