package com.smarteist.autoimageslider;

import android.support.v4.view.ViewPager;
import android.util.Log;

import static android.content.ContentValues.TAG;

class CircularSliderHandle implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private int mCurrentPosition;

    private CurrentPageListener currentPageListener;
    private int mScrollState;

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

    private void handleScrollState(final int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            setNextItemIfNeeded();
        }
    }

    private void setNextItemIfNeeded() {
        if (!isScrollStateSettling()) {
            handleSetNextItem();
        }
    }

    private boolean isScrollStateSettling() {
        return mScrollState == ViewPager.SCROLL_STATE_SETTLING;
    }

    private void handleSetNextItem() {
        final int lastPosition = mViewPager.getAdapter().getCount() - 1;
        if (mCurrentPosition == 0) {
            mViewPager.setCurrentItem(lastPosition, false);
        } else if (mCurrentPosition == lastPosition) {
            mViewPager.setCurrentItem(0, false);
        }
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
        Log.d(TAG, "onPageScrollStateChanged: " + state + "   " + mCurrentPosition);
//        int currentPage = mViewPager.getCurrentItem();
//        if (currentPage == mViewPager.getAdapter().getCount()-1 || currentPage == 0){
//            int previousState = mCurrentPosition;
//            mCurrentPosition = state;
//            if (previousState == 1 && mCurrentPosition == 0){
//                mViewPager.setCurrentItem(currentPage == 0 ? mViewPager.getAdapter().getCount()-1 : 0);
//            }
//            boolean leftSwipe = mPreviousPosition==
//            if (state == ViewPager.SCROLL_STATE_SETTLING){
//                mViewPager.setCurrentItem(currentPage == 0 ? mViewPager.getAdapter().getCount()-1 : 0);
//            }
//        }
        handleScrollState(state);
        mScrollState = state;
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
    }

    interface CurrentPageListener {
        void onCurrentPageChanged(int currentPosition);
    }
}
