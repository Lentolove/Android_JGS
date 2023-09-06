package com.tsp.learn.anim

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tsp.learn.databinding.AnimTestAcitivtyBinding

/**
 * author: tsp
 * Date: 2023/6/19 19:50
 * Des:
 */
class AnimActivity : AppCompatActivity() {

    private val TAG = "tsp-->"

    private lateinit var mBinding: AnimTestAcitivtyBinding

    private val myHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = AnimTestAcitivtyBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initEvent()
    }

    private fun initEvent() {
        mBinding.llAddAccount.setOnClickListener {
            val objectAnimation =
                ObjectAnimator.ofFloat(mBinding.llAddAccount, "translationX", 0f, 200f)
            objectAnimation.start()
        }
        mBinding.print.setOnClickListener {
            Log.d(TAG, "tranX = : ${mBinding.llAddAccount.translationX}")
        }
        mBinding.alphaAnim.setOnClickListener {
            val objectAnimation = ObjectAnimator.ofFloat(mBinding.alphaAnim, "alpha", 1f, 0f, 1f)
            objectAnimation.duration = 3000
            objectAnimation.start()
        }
        mBinding.scaleAnim.setOnClickListener {
            val objectAnimation = ObjectAnimator.ofFloat(mBinding.scaleAnim, "scaleX", 1f, 2f)
            // ValueAnimator.REVERSE 耗时两秒
            objectAnimation.duration = 3000
            objectAnimation.repeatCount = 1000

            objectAnimation.addListener(object : SimpleListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    Log.d(TAG, "onAnimationEnd ====")
                }

                override fun onAnimationStart(animation: Animator?) {
                    Log.d(TAG, "onAnimationStart ====")
                }
            })
            /**
             * 调用 objectAnimation.cancel 会触发 onAnimationEnd 回调
             * 1.如何避免改回调？ objectAnimation.removeAllListeners()
             */
            myHandler.postDelayed({
                objectAnimation.removeAllListeners()
                objectAnimation.cancel()
            }, 2000)
            objectAnimation.start()
        }


        //2. AnimatorSet 简单实用
        val aAnimator = ObjectAnimator.ofInt(1)
        aAnimator.addListener(object : SimpleListener() {
            override fun onAnimationEnd(animation: Animator?) {
                Log.d(TAG, "a -- onAnimationEnd  ====")
            }

            override fun onAnimationStart(animation: Animator?) {
                Log.d(TAG, "a -- onAnimationStart  ====")
            }
        })
        val bAnimator = ObjectAnimator.ofInt(1)
        bAnimator.addListener(object : SimpleListener() {
            override fun onAnimationEnd(animation: Animator?) {
                Log.d(TAG, "b -- onAnimationEnd  ====")
            }

            override fun onAnimationStart(animation: Animator?) {
                Log.d(TAG, "b -- onAnimationStart  ====")
            }
        })
        val cAnimator = ObjectAnimator.ofInt(1)
        cAnimator.addListener(object : SimpleListener() {
            override fun onAnimationEnd(animation: Animator?) {
                Log.d(TAG, "c -- onAnimationEnd  ====")
            }

            override fun onAnimationStart(animation: Animator?) {
                Log.d(TAG, "c -- onAnimationStart  ====")
            }
        })
        val dAnimator = ObjectAnimator.ofInt(1)
        dAnimator.addListener(object : SimpleListener() {
            override fun onAnimationEnd(animation: Animator?) {
                Log.d(TAG, "d -- onAnimationEnd  ====")
            }

            override fun onAnimationStart(animation: Animator?) {
                Log.d(TAG, "d -- onAnimationStart  ====")
            }
        })
//        AnimatorSet().apply {
//            play(aAnimator).before(bAnimator)//a 在b之前播放
//            play(bAnimator).with(cAnimator)//b和c同时播放动画效果
//            play(dAnimator).after(cAnimator)//d 在c播放结束之后播放
//            start()
//        }
//
//        AnimatorSet().apply {
//            playSequentially(aAnimator,bAnimator,cAnimator,dAnimator) //顺序播放
//            start()
//        }
//
//        AnimatorSet().apply {
//            playTogether(aAnimator,bAnimator,cAnimator,dAnimator) //同时播放
//            start()
//        }

        /**
         * 移动后保持在原来的位置
         * 1.translationXBy 在当前位置继续累加
         * 2.translationX 以其实点为圆心，到达距离后不再累加
         */
//        mBinding.centerView.animate().translationX(100f).translationY(100f).setDuration(2000).start()
        mBinding.transX.setOnClickListener {
//            mBinding.centerView.animate().translationXBy(10f).start()
            mBinding.centerView.animate().translationX(100f).start()
        }


        // ValueAnimator 均匀回调
//        val valueAnimator = ObjectAnimator.ofInt(1, 100)
//        valueAnimator.addUpdateListener {
//            Log.d(TAG, " addUpdateListener value ===  ${it.animatedValue}")
//        }
//        valueAnimator.duration = 60_000
//        valueAnimator.start()
        mBinding.expandAnim.setOnClickListener {
            menuAnim(true)
        }
        mBinding.closeAnim.setOnClickListener {
            menuAnim(false)
        }

    }

    private fun menuAnim(isExpand: Boolean){
        val layoutParams = mBinding.menuLay.layoutParams
        val startHeight = if (isExpand) dip2px(32f) else dip2px(222f)
        val endHeight = if (isExpand) dip2px(222f) else dip2px(32f)
        val startWidth = if (isExpand) dip2px(32f) else dip2px(40f)
        val endWidth = if (isExpand) dip2px(40f) else dip2px(32f)
        val widthAnim =  ObjectAnimator.ofInt(startWidth.toInt(), endWidth.toInt())
        widthAnim.addUpdateListener {
            val width = it.animatedValue as Int
            if (layoutParams.width != width){
                layoutParams.width = width
                mBinding.menuLay.layoutParams = layoutParams
                Log.d(TAG, "width = : " + width)
            }
        }
        val expandAnim =  ObjectAnimator.ofInt(startHeight.toInt(), endHeight.toInt())
        expandAnim.addUpdateListener {
            val height = it.animatedValue as Int
            if (layoutParams.height != height){
                layoutParams.height = height
                mBinding.menuLay.layoutParams = layoutParams
                Log.d(TAG, "height = : " + height)
            }
        }
        val startAngle = if (isExpand) 0f else 45f
        val endAngle = if (isExpand) 45f else 0f
        val transAnim = ObjectAnimator.ofFloat(mBinding.menuIv, "rotation",startAngle, endAngle)
        AnimatorSet().apply {
            play(expandAnim).with(transAnim).with(widthAnim)//a 在b之前播放
            duration = 300
            start()
        }
    }

    var density = 0f

    fun dip2px( dpValue: Float): Float {
        if (density > 0) return (dpValue * density + 0.5f)
        density = resources.displayMetrics.density
        return (dpValue * density + 0.5f)
    }
}


open class SimpleListener : Animator.AnimatorListener {
    override fun onAnimationStart(animation: Animator?) {
    }

    override fun onAnimationEnd(animation: Animator?) {
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationRepeat(animation: Animator?) {
    }

}

