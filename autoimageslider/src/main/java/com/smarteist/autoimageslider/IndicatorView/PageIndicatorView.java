package com.smarteist.autoimageslider.IndicatorView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.TextUtilsCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.smarteist.autoimageslider.IndicatorView.animation.type.AnimationType;
import com.smarteist.autoimageslider.IndicatorView.animation.type.ScaleAnimation;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.IndicatorView.draw.data.Indicator;
import com.smarteist.autoimageslider.IndicatorView.draw.data.Orientation;
import com.smarteist.autoimageslider.IndicatorView.draw.data.PositionSavedState;
import com.smarteist.autoimageslider.IndicatorView.draw.data.RtlMode;
import com.smarteist.autoimageslider.IndicatorView.utils.CoordinatesUtils;
import com.smarteist.autoimageslider.IndicatorView.utils.DensityUtils;
import com.smarteist.autoimageslider.IndicatorView.utils.IdUtils;

public class PageIndicatorView extends View implements ViewPager.OnPageChangeListener, IndicatorManager.Listener, ViewPager.OnAdapterChangeListener {

    private IndicatorManager manager;
    private DataSetObserver setObserver;
    private ViewPager viewPager;
    private boolean isInteractionEnabled;

    public PageIndicatorView(Context context) {
        super(context);
        init(null);
    }

