package com.wiser.rollnumber

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.wiser.rollnumber.ticker.TickerUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tickerView.setCharacterLists(TickerUtils.provideNumberList())
        tickerView.animationInterpolator = DecelerateInterpolator()
        tickerView.typeface = Typeface.MONOSPACE
        tickerView.run { tickerView.text = "$" + 999 }//显示
    }

    fun rollClick(view: View) {

        nfv_number?.jumpNumber()

        tickerView.run { tickerView.text = "$123" + (0..1000).random() + "$" }//显示

        val sb = StringBuffer()
        for (i in 0..(0..3).random()) {
            sb.append((0..9).random())
        }
        rnv_number?.setText(numbers = "1838", "你好", "李焕英",isAnimator = true)
        rnv_number2?.setText(sb.toString(), "你好", "李焕英")

        tvNumber?.startAnimator(sb.toString().toFloat())

    }
}