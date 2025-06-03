package com.smarteist.autoimageslider.InfiniteAdapter

import android.database.DataSetObserver
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.smarteist.autoimageslider.SliderViewAdapter

/**
 * Wraps a [SliderViewAdapter] to provide “infinite” scrolling behavior.
 * Internally, it multiplies the real item count by [INFINITE_SCROLL_FACTOR] to avoid
 * reaching the natural ends. All positions are mapped back to the real adapter via modulo.
 */
class InfinitePagerAdapter(
    private val wrappedAdapter: SliderViewAdapter<*>
) : PagerAdapter() {

    /**
     * If the wrapped adapter has no items, we return 0.
     * Otherwise we pretend there are (realCount × INFINITE_SCROLL_FACTOR) pages.
     */
    override fun getCount(): Int {
        val realCount = wrappedAdapter.count
        return if (realCount <= 0) 0 else realCount * INFINITE_SCROLL_FACTOR
    }

    /**
     * Return the actual number of items in the wrapped adapter.
     */
    val realCount: Int
        get() = wrappedAdapter.count.coerceAtLeast(0)

    /**
     * Given a “real” item position, return a virtual midpoint that lands on that item.
     * Useful for starting the pager near the middle of the infinite range.
     */
    fun getMiddlePosition(realPosition: Int): Int {
        val count = realCount
        if (count <= 0) return 0
        val midpoint = (count * (INFINITE_SCROLL_FACTOR / 2))
        return midpoint + realPosition.coerceIn(0, count - 1)
    }

    override fun instantiateItem(container: ViewGroup, virtualPosition: Int): Any {
        val realPos = getRealPosition(virtualPosition)
        return wrappedAdapter.instantiateItem(container, realPos)
    }

    override fun destroyItem(container: ViewGroup, virtualPosition: Int, `object`: Any) {
        val realPos = getRealPosition(virtualPosition)
        wrappedAdapter.destroyItem(container, realPos, `object`)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return wrappedAdapter.isViewFromObject(view, `object`)
    }

    override fun startUpdate(container: ViewGroup) {
        wrappedAdapter.startUpdate(container)
    }

    override fun finishUpdate(container: ViewGroup) {
        wrappedAdapter.finishUpdate(container)
    }

    override fun saveState(): Parcelable? {
        return wrappedAdapter.saveState()
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        wrappedAdapter.restoreState(state, loader)
    }

    override fun getPageTitle(virtualPosition: Int): CharSequence? {
        return wrappedAdapter.getPageTitle(getRealPosition(virtualPosition))
    }

    override fun getPageWidth(virtualPosition: Int): Float {
        return wrappedAdapter.getPageWidth(getRealPosition(virtualPosition))
    }

    override fun setPrimaryItem(container: ViewGroup, virtualPosition: Int, `object`: Any) {
        wrappedAdapter.setPrimaryItem(container, getRealPosition(virtualPosition), `object`)
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {
        wrappedAdapter.registerDataSetObserver(observer)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        wrappedAdapter.unregisterDataSetObserver(observer)
    }

    override fun getItemPosition(`object`: Any): Int {
        return wrappedAdapter.getItemPosition(`object`)
    }

    /**
     * Map any “virtual” position to the equivalent real adapter position.
     */
    fun getRealPosition(virtualPosition: Int): Int {
        val count = realCount
        return if (count > 0) virtualPosition % count else 0
    }

    companion object {
        /**
         * Must be even; determines how many “cycles” we allow before reaching the end.
         *
         * For example, if realCount = 5 and INFINITE_SCROLL_FACTOR = 10, getCount() = 50.
         * The midpoint is at 25, so you can scroll 25 items in either direction before hitting bounds.
         */
        const val INFINITE_SCROLL_FACTOR = 32_400
    }
}
