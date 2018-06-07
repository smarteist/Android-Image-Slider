package com.smarteist.autoimageslider;

import android.support.v4.view.ViewPager;

class CircularSliderHandle implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private int mCurrentPosition;

    private CurrentPageListener currentPageListener;

    CircularSliderHandle(final ViewPager viewPager) {
        mViewPager = viewPager;
    }

    void setCurrentPageListener(CurrentPageListener currentPageListener) {
        this.currentPageListener = currentPageListener;
    }

    @Override
    public void onPageSelected(final int position) {
        mCurrentPosition = position;
        currentPageListener.onCurrentPageChanged(mCurrentPosition);
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
        int currentPage = mViewPager.getCurrentItem();
        if (currentPage == mViewPager.getAdapter().getCount()-1 || currentPage == 0){
            int previousState = mCurrentPosition;
            mCurrentPosition = state;
            if (previousState == 1 && mCurrentPosition == 0){
                mViewPager.setCurrentItem(currentPage == 0 ? mViewPager.getAdapter().getCount()-1 : 0);
            }
        }
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
    }

    interface CurrentPageListener {
        void onCurrentPageChanged(int currentPosition);
    }
}
