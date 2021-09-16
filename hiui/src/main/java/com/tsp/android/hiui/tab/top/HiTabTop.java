package com.tsp.android.hiui.tab.top;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tsp.android.hiui.R;
import com.tsp.android.hiui.tab.bottom.HiTabBottomInfo;
import com.tsp.android.hiui.tab.common.IHiTab;

/**
 * author : shengping.tian
 * time   : 2021/07/27
 * desc   : top 导航栏单个布局
 * version: 1.0
 */
public class HiTabTop extends RelativeLayout implements IHiTab<HiTabTopInfo<?>> {

    private HiTabTopInfo<?> tabTopInfo;
    private ImageView tabImageView;
    private TextView tabNameView;
    //指示器
    private View indicator;

    public HiTabTop(Context context) {
        this(context, null);
    }

    public HiTabTop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HiTabTop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HiTabTopInfo<?> getTabTopInfo() {
        return tabTopInfo;
    }

    public ImageView getTabImageView() {
        return tabImageView;
    }

    public TextView getTabNameView() {
        return tabNameView;
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.hit_tab_top, this);
        tabImageView = view.findViewById(R.id.iv_image);
        tabNameView = view.findViewById(R.id.tv_name);
        indicator = view.findViewById(R.id.tab_top_indicator);
    }

    @Override
    public void setHiTabInfo(@NonNull HiTabTopInfo<?> data) {
        this.tabTopInfo = data;
        inflateInfo(false,true);
    }

    @Override
    public void resetHeight(int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = height;
        setLayoutParams(layoutParams);
        getTabNameView().setVisibility(View.GONE);
    }

    @Override
    public void onTabSelectedChange(int index, @Nullable HiTabTopInfo<?> prevInfo, @NonNull HiTabTopInfo<?> nextInfo) {
        if (prevInfo != tabTopInfo && nextInfo != tabTopInfo || prevInfo == nextInfo) {
            return;
        }
        inflateInfo(prevInfo != tabTopInfo, false);
    }

    /**
     * @param selected 是否选中
     * @param init     是否第一次初始化
     */
    private void inflateInfo(boolean selected, boolean init) {
        if (tabTopInfo.tabType == HiTabTopInfo.TabType.TXT) {
            if (init) {
                tabNameView.setVisibility(VISIBLE);
                tabImageView.setVisibility(GONE);
                if (!TextUtils.isEmpty(tabTopInfo.name)) {
                    tabNameView.setText(tabTopInfo.name);
                }
            }
            if (selected) {
                indicator.setVisibility(VISIBLE);
                tabNameView.setTextColor(getTextColor(tabTopInfo.tintColor));
            } else {
                indicator.setVisibility(GONE);
                tabNameView.setTextColor(getTextColor(tabTopInfo.defaultColor));
            }
        }else if (tabTopInfo.tabType == HiTabTopInfo.TabType.BITMAP) {
            if (init) {
                tabImageView.setVisibility(VISIBLE);
                tabNameView.setVisibility(GONE);
            }
            if (selected) {
                indicator.setVisibility(VISIBLE);
                tabImageView.setImageBitmap(tabTopInfo.selectedBitmap);
            } else {
                indicator.setVisibility(GONE);
                tabImageView.setImageBitmap(tabTopInfo.defaultBitmap);
            }
        }
    }


    @ColorInt
    private int getTextColor(Object color) {
        if (color instanceof String) {
            return Color.parseColor((String) color);
        } else {
            return (int) color;
        }
    }
}
