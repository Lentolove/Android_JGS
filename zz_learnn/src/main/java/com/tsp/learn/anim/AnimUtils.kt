package com.tsp.learn.anim

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import com.tsp.learn.ScreenUtils

/**
 * @author tsp
 * Date: 2023/11/17
 * desc:
 */
object AnimUtils {

    fun transXHideAndShow(view: View, toLeft: Boolean, onEnd: (() -> Unit)) {
        val width = view.width.toFloat()
        ObjectAnimator.ofFloat(
            view, "translationX",
            0f,
            if (toLeft) -width else width, 0f
        ).apply {
            duration = 1000
            interpolator = LinearInterpolator()
            addListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    onEnd.invoke()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

            })
        }.start()
    }


    fun userHeadPkAnim2(leftUser: View, rightUser: View, isLeftWin: Boolean, onAnimEnd : (()->Unit)) {
        val viewWidth = leftUser.width.toFloat()
        val viewHeight = leftUser.height.toFloat()
        val leftStarLocation = IntArray(2)
        val rightStartLocation = IntArray(2)
        val centerX = ScreenUtils.getScreenWidth(leftUser.context) / 2
        val centerY = ScreenUtils.getScreenHeight(leftUser.context) - ScreenUtils.dip2px(
            leftUser.context,
            590f
        ) / 2
        leftUser.getLocationOnScreen(leftStarLocation)
        rightUser.getLocationOnScreen(rightStartLocation)
        val transX = centerX - viewWidth  - leftStarLocation[0]
        val transY = centerY + viewHeight / 2 - leftStarLocation[1]
        val anim1Set = AnimatorSet()
        val leftXAnim = ObjectAnimator.ofFloat(leftUser, "translationX", 0f, transX)
        val leftYAnim = ObjectAnimator.ofFloat(leftUser, "translationY", 0f, transY)
        val rightXAnim = ObjectAnimator.ofFloat(rightUser, "translationX", 0f, -transX)
        val rightYAnim = ObjectAnimator.ofFloat(rightUser, "translationY", 0f, transY)

        anim1Set.apply {
            playTogether(leftXAnim, leftYAnim, rightXAnim, rightYAnim)
            interpolator = LinearInterpolator()
            duration = 500
        }

        val animSet2 = AnimatorSet()
        val leftTranX = ObjectAnimator.ofFloat(
            leftUser,
            "translationX",
            transX,
            transX - viewWidth / 2,
        )
        val rightTranX = ObjectAnimator.ofFloat(
            rightUser,
            "translationX",
            -transX,
            -transX + viewWidth / 2,
        )
        val scaleAnimX = ObjectAnimator.ofFloat(
            if (isLeftWin) leftUser else rightUser,
            "scaleX",
            1f,
            1.2f
        )
        val scaleAnimY = ObjectAnimator.ofFloat(
            if (isLeftWin) leftUser else rightUser,
            "scaleY",
            1f,
            1.2f,
        )

        val leftTranX2 = ObjectAnimator.ofFloat(
            leftUser,
            "translationX",
            transX - viewWidth / 2,
            transX,
        )
        val rightTranX2 = ObjectAnimator.ofFloat(
            rightUser,
            "translationX",
            -transX + viewWidth / 2,
            -transX,
        )

        val leftTranX3 = ObjectAnimator.ofFloat(
            leftUser,
            "translationX",
            transX,
            0f,
        )

        val leftTranY3 = ObjectAnimator.ofFloat(
            leftUser,
            "translationY",
            transY,
            0f,
        )

        val rightTranX3 = ObjectAnimator.ofFloat(
            rightUser,
            "translationX",
            -transX,
            0f
        )
        val rightTranY3 = ObjectAnimator.ofFloat(
            rightUser,
            "translationY",
            transY,
            0f,
        )

        val scaleAnimX3 = ObjectAnimator.ofFloat(
            if (isLeftWin) leftUser else rightUser,
            "scaleX",
            1.2f,
            1f
        )
        val scaleAnimY3 = ObjectAnimator.ofFloat(
            if (isLeftWin) leftUser else rightUser,
            "scaleY",
            1.2f,
            1f,
        )

        val animSet3 = AnimatorSet().apply {
            playTogether(leftTranX3, leftTranY3, rightTranX3,rightTranY3, scaleAnimX3, scaleAnimY3)
            interpolator = AccelerateInterpolator()
            duration = 700
        }

        animSet2.apply {
            play(leftTranX).with(rightTranX).with(scaleAnimX).with(scaleAnimY)
            play(leftTranX2).after(leftTranX).with(rightTranX2)
            interpolator = AccelerateInterpolator()
            duration = 300
        }
        AnimatorSet().apply {
            play(anim1Set)
            play(animSet2).after(anim1Set)
            play(animSet3).after(animSet2)
            addListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    onAnimEnd.invoke()
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationRepeat(animation: Animator?) {

                }

            })
        }.start()

    }



    fun userHeadPkAnim3(leftUser: View, rightUser: View, isLeftWin: Boolean, onAnimEnd : (()->Unit)) {
        val viewWidth = leftUser.width.toFloat()
        val viewHeight = leftUser.height.toFloat()
        val leftStarLocation = IntArray(2)
        val rightStartLocation = IntArray(2)
        val centerX = ScreenUtils.getScreenWidth(leftUser.context) / 2
        val centerY = ScreenUtils.getScreenHeight(leftUser.context) - ScreenUtils.dip2px(
            leftUser.context,
            590f
        ) / 2
        leftUser.getLocationOnScreen(leftStarLocation)
        rightUser.getLocationOnScreen(rightStartLocation)
        val transX = centerX - viewWidth  - leftStarLocation[0]
        val transY = centerY + viewHeight / 2 - leftStarLocation[1]
        val anim1Set = AnimatorSet()
        val leftXAnim = ObjectAnimator.ofFloat(leftUser, "translationX", 0f, transX)
        val leftYAnim = ObjectAnimator.ofFloat(leftUser, "translationY", 0f, transY)
        val rightXAnim = ObjectAnimator.ofFloat(rightUser, "translationX", 0f, -transX)
        val rightYAnim = ObjectAnimator.ofFloat(rightUser, "translationY", 0f, transY)

        //1.阶段一：平移到中间
        anim1Set.apply {
            playTogether(leftXAnim, leftYAnim, rightXAnim, rightYAnim)
            interpolator = LinearInterpolator()
            duration = 500
        }

        val leftTranX = ObjectAnimator.ofFloat(
            leftUser,
            "translationX",
            transX,
            transX - viewWidth / 2,
        )
        val rightTranX = ObjectAnimator.ofFloat(
            rightUser,
            "translationX",
            -transX,
            -transX + viewWidth / 2,
        )
        //2.阶段二：往两边离开
        val animSet2 = AnimatorSet().apply {
            playTogether(leftTranX, rightTranX)
            duration = 500
        }


        val leftTrans3 = ObjectAnimator.ofFloat(
            leftUser,
            "translationX",
            transX - viewWidth / 2,
            transX - viewWidth,
        )

        val rightTran3 = ObjectAnimator.ofFloat(
            rightUser,
            "translationX",
            -transX + viewWidth / 2,
            -transX + viewWidth,
        )

        val scaleAnim3x = ObjectAnimator.ofFloat(
            if (isLeftWin) leftUser else rightUser,
            "scaleX",
            1f,
            1.2f
        )
        val scaleAnim3y = ObjectAnimator.ofFloat(
            if (isLeftWin) leftUser else rightUser,
            "scaleY",
            1f,
            1.2f,
        )

        //3.阶段三：往两边离开，赢的头像放大
        val anim3Set = AnimatorSet().apply {
            playTogether(leftTrans3, rightTran3, scaleAnim3x, scaleAnim3y)
            duration = 500
        }


        //4.阶段四, 撞击在一起
        val leftTrans4 = ObjectAnimator.ofFloat(
            leftUser,
            "translationX",
            transX - viewWidth,
            transX,
        )

        val rightTran4 = ObjectAnimator.ofFloat(
            rightUser,
            "translationX",
            -transX + viewWidth,
            -transX,
        )

        val anim4Set = AnimatorSet().apply {
            playTogether(leftTrans4, rightTran4)
            interpolator = AccelerateInterpolator()
            duration = 600
        }


        //阶段5： 输的一方回原处
        val trans5 = ObjectAnimator.ofFloat(
            if (isLeftWin)  rightUser else leftUser,
            "translationX",
            if (isLeftWin) -transX else transX,
            0f,
        )
        val transY5 = ObjectAnimator.ofFloat(
            if (isLeftWin)  rightUser else leftUser,
            "translationY",
            transY,
            0f,
        )
        val animSet5 = AnimatorSet().apply {
            playTogether(trans5, transY5)
            interpolator = AccelerateInterpolator()
        }


        val trans6 = ObjectAnimator.ofFloat(
            if (isLeftWin)  leftUser else rightUser,
            "translationX",
            if (isLeftWin) transX else -transX,
            0f,
        )

        val transY6 = ObjectAnimator.ofFloat(
            if (isLeftWin)  leftUser else rightUser,
            "translationY",
            transY,
            0f,
        )

        val scaleAnim6x = ObjectAnimator.ofFloat(
            if (isLeftWin) leftUser else rightUser,
            "scaleX",
            1.2f,
            1f
        )
        val scaleAnim6y = ObjectAnimator.ofFloat(
            if (isLeftWin) leftUser else rightUser,
            "scaleY",
            1.2f,
            1f,
        )
        val anm6 = AnimatorSet().apply {
            playTogether(trans6, transY6, scaleAnim6x, scaleAnim6y)
            interpolator = AccelerateInterpolator()
            duration = 500
        }

        val totalAnim = AnimatorSet().apply {
            play(anim1Set)
            play(animSet2).after(anim1Set)
            play(anim3Set).after(2000)
            play(anim4Set).after(anim3Set)
            play(animSet5).after(anim4Set)
            play(anm6).after(animSet5)
            addListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    onAnimEnd.invoke()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

            })
        }.start()




    }


}