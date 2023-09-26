package com.tsp.learn.view;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * date 2019/2/1
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
@TargetApi(21)
public class SimpleOutlineProvider extends ViewOutlineProvider {
    private final Rect rect = new Rect();
    private float radius = 0f;

    public SimpleOutlineProvider(float radius) {
        this.radius = radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        view.getDrawingRect(rect);
        outline.setRoundRect(rect, radius);
    }
}
