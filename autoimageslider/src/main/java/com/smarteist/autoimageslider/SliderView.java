package com.smarteist.autoimageslider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SliderView {

    private OnSliderClickListener onSliderClickListener;

    private String description;
    private int descriptionTextColor = Color.WHITE;
    private float descriptionTextSize = 1;

    @DrawableRes
    private int imageRes = 0;

    private String imageUrl;

    private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_CROP;
    private Context context;

    public SliderView(Context context) {
        this.context = context;
    }

    private String getDescription() {
        return description;
    }

    public SliderView setDescription(String description) {
        this.description = description;
        return this;
    }

    public SliderView setDescriptionTextColor (int descriptionTextColor) {
        this.descriptionTextColor = descriptionTextColor;
        return this;
    }

    public SliderView setDescriptionTextSize (float descriptionTextSize) {
        this.descriptionTextSize = descriptionTextSize;
        return this;
    }

    public int getImageRes() {
        return imageRes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public SliderView setImageUrl(String imageUrl) {
        if (imageRes != 0) {
            throw new IllegalStateException("Can't set multiple images");
        }
        this.imageUrl = imageUrl;
        return this;
    }

    public SliderView setImageByte(byte[] imageByte) {
        ContextWrapper wrapper = new ContextWrapper(context);
        File file = new File(wrapper.getCacheDir().getAbsolutePath(),"Cached"+System.currentTimeMillis()+".jpeg");
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0,imageByte.length);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.imageUrl = String.valueOf(Uri.fromFile(file));
        return this;
    }

    public SliderView setImageDrawable(int imageDrawable) {
        if (!TextUtils.isEmpty(imageUrl)) {
            throw new IllegalStateException("Can't set multiple images");
        }
        this.imageRes = imageDrawable;
        return this;
    }

    private ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    public SliderView setImageScaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
        return this;
    }

    public View getView() {
        @SuppressLint("InflateParams")
        View v = LayoutInflater.from(context).inflate(R.layout.image_slider_layout_item, null, true);
        ImageView autoSliderImage = v.findViewById(R.id.iv_auto_image_slider);
        TextView tv_description = v.findViewById(R.id.tv_auto_image_slider);
        tv_description.getBackground();
        if (descriptionTextSize != 1) {
            tv_description.setTextSize(descriptionTextSize);
        }
        tv_description.setTextColor(descriptionTextColor);
        tv_description.setText(getDescription());
        bindData(v, autoSliderImage);
        return v;
    }

    public SliderView setOnSliderClickListener(OnSliderClickListener l) {
        onSliderClickListener = l;
        return this;
    }

    private void bindData(View v, ImageView autoSliderImage) {
        final SliderView con = this;
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSliderClickListener != null) {
                    onSliderClickListener.onSliderClick(con);
                }
            }
        });
        try {
            autoSliderImage.setScaleType(getScaleType());
            if (imageUrl != null) {
                Glide.with(context).load(imageUrl).into(autoSliderImage);
            }
            if (imageRes != 0){
                Glide.with(context).load(imageRes).into(autoSliderImage);
            }
        } catch (Exception exception) {
            Log.d("Exception", exception.getMessage());
        }
    }

    public interface OnSliderClickListener {
        void onSliderClick(SliderView sliderView);
    }
}
