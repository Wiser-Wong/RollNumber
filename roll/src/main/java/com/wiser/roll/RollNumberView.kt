package com.wiser.roll

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import java.util.regex.Pattern
import kotlin.math.abs

/**
 * @author Wiser
 *
 * 滚动的数字
 */
class RollNumberView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    /**
     * 开始文本paint
     */
    private var startPaint = Paint()

    /**
     * 数字paint
     */
    private var numPaint = Paint()

    /**
     * 结束文本paint
     */
    private var endPaint = Paint()

    /**
     * 当前动画滚动的纵向值，用于更新draw text
     */
    private var currentMoveY = 0f

    /**
     * 初始边距 防止贴太近
     */
    private var borderPadding = 10

    /**
     * 数字paddingLeft
     */
    private var numberPaddingLeft: Float = 0f

    /**
     * 数字paddingRight
     */
    private var numberPaddingRight: Float = 0f

    /**
     * 滚动结束时展示的数字
     */
    private var allNumbers: String? = "0"

    /**
     * 全部text
     */
    private var fullText: String? = ""

    /**
     * 数字前文字
     */
    private var startText: String? = ""

    /**
     * 数字尾文字
     */
    private var endText: String? = ""

    /**
     * 纵向滚动的数字数组
     */
    private var rollNumbersChar = charArrayOf()

    /**
     * 动画时间
     */
    private var duration: Long = 1000

    /**
     * 数字字体大小
     */
    private var numTextSize: Float = 50f

    /**
     * 开始文本字体大小
     */
    private var startTextSize: Float = numTextSize

    /**
     * 结束文本字体大小
     */
    private var endTextSize: Float = numTextSize

    /**
     * 数字字体颜色
     */
    private var numTextColor: Int = Color.DKGRAY

    /**
     * 开始文本字体颜色
     */
    private var startTextColor: Int = numTextColor

    /**
     * 结束文本字体颜色
     */
    private var endTextColor: Int = numTextColor

    /**
     * 字体粗度
     */
    private var textStrokeWidth: Float = 5f

    /**
     * 是否stroke
     */
    private var isStroke: Boolean = false

    /**
     * 滚动随机数个数
     */
    private var rollRandomMaxCount: Int = 10

    /**
     * 记录随机显示数字字符串
     * 例如 输入231
     * 随机字符串显示“3432”，“343213”，“34321291”，
     * 会转换成char[]绘制每一个字符
     */
    private var rollNumberRecords = HashMap<Int, String>()

    /**
     * 方向
     */
    private var direction: Int = UP

    /**
     * 是否有动画
     */
    private var isAnimator: Boolean = true

    companion object {
        const val UP = 1000
        const val DOWN = 1001
    }

    init {

        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RollNumberView)
        duration =
            ta.getInt(R.styleable.RollNumberView_rnv_roll_duration, duration.toInt()).toLong()
        rollRandomMaxCount =
            ta.getInt(R.styleable.RollNumberView_rnv_roll_random_max_count, rollRandomMaxCount)
        startText = ta.getString(R.styleable.RollNumberView_rnv_start_text) ?: ""
        endText = ta.getString(R.styleable.RollNumberView_rnv_end_text) ?: ""
        allNumbers = ta.getString(R.styleable.RollNumberView_rnv_numbers) ?: ""
        numTextSize = ta.getDimension(R.styleable.RollNumberView_rnv_numbers_size, numTextSize)
        startTextSize =
            ta.getDimension(R.styleable.RollNumberView_rnv_start_text_size, numTextSize)
        endTextSize = ta.getDimension(R.styleable.RollNumberView_rnv_end_text_size, numTextSize)
        numTextColor = ta.getColor(R.styleable.RollNumberView_rnv_numbers_color, numTextColor)
        startTextColor =
            ta.getColor(R.styleable.RollNumberView_rnv_start_text_color, numTextColor)
        endTextColor = ta.getColor(R.styleable.RollNumberView_rnv_end_text_color, numTextColor)
        textStrokeWidth =
            ta.getDimension(R.styleable.RollNumberView_rnv_text_stroke, textStrokeWidth)
        isStroke = ta.getBoolean(R.styleable.RollNumberView_rnv_text_is_stroke, isStroke)
        numberPaddingLeft =
            ta.getDimension(R.styleable.RollNumberView_rnv_numbers_padding_left, numberPaddingLeft)
        numberPaddingRight = ta.getDimension(
            R.styleable.RollNumberView_rnv_numbers_padding_right,
            numberPaddingRight
        )
        direction = ta.getInt(R.styleable.RollNumberView_rnv_roll_direction, direction)
        isAnimator = ta.getBoolean(R.styleable.RollNumberView_rnv_auto_animator, isAnimator)
        ta.recycle()

        startPaint.isAntiAlias = true
        startPaint.color = startTextColor
        startPaint.textSize = startTextSize
        startPaint.strokeWidth = textStrokeWidth

        numPaint.isAntiAlias = true
        numPaint.color = numTextColor
        numPaint.textSize = numTextSize
        numPaint.strokeWidth = textStrokeWidth

        endPaint.isAntiAlias = true
        endPaint.color = endTextColor
        endPaint.textSize = endTextSize
        endPaint.strokeWidth = textStrokeWidth

        if (isStroke) {
            startPaint.style = Paint.Style.STROKE
            numPaint.style = Paint.Style.STROKE
            endPaint.style = Paint.Style.STROKE
        }

        fullText = startText + allNumbers + endText

        if (isAnimator) {
            startAnim()
        }
    }

    /**
     * 设置文案 可选择执行动画
     */
    fun setText(
        numbers: String?,
        startText: String? = "",
        endText: String? = "",
        isAnimator: Boolean = true
    ) {
        requestLayout()
        this.fullText = startText + numbers + endText
        this.allNumbers = numbers
        this.startText = startText
        this.endText = endText
        this.isAnimator = isAnimator
        if (isAnimator) {
            startAnim()
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()

        if (isAnimator && isNumber(allNumbers)) {
            // 开始文字
            startText?.let {
                canvas?.drawText(
                    it,
                    paddingLeft.toFloat(),
                    getRealHeight() + paddingTop + borderPadding,
                    startPaint
                )
            }
            allNumbers?.let { nums ->
                // 中间数字
                val numbersChar = nums.toCharArray()
                for ((i, v) in nums.withIndex()) {
                    val chars = getRandomsStr(i, v, numbersChar.size).toCharArray()
                    if (!chars.contentEquals(rollNumbersChar)) {
                        this.rollNumbersChar = chars
                    }
                    for ((j, c) in rollNumbersChar.withIndex()) {
                        // 当已经滚动到最底部的数时，直接绘制最后一个数字，并停留在此处，不进行滚动了，否则就继续绘制滚动数字
                        when (direction) {
                            UP -> {
                                if (abs(getMeasureY() * (rollNumbersChar.size - 1)) <= abs(
                                        currentMoveY
                                    )
                                ) {
                                    canvas?.drawText(
                                        v.toString(),
                                        (startPaint.measureText(startText) + paddingLeft + numberPaddingLeft) + numPaint.measureText(
                                            c.toString()
                                        ) * i,
                                        getRealHeight() + paddingTop + borderPadding,
                                        numPaint
                                    )
                                    break
                                }
                                canvas?.drawText(
                                    rollNumbersChar,
                                    j,
                                    1,
                                    (startPaint.measureText(startText) + paddingLeft + numberPaddingLeft) + numPaint.measureText(
                                        c.toString()
                                    ) * i,
                                    getRealHeight() + paddingTop + borderPadding + getMeasureY() * j + currentMoveY,
                                    numPaint
                                )
                            }
                            DOWN -> {
                                if (abs(getMeasureY() * (rollNumbersChar.size - 1)) <= (getMeasureY() * (rollRandomMaxCount - 1) + currentMoveY)) {
                                    canvas?.drawText(
                                        v.toString(),
                                        (startPaint.measureText(startText) + paddingLeft + numberPaddingLeft) + numPaint.measureText(
                                            c.toString()
                                        ) * i,
                                        getRealHeight() + paddingTop + borderPadding,
                                        numPaint
                                    )
                                    break
                                }
                                canvas?.drawText(
                                    rollNumbersChar,
                                    j,
                                    1,
                                    (startPaint.measureText(startText) + paddingLeft + numberPaddingLeft) + numPaint.measureText(
                                        c.toString()
                                    ) * i,
                                    getRealHeight() + paddingTop + borderPadding - getMeasureY() * j + (getMeasureY() * (rollRandomMaxCount - 1) + currentMoveY),
                                    numPaint
                                )
                            }
                        }
                    }
                }
            }
            // 结束文字
            endText?.let {
                canvas?.drawText(
                    it,
                    startPaint.measureText(startText) + numPaint.measureText(
                        allNumbers ?: ""
                    ) + paddingLeft + numberPaddingLeft + numberPaddingRight,
                    getRealHeight() + paddingTop + borderPadding,
                    endPaint
                )
            }
        } else {
            startText?.let {
                canvas?.drawText(
                    it, paddingLeft.toFloat(), getRealHeight() + paddingTop + borderPadding,
                    startPaint
                )
            }
            allNumbers?.let {
                canvas?.drawText(
                    it,
                    (startPaint.measureText(startText) + paddingLeft + numberPaddingLeft),
                    getRealHeight() + paddingTop + borderPadding,
                    numPaint
                )
            }
            endText?.let {
                canvas?.drawText(
                    it,
                    startPaint.measureText(startText) + numPaint.measureText(
                        allNumbers ?: ""
                    ) + paddingLeft + numberPaddingLeft + numberPaddingRight,
                    getRealHeight() + paddingTop + borderPadding,
                    endPaint
                )
            }
        }
        canvas?.restore()
    }

    /**
     * 获取随机滚动的数字字符串
     * 字符串会转换成char[]来进行绘制
     */
    private fun getRandomsStr(index: Int, num: Char, size: Int): String {
        val str: String? = rollNumberRecords[index]
        if (!TextUtils.isEmpty(str)) {
            return str ?: "01234567"
        }
        val sb = StringBuffer()
        if (rollRandomMaxCount < size * 2) {
            this.rollRandomMaxCount = size * 2 + 4
        }
        for (i in 0..(rollRandomMaxCount - (size - index) * 2)) {
            if (i == 0) {
                sb.append(0)
            } else {
                sb.append((0..9).random())
            }
        }
        sb.append(num)
        rollNumberRecords[index] = sb.toString()
        return sb.toString()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val textWidth =
            startPaint.measureText(startText) + numPaint.measureText(
                allNumbers ?: ""
            ) + endPaint.measureText(
                endText
            )
        var height: Int =
            (getMeasureY() + startPaint.strokeWidth.coerceAtLeast(numPaint.strokeWidth)
                .coerceAtLeast(endPaint.strokeWidth)).toInt()
        var width: Int =
            (paddingLeft + textWidth + paddingRight + numberPaddingLeft + numberPaddingRight).toInt()
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            height = heightSpecSize
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            width = widthSpecSize
        }
        setMeasuredDimension(width, height)
    }

    /**
     * 执行动画
     */
    private fun startAnim() {
        // 清空记录的随机数
        rollNumberRecords.clear()
        allNumbers?.let { nums ->
            val numbersChar = nums.toCharArray()
            if (numbersChar.isNotEmpty()) {
                val chars =
                    getRandomsStr(numbersChar.size, numbersChar[0], numbersChar.size).toCharArray()
                if (!chars.contentEquals(rollNumbersChar)) {
                    this.rollNumbersChar = chars
                }
            }
        }
        val animator = ValueAnimator()
        val startY: Float
        val endY: Float
        if (direction == UP) {
            startY = 0f
            endY = -(rollNumbersChar.size - 1) * getMeasureY()
        } else {
            startY = -(rollNumbersChar.size - 1) * getMeasureY()
            endY = 0f
        }
        animator.setFloatValues(
            startY,
            endY
        )
        animator.interpolator = DecelerateInterpolator()
        animator.duration = duration
        animator.addUpdateListener { valueAnimator ->
            currentMoveY = valueAnimator?.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    /**
     * 获取测量高度
     */
    private fun getMeasureY(): Float =
        getRealHeight() + paddingTop + paddingBottom + borderPadding * 2

    /**
     * 获取文本高度
     */
    private fun getRealHeight(): Float = startPaint.textSize.coerceAtLeast(numPaint.textSize)
        .coerceAtLeast(endPaint.textSize) - startPaint.strokeWidth.coerceAtMost(
        numPaint.strokeWidth
    ).coerceAtMost(endPaint.strokeWidth) * 2

    /**
     * 判断字符串是否为数字
     *
     * @param str
     * @return
     */
    private fun isNumber(str: String?): Boolean {
        if (TextUtils.isEmpty(str)) return false
        val pattern = Pattern.compile("[0-9]*")
        val isNum = pattern.matcher(str)
        return isNum.matches()
    }
}