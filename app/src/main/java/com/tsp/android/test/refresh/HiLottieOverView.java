package com.tsp.android.test.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import com.airbnb.lottie.LottieAnimationView;
import com.tsp.android.hiui.refresh.HiOverView;
import com.tsp.android.jgs.R;


public class HiLottieOverView extends HiOverView {

    private LottieAnimationView pullAnimationView;

    public HiLottieOverView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HiLottieOverView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HiLottieOverView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.lottie_overview, this, true);
        pullAnimationView = findViewById(R.id.pull_animation);
        pullAnimationView.setAnimation("loading_wave.json");
    }

    @Override
    protected void onScroll(int scrollY, int pullRefreshHeight) {
    }

    @Override
    public void onVisible() {
    }

    @Override
    public void onOver() {
    }

    @Override
    public void onRefresh() {
        pullAnimationView.setSpeed(2);
        pullAnimationView.playAnimation();
    }

    @Override
    public void onFinish() {
        pullAnimationView.setProgress(0f);
        pullAnimationView.cancelAnimation();
    }

}