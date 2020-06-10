package com.smarteist.autoimageslider.InfiniteAdapter;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.smarteist.autoimageslider.SliderViewAdapter;


/**
 * Its just a wrapper adapter class for providing infinite behavior
 * for slider.
 */
public class InfinitePagerAdapter extends PagerAdapter {

    // Warning: it should be an even number.
    public static final int INFINITE_SCROLL_LIMIT = 32400;
    private static final String TAG = "InfinitePagerAdapter";
    private SliderViewAdapter adapter;

    public InfinitePagerAdapter(SliderViewAdapter adapter) {
        this.adapter = adapter;
    }

    public PagerAdapter getRealAdapter() {
        return this.adapter;
    }

    @Override
    public int getCount() {
        if (getRealCount() < 1) {
            return 0;
        }
        // warning: infinite scroller actually is not infinite!
        // very big number will be cause memory problems.
        return getRealCount() * INFINITE_SCROLL_LIMIT;
    }

    /**
     * @return the {@link #getCount()} result of the wrapped adapter
     */
    public int getRealCount() {
        try {
            return getRealAdapter().getCount();
        } catch (Exception e) {
            return 0;
        }
    }


    /**
     * @param item real position of item
     * @return virtual mid point
     */
    public int getMiddlePosition(int item) {
        int adapterCount = getRealCount() - 1;
        adapterCount = adapterCount == 0 ? 1 : adapterCount;
        int midpoint = adapterCount * (InfinitePagerAdapter.INFINITE_SCROLL_LIMIT / 2);
        return item + midpoint;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int virtualPosition) {
        // prevent division by zer
        if (getRealCount() < 1) {
            return adapter.instantiateItem(container, 0);
        }
        //Log.i(TAG, "instantiateItem: real virtualPosition: " + virtualPosition);
        //Log.i(TAG, "instantiateItem: virtual virtualPosition: " + virtualPosition);

        // only expose virtual virtualPosition to the inner adapter
        return adapter.instantiateItem(container, getRealPosition(virtualPosition));
    }

    @Override
    public void destroyItem(ViewGroup container, int virtualPosition, Object object) {
        // prevent division by zero
        if (getRealCount() < 1) {
            adapter.destroyItem(container, 0, object);
            return;
        }
        //Log.i(TAG, "destroyItem: real position: " + position);
        //Log.i(TAG, "destroyItem: virtual position: " + virtualPosition);

        // only expose virtual position to the inner adapter
        adapter.destroyItem(container, getRealPosition(virtualPosition), object);
    }

    @Override
    public void startUpdate(ViewGroup container) {
        adapter.startUpdate(container);
    }

    /*
     * Delegate rest of methods directly to the inner adapter.
     */
    @Override
    public void finishUpdate(ViewGroup container) {
        adapter.finishUpdate(container);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return adapter.isViewFromObject(view, object);
    }

    @Override
    public void restoreState(Parcelable bundle, ClassLoader classLoader) {
        adapter.restoreState(bundle, classLoader);
    }

    @Override
    public Parcelable saveState() {
        return adapter.saveState();
    }

    @Override
    public CharSequence getPageTitle(int virtualPosition) {
        return adapter.getPageTitle(getRealPosition(virtualPosition));
    }

    @Override
    public float getPageWidth(int position) {
        return adapter.getPageWidth(position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        adapter.setPrimaryItem(container, position, object);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        adapter.unregisterDataSetObserver(observer);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        adapter.registerDataSetObserver(observer);
    }

    @Override
    public int getItemPosition(Object object) {
        return adapter.getItemPosition(object);
    }

    public int getRealPosition(int virtualPosition) {
        if (getRealCount() > 0) {
            return virtualPosition % getRealCount();
        }
        return 0;
    }
}