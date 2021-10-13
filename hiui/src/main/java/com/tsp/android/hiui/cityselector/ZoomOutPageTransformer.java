package com.tsp.android.hiui.cityselector;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

/**
 * author : shengping.tian
 * time   : 2021/10/13
 * desc   :
 * version: 1.0
 */
public class ZoomOutPageTransformer implements ViewPager.PageTransformer {

    private static final float MIN_SCALE=0.75f;

    @Override
    public void transformPage(View page, float position) {
        int pageWidth=page.getWidth();

        if(position<-1){
            page.setAlpha(0);
        }else if(position<=0){//左页面时使用默认的幻灯片转换
            page.setAlpha(1);
            page.setTranslationX(0);
            page.setScaleX(1);
            page.setScaleY(1);
        }else if(position<=1){//右页面
            page.setAlpha(1 - position);
            page.setTranslationX(pageWidth*-position);//抵消默认的幻灯片转换

            float scaleFactor=Math.max(MIN_SCALE,1-position);
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
        }else{
            page.setAlpha(0);
        }
    }

}
