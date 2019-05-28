package com.smarteist.autoimageslider.Transformations;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class HingeTransformation implements ViewPager.PageTransformer{
    @Override
    public void transformPage(View page, float position) {

        page.setTranslationX(-position*page.getWidth());
        page.setPivotX(0);
        page.setPivotY(0);


        if (position < -1){    // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(0);

        }
        else if (position <= 0){    // [-1,0]
            page.setRotation(90*Math.abs(position));
            page.setAlpha(1-Math.abs(position));

        }
        else if (position <= 1){    // (0,1]
            page.setRotation(0);
            page.setAlpha(1);

        }
        else {    // (1,+Infinity]
            // This page is way off-screen to the right.
            page.setAlpha(0);

        }


    }
}