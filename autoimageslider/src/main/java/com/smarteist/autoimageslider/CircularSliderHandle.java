package com.smarteist.autoimageslider;

import android.support.v4.view.ViewPager;

class CircularSliderHandle implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private int mCurrentPosition;
    private int mPreviousState;

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
        if(mPreviousState == 1 && state ==0){
            int currentPage = mViewPager.getCurrentItem();
            int previousPage = mCurrentPosition;

            if((currentPage == previousPage) && (currentPage == mViewPager.getAdapter().getCount()-1 || currentPage == 0)){
                mViewPager.setCurrentItem(currentPage == 0 ? mViewPager.getAdapter().getCount()-1 : 0);
            }
        }
        mPreviousState = state;
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
    }

    interface CurrentPageListener {
        void onCurrentPageChanged(int currentPosition);
    }
}
