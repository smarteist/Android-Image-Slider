package com.smarteist.autoimageslider;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


class SliderAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<SliderView> sliderViews = new ArrayList<>();

    SliderAdapter(Context context) {
        this.context = context;
    }

    public void setSliderViews(ArrayList<SliderView> sliderViews) {
        this.sliderViews = sliderViews;
    }

    void addSliderView(SliderView view) {
        sliderViews.add(view);
        notifyDataSetChanged();
    }

    public void removeAllSliderViews() {
        sliderViews.clear();
        notifyDataSetChanged();
    }

    public SliderView getSliderView(int position) {
        if (sliderViews.isEmpty() || position >= sliderViews.size()) {
            return null;
        }
        return sliderViews.get(position);
    }

    @Override
    public int getCount() {
        return sliderViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        SliderView imageSliderView = sliderViews.get(position);
        View v = imageSliderView.getView();
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
