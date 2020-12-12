package com.smarteist.autoimageslider

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.smarteist.autoimageslider.SliderViewAdapter.ViewHolder
import java.util.*

abstract class SliderViewAdapter<VH : ViewHolder?> : PagerAdapter() {
    private var dataSetListener: DataSetListener? = null

    //Default View holder class
    abstract class ViewHolder(val itemView: View)

    private val destroyedItems: Queue<VH> = LinkedList()
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var viewHolder = destroyedItems.poll()
        if (viewHolder == null) {
            viewHolder = onCreateViewHolder(container)
        }
        // Re-add existing view before rendering so that we can make change inside getView()
        container.addView(viewHolder!!.itemView)
        onBindViewHolder(viewHolder, position)
        return viewHolder
    }

    override fun destroyItem(container: ViewGroup, position: Int, objectHere: Any) {
        container.removeView((objectHere as VH)!!.itemView)
        destroyedItems.add(objectHere as VH)
    }

    override fun isViewFromObject(view: View, objectHere: Any): Boolean {
        return (objectHere as VH)!!.itemView === view
    }

    override fun getItemPosition(objectHere: Any): Int {
        return POSITION_NONE
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        if (dataSetListener != null) {
            dataSetListener!!.dataSetChanged()
        }
    }

    /**
     * Create a new view holder
     *
     * @param parent wrapper view
     * @return view holder
     */
    abstract fun onCreateViewHolder(parent: ViewGroup?): VH

    /**
     * Bind data at position into viewHolder
     *
     * @param viewHolder item view holder
     * @param position   item position
     */
    abstract fun onBindViewHolder(viewHolder: VH, position: Int)
    fun dataSetChangedListener(dataSetListener: DataSetListener?) {
        this.dataSetListener = dataSetListener
    }

    interface DataSetListener {
        fun dataSetChanged()
    }
}