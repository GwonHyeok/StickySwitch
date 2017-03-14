package io.ghyeok.stickyswitch.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import io.ghyeok.stickyswitch.R

/**
 * Created by ghyeok on 2017. 3. 13..
 */
class StickySwitch : View {

    private val TAG = "LIQUID_SWITCH"

    // 아이콘 이미지
    lateinit private var leftIconDrawable: Drawable
    lateinit private var rightIconDrawable: Drawable

    // 아이콘 사이즈, 패딩
    private var iconSize = 100
    private var iconPadding = 70

    // 아이콘 텍스트
    private var leftText = ""
    private var rightText = ""

    @ColorInt private var sliderBackgroundColor = 0XFF181821.toInt()
    @ColorInt private var switchColor = 0xFF2371FA.toInt()

    private val sliderBackgroundPaint = Paint()
    private val sliderBackgroundRect = RectF()

    private val switchBackgroundPaint = Paint()

    // 왼쪽, 오른쪽 글씨 페인트, 크기
    private val leftTextPaint = Paint()
    private val leftTextRect = Rect()
    private val rightTextPaint = Paint()
    private val rightTextRect = Rect()

    // 왼쪽 글씨 텍스트 크기
    private var leftTextSize = 50f
        set(value) {
            field = value
            invalidate()
        }
    private var rightTextSize = 50f
        set(value) {
            field = value
            invalidate()
        }

    // 텍스트 투명도
    private var leftTextAlpha = 255
        set(value) {
            field = value
            invalidate()
        }
    private var rightTextAlpha = 163
        set(value) {
            field = value
            invalidate()
        }

    // 일반 텍스트 크기
    private var textSize = 50
        set(value) {
            field = value
            invalidate()
        }

    // 선택된 텍스트 크기
    private var selectedTextSize = 50
        set(value) {
            field = value
            invalidate()
        }

    // 스위치 상태
    private var isSwitchOn = false
        set(value) {
            field = value
            invalidate()
        }

    // 스위치 상태 변경시 애니메이션 진행도
    private var animatePercent: Double = 0.0
        set(value) {
            field = value
            invalidate()
        }

    // 바운스 animate rate
    private var animateBounceRate: Double = 1.0
        set(value) {
            field = value
            invalidate()
        }

