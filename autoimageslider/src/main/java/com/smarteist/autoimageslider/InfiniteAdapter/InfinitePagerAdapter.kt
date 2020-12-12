package com.smarteist.autoimageslider.InfiniteAdapter

import android.database.DataSetObserver
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.smarteist.autoimageslider.SliderViewAdapter

/**
 * Its just a wrapper adapter class for providing infinite behavior
 * for slider.
 */
class InfinitePagerAdapter(private val adapter: SliderViewAdapter<*>) : PagerAdapter() {
    val realAdapter: PagerAdapter get() = adapter

    override fun getCount(): Int {
        return if (realCount < 1) {
            0
        } else realCount * INFINITE_SCROLL_LIMIT
        // warning: infinite scroller actually is not infinite!
        // very big number will be cause memory problems.
    }

    /**
     * @return the [.getCount] result of the wrapped adapter
     */
    val realCount: Int
        get() = try {
            realAdapter.count
        } catch (e: Exception) {
            0
        }

    /**
     * @param item real position of item
     * @return virtual mid point
     */
    fun getMiddlePosition(item: Int): Int {
        val midpoint = Math.max(0, realCount) * (INFINITE_SCROLL_LIMIT / 2)
        return item + midpoint
    }

    override fun instantiateItem(container: ViewGroup, virtualPosition: Int): Any {
        // prevent division by zer
        return if (realCount < 1) {
            adapter.instantiateItem(container, 0)
        } else adapter.instantiateItem(container, getRealPosition(virtualPosition))
        //Log.i(TAG, "instantiateItem: real virtualPosition: " + virtualPosition);
        //Log.i(TAG, "instantiateItem: virtual virtualPosition: " + virtualPosition);

        // only expose virtual virtualPosition to the inner adapter
    }

    override fun destroyItem(container: ViewGroup, virtualPosition: Int, `object`: Any) {
        // prevent division by zero
        if (realCount < 1) {
            adapter.destroyItem(container, 0, `object`)
            return
        }
        //Log.i(TAG, "destroyItem: real position: " + position);
        //Log.i(TAG, "destroyItem: virtual position: " + virtualPosition);

        // only expose virtual position to the inner adapter
        adapter.destroyItem(container, getRealPosition(virtualPosition), `object`)
    }

    override fun startUpdate(container: ViewGroup) {
        adapter.startUpdate(container)
    }

    /*
     * Delegate rest of methods directly to the inner adapter.
     */
    override fun finishUpdate(container: ViewGroup) {
        adapter.finishUpdate(container)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return adapter.isViewFromObject(view, `object`)
    }

    override fun restoreState(bundle: Parcelable?, classLoader: ClassLoader?) {
        adapter.restoreState(bundle, classLoader)
    }

    override fun saveState(): Parcelable? {
        return adapter.saveState()
    }

    override fun getPageTitle(virtualPosition: Int): CharSequence? {
        return adapter.getPageTitle(getRealPosition(virtualPosition))
    }

    override fun getPageWidth(position: Int): Float {
        return adapter.getPageWidth(position)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        adapter.setPrimaryItem(container, position, `object`)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        adapter.unregisterDataSetObserver(observer)
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {
        adapter.registerDataSetObserver(observer)
    }

    override fun getItemPosition(`object`: Any): Int {
        return adapter.getItemPosition(`object`)
    }

    fun getRealPosition(virtualPosition: Int): Int {
        return if (realCount > 0) {
            virtualPosition % realCount
        } else 0
    }

    companion object {
        // Warning: it should be an even number.
        const val INFINITE_SCROLL_LIMIT = 32400
        private const val TAG = "InfinitePagerAdapter"
    }
}