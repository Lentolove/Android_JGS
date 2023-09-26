package com.tsp.learn.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.tsp.learn.R;

/**
 * @author leoyuu
 * date 2018/1/2
 * email july.oloy@qq.com
 */

public class CircleFrameLayout extends FrameLayout {

    private final Paint clipPaint = new Paint();
    private Path path = new Path();
    private float radius = 10;
    private boolean needNewPath = true;

    public CircleFrameLayout(Context context) {
        this(context, null);
    }

    public CircleFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        clipPaint.setAntiAlias(true);
        clipPaint.setColor(Color.WHITE);
        if (useOutlineProvider()) {
            setOutlineProvider(new SimpleOutlineProvider(radius));
            setClipToOutline(true);
        }
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (useOutlineProvider()) {
            super.dispatchDraw(canvas);
        } else {
            clipCorner(canvas);
        }
    }

    private void clipCorner(Canvas canvas) {
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        canvas.drawPath(getCurPath(), clipPaint);
        canvas.restoreToCount(saveCount);
        clipPaint.setXfermode(null);
    }

    private Path getCurPath(){
        if (needNewPath){
            int width = getWidth();
            int height = getHeight();
            Path path = new Path();
            path.moveTo(0, radius);
            path.arcTo(new RectF(0, 0, radius * 2, radius * 2), -180, 90);
            path.lineTo(width - radius, 0);
            path.arcTo(new RectF(width - 2 * radius, 0, width, radius * 2), -90, 90);
            path.lineTo(width, height - radius);
            path.arcTo(new RectF(width - 2 * radius, height - 2 * radius, width, height), 0, 90);
            path.lineTo(radius, height);
            path.arcTo(new RectF(0, height - 2 * radius, radius * 2, height), 90, 90);
            path.close();
            this.path = path;
            needNewPath = false;
        }
        return path;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        needNewPath = true;
        if (useOutlineProvider()) {
            invalidateOutline();
        }
    }

    private boolean useOutlineProvider() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
