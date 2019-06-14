package com.smarteist.autoimageslider;

import androidx.viewpager.widget.ViewPager;

class CircularSliderHandle implements ViewPager.OnPageChangeListener {

    private CurrentPageListener currentPageListener;
    private ViewPager mViewPager;
    private int mCurrentPosition;
    private int mPreviousPosition;
    private boolean mIsEndOfCycle;

    CircularSliderHandle(final ViewPager viewPager) {
        this.mViewPager = viewPager;
    }

    void setCurrentPageListener(CurrentPageListener currentPageListener) {
        this.currentPageListener = currentPageListener;
    }

    @Override
    public void onPageSelected(final int position) {
        this.mCurrentPosition = position;
        if (this.currentPageListener != null) {
            this.currentPageListener.onCurrentPageChanged(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

        if (state == ViewPager.SCROLL_STATE_IDLE) {

            if (mPreviousPosition == mCurrentPosition && !mIsEndOfCycle) {

                if (mCurrentPosition == 0) {
                    mViewPager.setCurrentItem(getAdapterItemsCount() - 1);
                } else {
                    mViewPager.setCurrentItem(0);
                }

                mIsEndOfCycle = true;
            } else {
                mIsEndOfCycle = false;
            }

            mPreviousPosition = mCurrentPosition;
        }

    }

    private int getAdapterItemsCount() {
        try {
            return mViewPager.getAdapter().getCount();
        } catch (NullPointerException e) {
            return 0;
        }
    }


    @Override
    public void onPageScrolled(final int position, final float positionOffset,
                               final int positionOffsetPixels) {
    }

    interface CurrentPageListener {
        void onCurrentPageChanged(int currentPosition);
    }
}