package com.smarteist.autoimageslider

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController
import com.smarteist.autoimageslider.Model.SliderItem


class MainActivity : AppCompatActivity() {
    lateinit var sliderView: SliderView
    private lateinit var adapter: SliderAdapterExample

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sliderView = findViewById(R.id.imageSlider)
        adapter = SliderAdapterExample(this)
        sliderView.setSliderAdapter(adapter)
        sliderView.setIndicatorAnimationType(IndicatorAnimationType.WORM) //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION)
        sliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH

        sliderView.autoCycleEnabled = true
        sliderView.startAutoCycle()
        sliderView.setOnIndicatorClickListener(object : DrawController.ClickListener {
            override fun onIndicatorClicked(position: Int) {
                Log.i("GGG", "onIndicatorClicked: " + sliderView.currentPagePosition)
            }
        })
    }

    fun renewItems(view: View?) {
        val sliderItemList: MutableList<SliderItem> = ArrayList()
        //dummy data
        for (i in 0..4) {
            val sliderItem = SliderItem(
                description = "Slider Item $i",
                imageUrl = if (i % 2 == 0) {
                    "https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
                } else {
                    "https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260"
                }
            )
            sliderItemList.add(sliderItem)
        }
        adapter.renewItems(sliderItemList)
    }

    fun removeLastItem(view: View?) {
        if (adapter.count - 1 >= 0) adapter.deleteItem(adapter.count - 1)
    }

    fun addNewItem(view: View?) {
        val sliderItem = SliderItem(
            description = "Slider Item Added Manually",
            imageUrl = "https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
        )
        adapter.addItem(sliderItem)
    }
}