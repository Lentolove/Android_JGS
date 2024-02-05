package com.tsp.learn.anim

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.tsp.learn.R
import java.util.ArrayList

/**
 * @author tsp
 * Date: 2023/11/17
 * desc:
 */
class XiuxianTeamView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
): ConstraintLayout(context, attrs, defStyleAttr) {

    private val leftTeamLay : View
    private val rightTeamLay : View
    private val leftUserList = ArrayList<View>()
    private val rightUserList = ArrayList<View>()

    init {
        inflate(context, R.layout.xiuxian_team_view, this)
        leftTeamLay = findViewById(R.id.left_team_lay)
        rightTeamLay = findViewById(R.id.right_team_lay)
        leftUserList.add(findViewById(R.id.left_user1))
        leftUserList.add(findViewById(R.id.left_user2))
        leftUserList.add(findViewById(R.id.left_user3))
        leftUserList.add(findViewById(R.id.left_user4))
        leftUserList.add(findViewById(R.id.left_user5))
        rightUserList.add(findViewById(R.id.right_user1))
        rightUserList.add(findViewById(R.id.right_user2))
        rightUserList.add(findViewById(R.id.right_user3))
        rightUserList.add(findViewById(R.id.right_user4))
        rightUserList.add(findViewById(R.id.right_user5))
    }


    fun startHideAnim(){
        index = 0
        AnimUtils.transXHideAndShow(leftTeamLay, true){
                handler.postDelayed({
                    startPkAnim()
                }, 1000)
        }
        AnimUtils.transXHideAndShow(rightTeamLay, false){

        }
    }

    private var index = 0

    fun startPkAnim(){
        if (index >= leftUserList.size) return
        AnimUtils.userHeadPkAnim3(leftUserList[index], rightUserList[index], index % 2 == 0){
            handler.postDelayed({
                index++
                startPkAnim()
            }, 1000)
        }
    }

}