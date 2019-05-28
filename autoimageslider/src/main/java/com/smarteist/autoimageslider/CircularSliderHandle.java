package com.smarteist.autoimageslider;

import androidx.viewpager.widget.ViewPager;

class CircularSliderHandle implements ViewPager.OnPageChangeListener {

    private CurrentPageListener currentPageListener;
    private ViewPager mViewPager;
    private boolean mIsOnLoopEnd = false;
    private int mCurrentPosition;
    private int mPreviousPosition;

    CircularSliderHandle(final ViewPager viewPager) {
        this.mViewPager = viewPager;
    }

    void setCurrentPageListener(CurrentPageListener currentPageListener) {
        this.currentPageListener = currentPageListener;
    }

    @Override
    public void onPageSelected(final int position) {
        this.mCurrentPosition = position;
        this.currentPageListener.onCurrentPageChanged(mCurrentPosition);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

        int itemsCount = getAdapterItemsCount();

        if (state == ViewPager.SCROLL_STATE_IDLE) {

            if (mPreviousPosition == mCurrentPosition && !mIsOnLoopEnd) {

                if (mCurrentPosition == 0) {
                    mViewPager.setCurrentItem(itemsCount - 1);
                    mIsOnLoopEnd = true;

                } else {
                    mViewPager.setCurrentItem(0);
                    mIsOnLoopEnd = true;
                }

            } else {
                mIsOnLoopEnd = false;
            }

            mPreviousPosition = mViewPager.getCurrentItem();
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