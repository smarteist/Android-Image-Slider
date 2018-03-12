package imageslider.smarteist.com.imgeslider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import imageslider.smarteist.com.autoimageslider.SliderLayout;
import imageslider.smarteist.com.autoimageslider.SliderView;

public class MainActivity extends AppCompatActivity {

    SliderLayout sliderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatotAnimation(SliderLayout.Animations.DROP); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(3); //set scroll delay in seconds :

        setSliderViews();
    }

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
}
