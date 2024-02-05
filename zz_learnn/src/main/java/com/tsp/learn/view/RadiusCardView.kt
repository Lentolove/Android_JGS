package com.tsp.learn.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.tsp.learn.R


/**
 * @author tsp
 * Date: 2023/9/11
 * desc:
 */
class RadiusCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : CardView(context, attrs, defStyleAttr) {

    private var tlRadius = 0f
    private var trRadius = 0f
    private var brRadius = 0f
    private var blRadius = 0f

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.RadiusCardView)
        val allRadius = array.getDimension(R.styleable.RadiusCardView_allRadius, 0f)
        if (allRadius == 0f){
            tlRadius = array.getDimension(R.styleable.RadiusCardView_rcv_topLeftRadius, 0f)
            trRadius = array.getDimension(R.styleable.RadiusCardView_rcv_topRightRadius, 0f)
            brRadius = array.getDimension(R.styleable.RadiusCardView_rcv_bottomRightRadius, 0f)
            blRadius = array.getDimension(R.styleable.RadiusCardView_rcv_bottomLeftRadius, 0f)
        }else{
            tlRadius = allRadius
            trRadius = allRadius
            brRadius = allRadius
            blRadius = allRadius
        }
        array.recycle()
        background = ColorDrawable()
    }

    fun setAllCornerRadius(radius : Float){
        setCornersRadius(radius, radius, radius, radius)
    }

    fun setCornersRadius(tlRadius : Float = 0f, trRadius : Float = 0f, blRadius : Float = 0f, brRadius : Float = 0f){
        this.tlRadius = tlRadius
        this.trRadius = trRadius
        this.blRadius = blRadius
        this.brRadius = brRadius
        invalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        val path = Path()
        val rectF = getRectF()
        val radius = floatArrayOf(tlRadius, tlRadius, trRadius, trRadius, brRadius, brRadius, blRadius, blRadius)
        path.addRoundRect(rectF, radius, Path.Direction.CW)
        canvas!!.clipPath(path, Region.Op.INTERSECT)
        super.onDraw(canvas)
    }


    private fun getRectF(): RectF {
        val rect = Rect()
        getDrawingRect(rect)
        return RectF(rect)
    }


}