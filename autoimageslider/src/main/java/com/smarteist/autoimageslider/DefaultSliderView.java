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

public class DefaultSliderView extends SliderView {

    private String description;
    private int descriptionTextColor = Color.WHITE;
    private float descriptionTextSize = 1;

    @Override
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
        bindViewData(v, autoSliderImage);
        return v;
    }

    @Override
    void bindViewData(View v, ImageView autoSliderImage) {
        final DefaultSliderView con = this;
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
                Glide.with(context).asDrawable().load(imageUrl).into(autoSliderImage);
            }
            if (imageRes != 0) {
                Glide.with(context).asDrawable().load(imageRes).into(autoSliderImage);
            }
        } catch (Exception exception) {
            Log.d("Exception", exception.getMessage());
        }
    }

    public DefaultSliderView(Context context) {
        super(context);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDescriptionTextColor(int descriptionTextColor) {
        this.descriptionTextColor = descriptionTextColor;
    }

    public void setDescriptionTextSize(float descriptionTextSize) {
        this.descriptionTextSize = descriptionTextSize;
    }

}
