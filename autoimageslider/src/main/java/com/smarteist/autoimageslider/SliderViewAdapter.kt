package com.smarteist.autoimageslider

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import java.util.ArrayDeque

/**
 * A base PagerAdapter that recycles ViewHolders.
 * Subclasses must implement getCount(), onCreateViewHolder(), and onBindViewHolder().
 *
 * @param VH a non‐nullable ViewHolder type
 */
abstract class SliderViewAdapter<VH : SliderViewAdapter.ViewHolder> : PagerAdapter() {

    /** Simple listener interface to notify if the dataset has changed. */
    interface DataSetListener {
        fun dataSetChanged()
    }

    /** Default ViewHolder, holding a single [itemView]. */
    abstract class ViewHolder(val itemView: View)

    private var dataSetListener: DataSetListener? = null

    /** Queue of recycled ViewHolders. */
    private val recycledHolders: ArrayDeque<VH> = ArrayDeque()

    /**
     * Return the “real” number of items (before infinite‐loop wrapping).
     * Subclasses can override if they want to support infinite scrolling.
     * By default, it just returns whatever getCount() returns.
     */
    open fun realItemCount(): Int = count

    /**
     * Subclasses must override this to return how many pages there are.
     * If you want “infinite” behavior, return a large number (e.g. Int.MAX_VALUE),
     * and implement onBindViewHolder so that it does position % realItemCount().
     */
    abstract override fun getCount(): Int

    /**
     * Create a brand‐new ViewHolder (and its itemView) for the given container.
     */
    abstract fun onCreateViewHolder(parent: ViewGroup): VH

    /**
     * Bind data into [holder] for display at [position].
     * If you’re doing “infinite scrolling,” use `position % realItemCount()`.
     */
    abstract fun onBindViewHolder(holder: VH, position: Int)

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // Try to reuse an existing holder, or create a new one if none are available.
        val holder: VH = recycledHolders.pollFirst() ?: onCreateViewHolder(container)

        // Add its view to the ViewPager
        container.addView(holder.itemView)
        // Bind data for this position
        onBindViewHolder(holder, position)
        return holder
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        @Suppress("UNCHECKED_CAST")
        val holder = `object` as VH
        container.removeView(holder.itemView)
        recycledHolders.addLast(holder)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        @Suppress("UNCHECKED_CAST")
        val holder = `object` as VH
        return holder.itemView === view
    }

    override fun getItemPosition(`object`: Any): Int = POSITION_NONE

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        dataSetListener?.dataSetChanged()
    }

    /** Register a listener to be notified when the data set changes. */
    fun setDataSetListener(listener: DataSetListener?) {
        dataSetListener = listener
    }
}
