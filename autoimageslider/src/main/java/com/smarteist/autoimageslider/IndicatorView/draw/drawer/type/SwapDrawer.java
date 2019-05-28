package com.smarteist.autoimageslider.IndicatorView.draw.drawer.type;

import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import com.smarteist.autoimageslider.IndicatorView.animation.data.Value;
import com.smarteist.autoimageslider.IndicatorView.animation.data.type.SwapAnimationValue;
import com.smarteist.autoimageslider.IndicatorView.draw.data.Indicator;
import com.smarteist.autoimageslider.IndicatorView.draw.data.Orientation;

public class SwapDrawer extends BaseDrawer {

    public SwapDrawer(@NonNull Paint paint, @NonNull Indicator indicator) {
        super(paint, indicator);
    }

    public void draw(
            @NonNull Canvas canvas,
            @NonNull Value value,
            int position,
            int coordinateX,
            int coordinateY) {

        if (!(value instanceof SwapAnimationValue)) {
            return;
        }

        SwapAnimationValue v = (SwapAnimationValue) value;
        int selectedColor = indicator.getSelectedColor();
        int unselectedColor = indicator.getUnselectedColor();
        int radius = indicator.getRadius();

        int selectedPosition = indicator.getSelectedPosition();
        int selectingPosition = indicator.getSelectingPosition();
        int lastSelectedPosition = indicator.getLastSelectedPosition();

        int coordinate = v.getCoordinate();
        int color = unselectedColor;

        if (indicator.isInteractiveAnimation()) {
            if (position == selectingPosition) {
                coordinate = v.getCoordinate();
                color = selectedColor;

            } else if (position == selectedPosition) {
                coordinate = v.getCoordinateReverse();
                color = unselectedColor;
            }

        } else {
            if (position == lastSelectedPosition) {
                coordinate = v.getCoordinate();
                color = selectedColor;

            } else if (position == selectedPosition) {
                coordinate = v.getCoordinateReverse();
                color = unselectedColor;
            }
        }

        paint.setColor(color);
        if (indicator.getOrientation() == Orientation.HORIZONTAL) {
            canvas.drawCircle(coordinate, coordinateY, radius, paint);
        } else {
            canvas.drawCircle(coordinateX, coordinate, radius, paint);
        }
    }
}
