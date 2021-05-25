package com.wiser.roll

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView

/**
 * @author Wiser
 */
class RunTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    private var duration = 1500

    private var isAutoStart = false

    var number = 0f
        set(number) {
            field = number
            text = String.format("%,.2f", number)
        }

    init {
        val ta = context.obtainStyledAttributes(attrs,R.styleable.RunTextView)
        duration = ta.getInt(R.styleable.RunTextView_rtv_duration,duration)
        number = ta.getFloat(R.styleable.RunTextView_rtv_number,number)
        isAutoStart = ta.getBoolean(R.styleable.RunTextView_rtv_auto,isAutoStart)
        ta.recycle()

        if (isAutoStart) {
            startAnimator(number)
        }
    }

    fun setDuration(duration: Int): RunTextView? {
        this.duration = duration
        return this
    }

    /**
     * 显示
     * @param number
     */
    fun startAnimator(number: Float) {
        val objectAnimator = ObjectAnimator.ofFloat(
            this, "number", 0f, number
        )
        objectAnimator.duration = duration.toLong()
        objectAnimator.interpolator = AccelerateDecelerateInterpolator()
        objectAnimator.start()
    }
}