# Android image slider
This is an amazing image slider for the Android .
 
You can easily load images from an internet URL, drawable, or file. And there are many kinds of amazing animations you can choose.

     implementation 'com.github.smarteist:autoimageslider:1.3.0'

### New Feautures 
* Added new adapter based slider view, Provides the ability to add custom views
* Bugs fixed
* Migrated to androidx

# Demo
![](https://github.com/smarteist/android-image-slider/blob/master/1.gif)
![](https://github.com/smarteist/android-image-slider/blob/master/2.gif)
![](https://github.com/smarteist/android-image-slider/blob/master/4.gif)
![](https://github.com/smarteist/android-image-slider/blob/master/7.gif)

# Integration guide

Just put the view in the layout xml like this:

```xml
        <com.smarteist.autoimageslider.SliderView
        android:layout_width="match_parent"
        android:layout_height="300dp"
        android:layout_alignParentTop="true"
        android:id="@+id/imageSlider"/>
```
   
   Or you can wrap it in a cardView :
   
```xml
       <androidx.cardview.widget.CardView
               app:cardCornerRadius="6dp"
               android:layout_margin="16dp"
               android:layout_width="match_parent"
               android:layout_height="wrap_content">
       
               <com.smarteist.autoimageslider.SliderView
                   android:layout_width="match_parent"
                   android:layout_height="300dp"
                   android:layout_alignParentTop="true"
                   android:id="@+id/imageSlider"/>
       
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

        sliderView.setSliderAdapter(new SliderAdapterExample(this));

        sliderView.setIndicatorAnimation(IndicatorAnimations.SWAP); //set indicator animation by using 	  SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.setScrollTimeInSec(2); //set scroll delay in seconds :
        sliderView.startAutoCycle();
    }
```

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
