package com.smarteist.autoimageslider.Transformations;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class TossTransformation implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {

        page.setTranslationX(-position * page.getWidth());
        page.setCameraDistance(20000);


        if (position < 0.5 && position > -0.5) {
            page.setVisibility(View.VISIBLE);

        } else {
            page.setVisibility(View.INVISIBLE);

        }


        if (position < -1) {     // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(0);

        }
        else if (position <= 0) {    // [-1,0]
            page.setAlpha(1);
            page.setScaleX(Math.max(0.4f, (1 - Math.abs(position))));
            page.setScaleY(Math.max(0.4f, (1 - Math.abs(position))));
            page.setRotationX(1080 * (1 - Math.abs(position) + 1));
            page.setTranslationY(-1000*Math.abs(position));

        }
        else if (position <= 1) {    // (0,1]
            page.setAlpha(1);
            page.setScaleX(Math.max(0.4f, (1-Math.abs(position))));
            page.setScaleY(Math.max(0.4f, (1-Math.abs(position))));
            page.setRotationX(-1080 * (1 - Math.abs(position) + 1));
            page.setTranslationY(-1000*Math.abs(position));

        }
        else {    // (1,+Infinity]
            // This page is way off-screen to the right.
            page.setAlpha(0);

        }
    }
}