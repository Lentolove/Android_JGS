package com.tsp.learn.font;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class FontStyleUtil {

    private static volatile FontStyleUtil fontStyleUtil = new FontStyleUtil();
    private Typeface typeface;

    public static FontStyleUtil get() {
        return fontStyleUtil;
    }

    public void setRobotoMediumFontStyle(TextView textView) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/Roboto-Medium-12.ttf");
        }
        textView.setTypeface(typeface);
    }

    public void setBananaPieMediumFontStyle(TextView textView) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/BananaPie-Medium.otf");
        }
        textView.setTypeface(typeface);
    }

    public void setBananaPieRegularFontStyle(TextView textView) {
        Typeface  typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/BananaPie-Regular.otf");

        textView.setTypeface(typeface);
    }

    /**
     * 设置 BananaPie 粗体
     */
    public void setBananaBoldFontStyle(TextView textView) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/BananaPie-Medium.otf");
        }
        textView.setTypeface(typeface);
        textView.getPaint().setFakeBoldText(true);
    }


    public void setRobotoFontStyle(TextView textView) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        }
        textView.setTypeface(typeface);
    }

    public Typeface getTypeface(Context context) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium-12.ttf");
        }
        return typeface;
    }

    public Typeface getBananaPieMediumFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/BananaPie-Medium.otf");
    }
}
