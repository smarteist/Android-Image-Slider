# Android image slider
[ ![Download](https://api.bintray.com/packages/smarteistbintray/android/androidautoimageslider/images/download.svg) ](https://bintray.com/smarteistbintray/android/androidautoimageslider/_latestVersion)[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android%20Slider-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7693)

[![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=15)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This is an amazing image slider for the Android .
 
You can easily load images with your custom layout, And there are many kinds of amazing animations you can choose.

     implementation 'com.github.smarteist:autoimageslider:1.3.2'

### New Feautures 
* Added new adapter based slider view, Provides the ability to add custom views
* Bugs fixed
* Fixed animation issues
* Indicator attributes added
* Migrated to androidx

# Demo
![](https://github.com/smarteist/android-image-slider/blob/master/gif/0.gif)
![](https://github.com/smarteist/android-image-slider/blob/master/gif/8.gif)
![](https://github.com/smarteist/android-image-slider/blob/master/gif/4.gif)
![](https://github.com/smarteist/android-image-slider/blob/master/gif/7.gif)

# Integration guide

Just put the view in the layout xml like this:

```xml
        <com.smarteist.autoimageslider.SliderView
                    android:id="@+id/imageSlider"
                    android:layout_width="match_parent"
                    android:layout_height="300dp"
                    app:sliderAnimationDuration="600"
                    app:sliderAutoCycleDirection="back_and_forth"
                    app:sliderAutoCycleEnabled="true"
                    app:sliderCircularHandlerEnabled="true"
                    app:sliderIndicatorAnimationDuration="600"
                    app:sliderIndicatorGravity="center_horizontal|bottom"
                    app:sliderIndicatorMargin="15dp"
                    app:sliderIndicatorOrientation="horizontal"
                    app:sliderIndicatorPadding="3dp"
                    app:sliderIndicatorRadius="2dp"
                    app:sliderIndicatorSelectedColor="#5A5A5A"
                    app:sliderIndicatorUnselectedColor="#FFF"
                    app:sliderScrollTimeInSec="1"
                    app:sliderStartAutoCycle="true" />
```
   
   Or you can wrap it in a cardView :
   
```xml
       <androidx.cardview.widget.CardView
               app:cardCornerRadius="6dp"
               android:layout_margin="16dp"
               android:layout_width="match_parent"
               android:layout_height="wrap_content">
       
               <com.smarteist.autoimageslider.SliderView
                           android:id="@+id/imageSlider"
                           android:layout_width="match_parent"
                           android:layout_height="300dp"
                           app:sliderAnimationDuration="600"
                           app:sliderAutoCycleDirection="back_and_forth"
                           app:sliderAutoCycleEnabled="true"
                           app:sliderCircularHandlerEnabled="true"
                           app:sliderIndicatorAnimationDuration="600"
                           app:sliderIndicatorGravity="center_horizontal|bottom"
                           app:sliderIndicatorMargin="15dp"
                           app:sliderIndicatorOrientation="horizontal"
                           app:sliderIndicatorPadding="3dp"
                           app:sliderIndicatorRadius="2dp"
                           app:sliderIndicatorSelectedColor="#5A5A5A"
                           app:sliderIndicatorUnselectedColor="#FFF"
                           app:sliderScrollTimeInSec="1"
                           app:sliderStartAutoCycle="true" />
       
       </androidx.cardview.widget.CardView>
```
     

The new version requires an adapter for the sliderView , Although its very similar to RecyclerAdapter , and it's easy for you to implement this adapter... but here is an example for adapter implementation :

```java	
public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

    private Context context;

    public SliderAdapterExample(Context context) {
        this.context = context;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        viewHolder.textViewDescription.setText("This is slider item " + position);

        switch (position) {
            case 0:
                Glide.with(viewHolder.itemView)
                        .load("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
                        .into(viewHolder.imageViewBackground);
                break;
            case 1:
                Glide.with(viewHolder.itemView)
                        .load("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260")
                        .into(viewHolder.imageViewBackground);
                break;
            case 2:
                Glide.with(viewHolder.itemView)
                        .load("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
                        .into(viewHolder.imageViewBackground);
                break;
            default:
                Glide.with(viewHolder.itemView)
                        .load("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
                        .into(viewHolder.imageViewBackground);
                break;

        }

    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return 4;
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }
}
```
    
First setAdapter to Sliderview

```java
    sliderView.setSliderAdapter(new SliderAdapterExample(context));
```
		
Then call this method if you want the slider to start flipping automatically :

```java
    sliderView.startAutoCycle();
```

Here is a more realistic and more complete example :

```java

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
    
            SliderView sliderView = findViewById(R.id.imageSlider);
    
            SliderAdapterExample adapter = new SliderAdapterExample(this);
    
            sliderView.setSliderAdapter(adapter);
    
            sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
            sliderView.setIndicatorSelectedColor(Color.WHITE);
            sliderView.setIndicatorUnselectedColor(Color.GRAY);
            sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
            sliderView.startAutoCycle();
            
        }
```

# Contribute

Suggestions and pull requests are always welcome.

# Licence

Copyright [2019] [Ali Hosseini]

   Licensed under the Apache License, Version 2.0;
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
