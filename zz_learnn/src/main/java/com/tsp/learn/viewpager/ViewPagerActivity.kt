package com.tsp.learn.viewpager

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.tsp.learn.databinding.ActivityViewPagerBinding
import com.tsp.learn.viewpager.fragment.Fragment1
import com.tsp.learn.viewpager.fragment.Fragment2
import com.tsp.learn.viewpager.fragment.Fragment3

class ViewPagerActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ViewPagerActivity"
    }

    private lateinit var mBinding: ActivityViewPagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityViewPagerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initData()
    }

    private fun initData() {
        val fragmentList = mutableListOf(
            Fragment1(),
            Fragment2(),
            Fragment3(),
        )
        mBinding.viewPager.apply {
            adapter = MyPagerAdapter(supportFragmentManager, fragmentList)

            //监听 ViewPager 滑动状态
            addOnAdapterChangeListener(object : ViewPager.OnPageChangeListener,
                ViewPager.OnAdapterChangeListener {

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    Log.i(
                        TAG,
                        "onPageScrolled: position = $position, positionOffset = $positionOffset ,positionOffsetPixels = $positionOffsetPixels "
                    )
                }

                override fun onPageSelected(position: Int) {
                    Log.i(TAG, "onPageSelected: position = $position")
                }

                override fun onPageScrollStateChanged(state: Int) {
                    Log.i(TAG, "onPageScrollStateChanged: $state")
                }

                override fun onAdapterChanged(
                    viewPager: ViewPager,
                    oldAdapter: PagerAdapter?,
                    newAdapter: PagerAdapter?
                ) {
                }
            })
            //设置viewPager 切换动画
            setPageTransformer(false, DepthPagerTransformer())
        }

    }

    inner class DepthPagerTransformer : ViewPager.PageTransformer {

        private val MIN_SCALE = 0.75f

        override fun transformPage(page: View, position: Float) {
            val pageWidth: Int = page.width
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0f
            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                page.alpha = 1f
                page.translationX = 0f
                page.scaleX = 1f
                page.scaleY = 1f
            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                page.alpha = 1 - position
                // Counteract the default slide transition
                page.translationX = pageWidth * -position
                // Scale the page down (between MIN_SCALE and 1)
                val scaleFactor: Float = (MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position)))
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0f
            }
        }
    }

    inner class RotateDownPageTransformer : ViewPager.PageTransformer {
        val ROT_MAX = 20.0f
        var mRot = 0f
        override fun transformPage(view: View, position: Float) {
            Log.e("TAG", "$view , $position")
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.rotation = 0f
            } else if (position <= 1) {
                // a页滑动至b页 ； a页从 0.0 ~ -1 ；b页从1 ~ 0.0
                if (position < 0) {
                    mRot = ROT_MAX * position
                    view.pivotX = view.measuredWidth * 0.5f
                    view.pivotY = view.measuredHeight.toFloat()
                    view.rotation = mRot
                } else {
                    mRot = ROT_MAX * position
                    view.pivotX = view.measuredWidth * 0.5f
                    view.pivotY = view.measuredHeight.toFloat()
                    view.rotation = mRot
                }
            } else { // (1,+Infinity]
                view.rotation = 0f
            }
        }
    }
}