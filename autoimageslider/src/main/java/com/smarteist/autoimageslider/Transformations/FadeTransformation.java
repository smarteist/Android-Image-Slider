package com.smarteist.autoimageslider.Transformations;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class FadeTransformation implements ViewPager.PageTransformer{
    @Override
    public void transformPage(View view, float position) {

        view.setTranslationX(-position*view.getWidth());

        // Page is not an immediate sibling, just make transparent
        if(position < -1 || position > 1) {
            view.setAlpha(0);
        }
        // Page is sibling to left or right
        else if (position <= 0 || position <= 1) {

            // Calculate alpha.  Position is decimal in [-1,0] or [0,1]
            float alpha = (position <= 0) ? position + 1 : 1 - position;
            view.setAlpha(alpha);

        }
        // Page is active, make fully visible
        else if (position == 0) {
            view.setAlpha(1);
        }



    }
}