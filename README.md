# Android image slider
This is an amazing image slider for the Android .
 
You can easily load images from an internet URL, drawable, or file. And there are many kinds of amazing animations you can choose.

     implementation 'com.github.smarteist:autoimageslider:1.2.0'
     implementation 'com.github.bumptech.glide:glide:4.7.1'

### New Feautures 
Ability to change slide animation by using 
		sliderLayout.setSliderTransformAnimation();
Some issues fixed.
Ability to implement a custom sliderView with exteding from class "SliderView"
and change it any way you want.

# Demo
![](https://github.com/smarteist/android-image-slider/blob/master/1.gif)
![](https://github.com/smarteist/android-image-slider/blob/master/2.gif)
![](https://github.com/smarteist/android-image-slider/blob/master/4.gif)
![](https://github.com/smarteist/android-image-slider/blob/master/7.gif)

# Integration guide

Just put the view in the layout xml like this:

        <com.smarteist.autoimageslider.SliderLayout
        android:layout_width="match_parent"
        android:layout_height="300dp"
        android:layout_alignParentTop="true"
        android:id="@+id/imageSlider"/>
        
        
And implement the slider with your own programming
Here is an example of the implementation of this library in java :


         	SliderLayout sliderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.FILL); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(1); //set scroll delay in seconds :

        setSliderViews();
    }

    private void setSliderViews() {

        for (int i = 0; i <= 3; i++) {

            SliderView sliderView = new SliderView(this);

            switch (i) {
                case 0:
                    sliderView.setImageUrl("https://images.pexels.com/photos/547114/pexels-photo-547114.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
                case 1:
                    sliderView.setImageUrl("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
                case 2:
                    sliderView.setImageUrl("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260");
                    break;
                case 3:
                    sliderView.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            sliderView.setDescription("setDescription " + (i + 1));
            final int finalI = i;
            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                    Toast.makeText(MainActivity.this, "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show();
                }
            });

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);
        }
	}


