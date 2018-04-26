It's an Android animated and automatic scrolling Image Slider Library with the functionality of adding an image with its optional description, it also has a View Pager Indicator and built in listeners.

You can easily load images from an internet URL, drawable, or file. And there are many kinds of amazing animations you can choose...

<img src="https://raw.githubusercontent.com/smarteist/android-image-slider/master/ezgif-4-1091d2bf4b.gif" alt="Auto Image Slider" width="300px" style="max-width:100%;">

To add pageindicatorview to your project, first make sure in root build.gradle you have specified the following repository:

    repositories { 
    mavenCentral()
    }
    
Once you make sure you have maven repository in your project, all you need to do is to add the following line in dependencies section of your project build.gradle :

    implementation 'com.github.smarteist:autoimageslider:1.0.0'

Usage Sample :

In anctivity view xml file :

    <imageslider.smarteist.com.autoimageslider.SliderLayout
        android:layout_width="match_parent"
        android:layout_height="300dp"
        android:layout_alignParentTop="true"
        android:id="@+id/imageSlider"/>
        
Here is simple example, how to change Handle Slider Layout.
first you need to initialize Slider layout :

    SliderLayout sliderLayout = findViewById(R.id.imageSlider);
    sliderLayout.setIndicatotAnimation(SliderLayout.Animations.DROP); //set indicator animation by using SliderLayout.Animations.--> : **NONE** or **WORM** or  **THIN_WORM** or **COLOR** or **DROP** or **FILL** or **SCALE** or **SCALE_DOWN** or **SLIDE** and **SWAP**!!
    sliderLayout.setScrollTimeInSec(3); //set scroll delay in seconds :

Now you should add some slider views and handle selected slider on your own programatically like this :

        private void setSliderViews() {

        for (int i = 0; i <= 3; i++) {

            SliderView sliderView = new SliderView(this);

            switch (i) {
                case 0:
                    sliderView.setImageDrawable(R.drawable.img1);
                    break;
                case 1:
                    sliderView.setImageDrawable(R.drawable.img2);
                    break;
                case 2:
                    sliderView.setImageDrawable(R.drawable.img3);
                    break;
                case 3:
                    sliderView.setImageDrawable(R.drawable.img4);
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            sliderView.setDescription("Description " + (i + 1));
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