    public PageIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        findViewPager(getParent());
    }

    @Override
    protected void onDetachedFromWindow() {
        unRegisterSetObserver();
        super.onDetachedFromWindow();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Indicator indicator = manager.indicator();
        PositionSavedState positionSavedState = new PositionSavedState(super.onSaveInstanceState());
        positionSavedState.setSelectedPosition(indicator.getSelectedPosition());
        positionSavedState.setSelectingPosition(indicator.getSelectingPosition());
        positionSavedState.setLastSelectedPosition(indicator.getLastSelectedPosition());

        return positionSavedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof PositionSavedState) {
            Indicator indicator = manager.indicator();
            PositionSavedState positionSavedState = (PositionSavedState) state;
            indicator.setSelectedPosition(positionSavedState.getSelectedPosition());
            indicator.setSelectingPosition(positionSavedState.getSelectingPosition());
            indicator.setLastSelectedPosition(positionSavedState.getLastSelectedPosition());
            super.onRestoreInstanceState(positionSavedState.getSuperState());

        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Pair<Integer, Integer> pair = manager.drawer().measureViewSize(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(pair.first, pair.second);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        manager.drawer().draw(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        manager.drawer().touch(event);
        return true;
    }

    @Override
    public void onIndicatorUpdated() {
        invalidate();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        onPageScroll(position, positionOffset);
    }

    @Override
    public void onPageSelected(int position) {
        onPageSelect(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
		if (state == ViewPager.SCROLL_STATE_IDLE) {
			manager.indicator().setInteractiveAnimation(isInteractionEnabled);
		}
	}

    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
        updateState();
    }


    public void setCount(int count) {
        if (count >= 0 && manager.indicator().getCount() != count) {
            manager.indicator().setCount(count);
            updateVisibility();
            requestLayout();
        }
    }


    public int getCount() {
        return manager.indicator().getCount();
    }


    public void setDynamicCount(boolean dynamicCount) {
        manager.indicator().setDynamicCount(dynamicCount);

        if (dynamicCount) {
            registerSetObserver();
        } else {
            unRegisterSetObserver();
        }
    }


    public void setRadius(int radiusDp) {
        if (radiusDp < 0) {
            radiusDp = 0;
        }

        int radiusPx = DensityUtils.dpToPx(radiusDp);
        manager.indicator().setRadius(radiusPx);
        invalidate();
    }


    public void setRadius(float radiusPx) {
        if (radiusPx < 0) {
            radiusPx = 0;
        }

        manager.indicator().setRadius((int) radiusPx);
        invalidate();
    }


    public int getRadius() {
        return manager.indicator().getRadius();
    }


    public void setPadding(int paddingDp) {
        if (paddingDp < 0) {
            paddingDp = 0;
        }

        int paddingPx = DensityUtils.dpToPx(paddingDp);
        manager.indicator().setPadding(paddingPx);
        invalidate();
    }


    public void setPadding(float paddingPx) {
        if (paddingPx < 0) {
            paddingPx = 0;
        }

        manager.indicator().setPadding((int) paddingPx);
        invalidate();
    }


    public int getPadding() {
        return manager.indicator().getPadding();
    }


    public void setScaleFactor(float factor) {
        if (factor > ScaleAnimation.MAX_SCALE_FACTOR) {
            factor = ScaleAnimation.MAX_SCALE_FACTOR;

        } else if (factor < ScaleAnimation.MIN_SCALE_FACTOR) {
            factor = ScaleAnimation.MIN_SCALE_FACTOR;
        }

        manager.indicator().setScaleFactor(factor);
    }


    public float getScaleFactor() {
        return manager.indicator().getScaleFactor();
    }


    public void setStrokeWidth(float strokePx) {
        int radiusPx = manager.indicator().getRadius();

        if (strokePx < 0) {
            strokePx = 0;

        } else if (strokePx > radiusPx) {
            strokePx = radiusPx;
        }

        manager.indicator().setStroke((int) strokePx);
        invalidate();
    }



    public void setStrokeWidth(int strokeDp) {
        int strokePx = DensityUtils.dpToPx(strokeDp);
        int radiusPx = manager.indicator().getRadius();

        if (strokePx < 0) {
            strokePx = 0;

        } else if (strokePx > radiusPx) {
            strokePx = radiusPx;
        }

        manager.indicator().setStroke(strokePx);
        invalidate();
    }


    public int getStrokeWidth() {
        return manager.indicator().getStroke();
    }


    public void setSelectedColor(int color) {
        manager.indicator().setSelectedColor(color);
        invalidate();
    }


    public int getSelectedColor() {
        return manager.indicator().getSelectedColor();
    }


    public void setUnselectedColor(int color) {
        manager.indicator().setUnselectedColor(color);
        invalidate();
    }


    public int getUnselectedColor() {
        return manager.indicator().getUnselectedColor();
    }


    public void setAutoVisibility(boolean autoVisibility) {
        if (!autoVisibility) {
            setVisibility(VISIBLE);
        }

        manager.indicator().setAutoVisibility(autoVisibility);
        updateVisibility();
    }


    public void setOrientation(@Nullable Orientation orientation) {
        if (orientation != null) {
            manager.indicator().setOrientation(orientation);
            requestLayout();
        }
    }


    public void setAnimationDuration(long duration) {
        manager.indicator().setAnimationDuration(duration);
    }


    public long getAnimationDuration() {
        return manager.indicator().getAnimationDuration();
    }


    public void setAnimationType(@Nullable AnimationType type) {
        manager.onValueUpdated(null);

        if (type != null) {
            manager.indicator().setAnimationType(type);
        } else {
            manager.indicator().setAnimationType(AnimationType.NONE);
        }
        invalidate();
    }


    public void setInteractiveAnimation(boolean isInteractive) {
        manager.indicator().setInteractiveAnimation(isInteractive);
        this.isInteractionEnabled = isInteractive;
    }


    public void setViewPager(@Nullable ViewPager pager) {
        releaseViewPager();
        if (pager == null) {
            return;
        }

        viewPager = pager;
        viewPager.addOnPageChangeListener(this);
        viewPager.addOnAdapterChangeListener(this);
        manager.indicator().setViewPagerId(viewPager.getId());

        setDynamicCount(manager.indicator().isDynamicCount());
        updateState();
    }


    public void releaseViewPager() {
        if (viewPager != null) {
            viewPager.removeOnPageChangeListener(this);
            viewPager = null;
        }
    }


    public void setRtlMode(@Nullable RtlMode mode) {
        Indicator indicator = manager.indicator();
        if (mode == null) {
            indicator.setRtlMode(RtlMode.Off);
        } else {
            indicator.setRtlMode(mode);
        }

        if (viewPager == null) {
            return;
        }

        int selectedPosition = indicator.getSelectedPosition();
        int position = selectedPosition;

        if (isRtl()) {
            position = (indicator.getCount() - 1) - selectedPosition;

        } else if (viewPager != null) {
            position = viewPager.getCurrentItem();
        }

        indicator.setLastSelectedPosition(position);
        indicator.setSelectingPosition(position);
        indicator.setSelectedPosition(position);
        invalidate();
    }


    public int getSelection() {
        return manager.indicator().getSelectedPosition();
    }


    public void setSelection(int position) {
        Indicator indicator = manager.indicator();
        position = adjustPosition(position);

        if (position == indicator.getSelectedPosition() || position == indicator.getSelectingPosition()) {
            return;
        }

        indicator.setInteractiveAnimation(false);
        indicator.setLastSelectedPosition(indicator.getSelectedPosition());
        indicator.setSelectingPosition(position);
        indicator.setSelectedPosition(position);
        manager.animate().basic();
    }

    public void setSelected(int position) {
        Indicator indicator = manager.indicator();
        AnimationType animationType = indicator.getAnimationType();
        indicator.setAnimationType(AnimationType.NONE);

        setSelection(position);
        indicator.setAnimationType(animationType);
    }


    public void clearSelection() {
        Indicator indicator = manager.indicator();
        indicator.setInteractiveAnimation(false);
        indicator.setLastSelectedPosition(Indicator.COUNT_NONE);
        indicator.setSelectingPosition(Indicator.COUNT_NONE);
        indicator.setSelectedPosition(Indicator.COUNT_NONE);
        manager.animate().basic();
    }


    public void setProgress(int selectingPosition, float progress) {
        Indicator indicator = manager.indicator();
        if (!indicator.isInteractiveAnimation()) {
            return;
        }

        int count = indicator.getCount();
        if (count <= 0 || selectingPosition < 0) {
            selectingPosition = 0;

        } else if (selectingPosition > count - 1) {
            selectingPosition = count - 1;
        }

        if (progress < 0) {
            progress = 0;

        } else if (progress > 1) {
            progress = 1;
        }

        if (progress == 1) {
            indicator.setLastSelectedPosition(indicator.getSelectedPosition());
            indicator.setSelectedPosition(selectingPosition);
        }

        indicator.setSelectingPosition(selectingPosition);
        manager.animate().interactive(progress);
    }

    public void setClickListener(@Nullable DrawController.ClickListener listener) {
        manager.drawer().setClickListener(listener);
    }

    private void init(@Nullable AttributeSet attrs) {
        setupId();
        initIndicatorManager(attrs);
    }

    private void setupId() {
        if (getId() == NO_ID) {
            setId(IdUtils.generateViewId());
        }
    }

    private void initIndicatorManager(@Nullable AttributeSet attrs) {
        manager = new IndicatorManager(this);
        manager.drawer().initAttributes(getContext(), attrs);

        Indicator indicator = manager.indicator();
        indicator.setPaddingLeft(getPaddingLeft());
        indicator.setPaddingTop(getPaddingTop());
        indicator.setPaddingRight(getPaddingRight());
        indicator.setPaddingBottom(getPaddingBottom());
        isInteractionEnabled = indicator.isInteractiveAnimation();
    }

    private void registerSetObserver() {
        if (setObserver != null || viewPager == null || viewPager.getAdapter() == null) {
            return;
        }

        setObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                updateState();
            }
        };

        try {
            viewPager.getAdapter().registerDataSetObserver(setObserver);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void unRegisterSetObserver() {
        if (setObserver == null || viewPager == null || viewPager.getAdapter() == null) {
            return;
        }

        try {
            viewPager.getAdapter().unregisterDataSetObserver(setObserver);
            setObserver = null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void updateState() {
        if (viewPager == null || viewPager.getAdapter() == null) {
            return;
        }

        int count = viewPager.getAdapter().getCount();
        int selectedPos = isRtl() ? (count - 1) - viewPager.getCurrentItem() : viewPager.getCurrentItem();

        manager.indicator().setSelectedPosition(selectedPos);
        manager.indicator().setSelectingPosition(selectedPos);
        manager.indicator().setLastSelectedPosition(selectedPos);
        manager.indicator().setCount(count);
        manager.animate().end();

        updateVisibility();
        requestLayout();
    }

    private void updateVisibility() {
        if (!manager.indicator().isAutoVisibility()) {
            return;
        }

        int count = manager.indicator().getCount();
        int visibility = getVisibility();

        if (visibility != VISIBLE && count > Indicator.MIN_COUNT) {
            setVisibility(VISIBLE);

        } else if (visibility != INVISIBLE && count <= Indicator.MIN_COUNT) {
            setVisibility(View.INVISIBLE);
        }
    }

    private void onPageSelect(int position) {
        Indicator indicator = manager.indicator();
        boolean canSelectIndicator = isViewMeasured();
        int count = indicator.getCount();

        if (canSelectIndicator) {
            if (isRtl()) {
                position = (count - 1) - position;
            }

            setSelection(position);
        }
    }

	private void onPageScroll(int position, float positionOffset) {
        Indicator indicator = manager.indicator();
        AnimationType animationType = indicator.getAnimationType();
        boolean interactiveAnimation = indicator.isInteractiveAnimation();
        boolean canSelectIndicator = isViewMeasured() && interactiveAnimation && animationType != AnimationType.NONE;

        if (!canSelectIndicator) {
            return;
        }

        Pair<Integer, Float> progressPair = CoordinatesUtils.getProgress(indicator, position, positionOffset, isRtl());
        int selectingPosition = progressPair.first;
        float selectingProgress = progressPair.second;
        setProgress(selectingPosition, selectingProgress);
    }

    private boolean isRtl() {
        switch (manager.indicator().getRtlMode()) {
            case On:
                return true;

            case Off:
                return false;

            case Auto:
                return TextUtilsCompat.getLayoutDirectionFromLocale(getContext().getResources().getConfiguration().locale) == ViewCompat.LAYOUT_DIRECTION_RTL;
        }

        return false;
    }

    private boolean isViewMeasured() {
        return getMeasuredHeight() != 0 || getMeasuredWidth() != 0;
    }

    private void findViewPager(@Nullable ViewParent viewParent) {
        boolean isValidParent = viewParent != null &&
                viewParent instanceof ViewGroup &&
                ((ViewGroup) viewParent).getChildCount() > 0;

        if (!isValidParent) {
            return;
        }

        int viewPagerId = manager.indicator().getViewPagerId();
        ViewPager viewPager = findViewPager((ViewGroup) viewParent, viewPagerId);

        if (viewPager != null) {
            setViewPager(viewPager);
        } else {
            findViewPager(viewParent.getParent());
        }
    }

    @Nullable
    private ViewPager findViewPager(@NonNull ViewGroup viewGroup, int id) {
        if (viewGroup.getChildCount() <= 0) {
            return null;
        }

        View view = viewGroup.findViewById(id);
        if (view != null && view instanceof ViewPager) {
            return (ViewPager) view;
        } else {
            return null;
        }
    }

    private int adjustPosition(int position){
        Indicator indicator = manager.indicator();
        int count = indicator.getCount();
        int lastPosition = count - 1;

        if (position < 0) {
            position = 0;

        } else if (position > lastPosition) {
            position = lastPosition;
        }

        return position;
    }
}
