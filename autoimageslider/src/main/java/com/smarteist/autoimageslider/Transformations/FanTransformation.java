package com.smarteist.autoimageslider.Transformations;

import android.support.v4.view.ViewPager;
import android.view.View;

public class FanTransformation implements ViewPager.PageTransformer{
    @Override
    public void transformPage(View page, float position) {

        page.setTranslationX(-position*page.getWidth());
        page.setPivotX(0);
        page.setPivotY(page.getHeight()/2);
        page.setCameraDistance(20000);

        if (position < -1){     // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(0);

        }
        else if (position <= 0){    // [-1,0]
            page.setAlpha(1);
            page.setRotationY(-120*Math.abs(position));
        }
        else if (position <= 1){    // (0,1]
            page.setAlpha(1);
            page.setRotationY(120*Math.abs(position));

        }
        else {    // (1,+Infinity]
            // This page is way off-screen to the right.
            page.setAlpha(0);

        }


    }
}