    // 리스너
    var onSelectedChangeListener: OnSelectedChangeListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs, defStyleAttr, defStyleRes)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StickySwitch, defStyleAttr, defStyleRes)

        // 왼쪽 스위치 아이콘
        leftIconDrawable = typedArray.getDrawable(R.styleable.StickySwitch_leftIcon)
        leftText = typedArray.getString(R.styleable.StickySwitch_leftText)

        // 오른쪽 스위치 아이콘
        rightIconDrawable = typedArray.getDrawable(R.styleable.StickySwitch_rightIcon)
        rightText = typedArray.getString(R.styleable.StickySwitch_rightText)

        // 아이콘 크기
        iconSize = typedArray.getDimensionPixelSize(R.styleable.StickySwitch_iconSize, iconSize)
        iconPadding = typedArray.getDimensionPixelSize(R.styleable.StickySwitch_iconPadding, iconPadding)

        // 저장되어 있는 텍스트 사이즈
        textSize = typedArray.getDimensionPixelSize(R.styleable.StickySwitch_textSize, textSize)
        selectedTextSize = typedArray.getDimensionPixelSize(R.styleable.StickySwitch_selectedTextSize, selectedTextSize)

        // 현재 텍스트 사이즈
        leftTextSize = selectedTextSize.toFloat()
        rightTextSize = textSize.toFloat()

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // 아이콘 상하좌우 margin
        val iconMarginLeft = iconPadding
        val iconMarginBottom = iconPadding
        val iconMarginRight = iconPadding
        val iconMarginTop = iconPadding

        // 아이콘 가로 크기
        val iconWidth = iconSize
        val iconHeight = iconSize

        // 원 Radius
        val sliderRadius = iconMarginTop + iconHeight / 2f
        val circleRadius = iconMarginTop + iconHeight / 2f

        // 아이콘뒤를 감싸는 타원 배경
        sliderBackgroundPaint.color = sliderBackgroundColor
        sliderBackgroundRect.set(0f, 0f, measuredWidth.toFloat(), (iconMarginTop + iconHeight + iconMarginBottom).toFloat())
        canvas?.drawRoundRect(sliderBackgroundRect, sliderRadius, sliderRadius, sliderBackgroundPaint)

        // 현재 상태를 나타내는 버튼
        switchBackgroundPaint.color = switchColor

        // 애니메이션 상상
        // 0% ~ 50% 까지는 반지름의 절반이 될때까지 원이 반대편 원에 도착한다
        // 50% ~ 100% 까지는 원이 점점커진다
        canvas?.save()

        // 애니메이션 절반 이전
        val isBeforeHalf = animatePercent in 0.0..0.5

        // 애니메이션에서 원, 사각형 들이 움직여야하는 길이
        val widthSpace = measuredWidth - circleRadius * 2

        // 원본 원 x, y, radius
        val ocX = (circleRadius + widthSpace * Math.min(1.0, animatePercent * 2))
        val ocY = circleRadius
        val ocRadius = circleRadius * (if (isBeforeHalf) 1.0 - animatePercent else animatePercent)

        // 복제 원 x, y, radius
        val ccX = (circleRadius + widthSpace * (if (isBeforeHalf) 0.0 else Math.abs(0.5 - animatePercent) * 2))
        val ccY = circleRadius
        val ccRadius = circleRadius * (if (isBeforeHalf) 1.0 - animatePercent else animatePercent)

        // 원과 원 사이에 줄
        val rectL = ccX
        val rectT = circleRadius - circleRadius / 2
        val rectR = ocX
        val rectB = circleRadius + circleRadius / 2

        canvas?.drawCircle(ocX.toFloat(), ocY, evaluateBounceRate(ocRadius).toFloat(), switchBackgroundPaint)
        canvas?.drawCircle(ccX.toFloat(), ccY, evaluateBounceRate(ccRadius).toFloat(), switchBackgroundPaint)
        canvas?.drawRect(rectL.toFloat(), rectT, rectR.toFloat(), rectB, switchBackgroundPaint)

        canvas?.restore()

        // 왼쪽 아이콘
        canvas?.save()
        leftIconDrawable.bounds.set(iconMarginLeft,
                iconMarginTop,
                iconMarginLeft + iconWidth,
                iconMarginTop + iconHeight
        )
        leftIconDrawable.alpha = if (isSwitchOn) 153 else 255
        leftIconDrawable.draw(canvas)
        canvas?.restore()

        // 오른쪽 아이콘
        canvas?.save()
        rightIconDrawable.bounds.set(
                measuredWidth - iconWidth - iconMarginRight,
                iconMarginTop,
                measuredWidth - iconMarginRight,
                iconMarginTop + iconHeight
        )
        rightIconDrawable.alpha = if (!isSwitchOn) 153 else 255
        rightIconDrawable.draw(canvas)
        canvas?.restore()

        // 스위치 하단 남는 부분
        val bottomSpaceHeight = measuredHeight - (circleRadius * 2)

        // 텍스트 페인트 색상
        leftTextPaint.color = Color.WHITE
        leftTextPaint.alpha = leftTextAlpha
        rightTextPaint.color = Color.WHITE
        rightTextPaint.alpha = rightTextAlpha

        // 텍스트 페인트 크기
        leftTextPaint.textSize = leftTextSize
        rightTextPaint.textSize = rightTextSize

        // 텍스트 크기 측정
        measureText()

        // 왼쪽 텍스트 좌표
        val leftTextX = (circleRadius * 2 - leftTextRect.width()) * 0.5
        val leftTextY = (circleRadius * 2) + (bottomSpaceHeight * 0.5) + (leftTextRect.height() * 0.25)

        // 왼쪽 아이콘 하단 글씨
        canvas?.save()
        canvas?.drawText(leftText, leftTextX.toFloat(), leftTextY.toFloat(), leftTextPaint)
        canvas?.restore()

        // 오른쪽 텍스트 좌표
        val rightTextX = ((circleRadius * 2 - rightTextRect.width()) * 0.5) + (measuredWidth - (circleRadius * 2))
        val rightTextY = (circleRadius * 2) + (bottomSpaceHeight * 0.5) + (rightTextRect.height() * 0.25)

        // 오른쪽 아이콘 하단 글씨
        canvas?.save()
        canvas?.drawText(rightText, rightTextX.toFloat(), rightTextY.toFloat(), rightTextPaint)
        canvas?.restore()
    }

    private fun evaluateBounceRate(value: Double): Double {
        return value * animateBounceRate
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        isSwitchOn = isSwitchOn.not()
        animateCheckState(isSwitchOn)
        onSelectedChangeListener?.onSelectedChange(if (isSwitchOn) Direction.RIGHT else Direction.LEFT)

        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        measureText()

        val diameter = (iconPadding + iconSize / 2) * 2
        val textWidth = leftTextRect.width() + rightTextRect.width()

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = 0

        when (heightMode) {
            MeasureSpec.UNSPECIFIED -> heightSize = heightMeasureSpec
            MeasureSpec.AT_MOST -> heightSize = diameter + (selectedTextSize * 2)
            MeasureSpec.EXACTLY -> heightSize = MeasureSpec.getSize(heightMeasureSpec)
        }

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = 0

        when (widthMode) {
            MeasureSpec.UNSPECIFIED -> widthSize = widthMeasureSpec
            MeasureSpec.AT_MOST -> widthSize = diameter * 2 + textWidth
            MeasureSpec.EXACTLY -> widthSize = MeasureSpec.getSize(widthMeasureSpec)
        }

        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    private fun measureText() {
        leftTextPaint.getTextBounds(leftText, 0, leftText.length, leftTextRect)
        rightTextPaint.getTextBounds(rightText, 0, rightText.length, rightTextRect)
    }

    private fun animateCheckState(newCheckedState: Boolean) {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
                getLiquidAnimator(newCheckedState),
                leftTextSizeAnimator(newCheckedState),
                rightTextSizeAnimator(newCheckedState),
                leftTextAlphaAnimator(newCheckedState),
                rightTextAlphaAnimator(newCheckedState),
                getBounceAnimator()
        )
        animatorSet.start()
    }

    /**
     * 왼쪽 텍스트 알파 애니메이터
     */
    private fun leftTextAlphaAnimator(newCheckedState: Boolean): Animator {
        val toAlpha = if (newCheckedState) 163 else 255
        val animator = ValueAnimator.ofInt(leftTextAlpha, toAlpha)
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.startDelay = 200
        animator.duration = 400
        animator.addUpdateListener { leftTextAlpha = (it.animatedValue as Int) }
        return animator
    }

    private fun rightTextAlphaAnimator(newCheckedState: Boolean): Animator {
        val toAlpha = if (newCheckedState) 255 else 163
        val animator = ValueAnimator.ofInt(rightTextAlpha, toAlpha)
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.startDelay = 200
        animator.duration = 400
        animator.addUpdateListener { rightTextAlpha = (it.animatedValue as Int) }
        return animator
    }

    /**
     * 왼쪽 텍스트 크기 변경 에니메이터
     */
    private fun leftTextSizeAnimator(newCheckedState: Boolean): Animator {
        val toTextSize = if (newCheckedState) textSize else selectedTextSize
        val textSizeAnimator = ValueAnimator.ofFloat(leftTextSize, toTextSize.toFloat())
        textSizeAnimator.interpolator = AccelerateDecelerateInterpolator()
        textSizeAnimator.startDelay = 200
        textSizeAnimator.duration = 400
        textSizeAnimator.addUpdateListener { leftTextSize = (it.animatedValue as Float) }
        return textSizeAnimator
    }

    /**
     * 오른쪽 텍스트 크기 변경 에니메이터
     */
    private fun rightTextSizeAnimator(newCheckedState: Boolean): Animator {
        val toTextSize = if (newCheckedState) selectedTextSize else textSize
        val textSizeAnimator = ValueAnimator.ofFloat(rightTextSize, toTextSize.toFloat())
        textSizeAnimator.interpolator = AccelerateDecelerateInterpolator()
        textSizeAnimator.startDelay = 200
        textSizeAnimator.duration = 400
        textSizeAnimator.addUpdateListener { rightTextSize = (it.animatedValue as Float) }
        return textSizeAnimator
    }

    /**
     * 상태 변경시 물방울 효과 에니메이터
     */
    private fun getLiquidAnimator(newCheckedState: Boolean): Animator {
        val liquidAnimator = ValueAnimator.ofFloat(animatePercent.toFloat(), if (newCheckedState) 1f else 0f)
        liquidAnimator.duration = 600
        liquidAnimator.interpolator = AccelerateInterpolator()
        liquidAnimator.addUpdateListener { animatePercent = (it.animatedValue as Float).toDouble() }
        return liquidAnimator
    }

    /**
     * 원이 바운스 하는 효과 에니메이터
     */
    private fun getBounceAnimator(): Animator {
        val animator = ValueAnimator.ofFloat(1f, 0.9f, 1f)
        animator.duration = 250
        animator.startDelay = 600
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animateBounceRate = (it.animatedValue as Float).toDouble() }
        return animator
    }

    enum class Direction {
        LEFT, RIGHT
    }

    interface OnSelectedChangeListener {
        fun onSelectedChange(direction: Direction)
    }
}