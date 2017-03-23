/*
 * MIT License
 *
 * Copyright (c) 2017 GwonHyeok
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import io.ghyeok.stickyswitch.R

/**
 * Created by ghyeok on 2017. 3. 13..
 *
 * This class implements a beautiful switch widget for android
 *
 * @author GwonHyeok
 */
class StickySwitch : View {

    private val TAG = "LIQUID_SWITCH"

    // left, right icon drawable
    var leftIcon: Drawable? = null
        set(drawable) {
            field = drawable
            invalidate()
        }
    private var rightIcon: Drawable? = null
        set(drawable) {
            field = drawable
            invalidate()
        }

    // icon variables
    private var iconSize = 100
    private var iconPadding = 70

    // text variables
    var leftText = ""
        set(value) {
            field = value
            invalidate()
        }
    var rightText = ""
        set(value) {
            field = value
            invalidate()
        }

    // colors
    var sliderBackgroundColor = 0XFF181821.toInt()
        set(@ColorInt colorInt) {
            field = colorInt
            invalidate()
        }
    var switchColor = 0xFF2371FA.toInt()
        set(@ColorInt colorInt) {
            field = colorInt
            invalidate()
        }
    var textColor = 0xFFFFFFFF.toInt()
        set(@ColorInt colorInt) {
            field = colorInt
            invalidate()
        }

    // rounded rect
    private val sliderBackgroundPaint = Paint()
    private val sliderBackgroundRect = RectF()

    // circular switch
    private val switchBackgroundPaint = Paint()

    // left, right text paint and size
    private val leftTextPaint = Paint()
    private val leftTextRect = Rect()
    private val rightTextPaint = Paint()
    private val rightTextRect = Rect()

    // left, right text size
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

    // text color transparency
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

    // text size
    private var textSize = 50
        set(value) {
            field = value
            invalidate()
        }

    // text size when selected status
    private var selectedTextSize = 50
        set(value) {
            field = value
            invalidate()
        }

    // switch Status
    // false : left status
    // true  : right status
    private var isSwitchOn = false
        set(value) {
            field = value
            invalidate()
        }

    // percent of switch animation
    // animatePercent will be 0.0 ~ 1.0
    private var animatePercent: Double = 0.0
        set(value) {
            field = value
            invalidate()
        }

    // circular switch bounce rate
    // animateBounceRate will be 1.1 ~ 0.0
    private var animateBounceRate: Double = 1.0
        set(value) {
            field = value
            invalidate()
        }

    private val isUnderLollipop = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP

    // listener
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

        // left switch icon
        leftIcon = typedArray.getDrawable(R.styleable.StickySwitch_leftIcon)
        leftText = typedArray.getString(R.styleable.StickySwitch_leftText) ?: leftText

        // right switch icon
        rightIcon = typedArray.getDrawable(R.styleable.StickySwitch_rightIcon)
        rightText = typedArray.getString(R.styleable.StickySwitch_rightText) ?: rightText

        // icon size
        iconSize = typedArray.getDimensionPixelSize(R.styleable.StickySwitch_iconSize, iconSize)
        iconPadding = typedArray.getDimensionPixelSize(R.styleable.StickySwitch_iconPadding, iconPadding)

        // saved text size
        textSize = typedArray.getDimensionPixelSize(R.styleable.StickySwitch_textSize, textSize)
        selectedTextSize = typedArray.getDimensionPixelSize(R.styleable.StickySwitch_selectedTextSize, selectedTextSize)

        // current text size
        leftTextSize = selectedTextSize.toFloat()
        rightTextSize = textSize.toFloat()

        // slider background color
        sliderBackgroundColor = typedArray.getColor(R.styleable.StickySwitch_sliderBackgroundColor, sliderBackgroundColor)

        // switch color
        switchColor = typedArray.getColor(R.styleable.StickySwitch_switchColor, switchColor)

        // text color
        textColor = typedArray.getColor(R.styleable.StickySwitch_textColor, textColor)

        typedArray.recycle()
    }

    /**
     * Draw Sticky Switch View
     *
     * Animation
     *
     * 0% ~ 50%
     * radius : circle radius -> circle radius / 2
     * x      : x -> x + widthSpace
     * y      : y
     *
     * 50% ~ 100%
     * radius : circle radius / 2 -> circle radius
     * x      : x + widthSpace -> x + widthSpace
     * y      : y
     *
     * @param canvas the canvas on which the background will be drawn
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // icon margin
        val iconMarginLeft = iconPadding
        val iconMarginBottom = iconPadding
        val iconMarginRight = iconPadding
        val iconMarginTop = iconPadding

        // icon width, height
        val iconWidth = iconSize
        val iconHeight = iconSize

        // circle Radius
        val sliderRadius = iconMarginTop + iconHeight / 2f
        val circleRadius = iconMarginTop + iconHeight / 2f

        // draw circular rect
        sliderBackgroundPaint.color = sliderBackgroundColor
        sliderBackgroundRect.set(0f, 0f, measuredWidth.toFloat(), (iconMarginTop + iconHeight + iconMarginBottom).toFloat())
        canvas?.drawRoundRect(sliderBackgroundRect, sliderRadius, sliderRadius, sliderBackgroundPaint)

        // switch background
        switchBackgroundPaint.color = switchColor

        canvas?.save()

        // if animation is before half
        val isBeforeHalf = animatePercent in 0.0..0.5

        // width at which objects move in animation
        val widthSpace = measuredWidth - circleRadius * 2

        // original circular switch x, y, radius
        val ocX = (circleRadius + widthSpace * Math.min(1.0, animatePercent * 2))
        val ocY = circleRadius
        val ocRadius = circleRadius * (if (isBeforeHalf) 1.0 - animatePercent else animatePercent)

        // copied circular switch x, y, radius
        val ccX = (circleRadius + widthSpace * (if (isBeforeHalf) 0.0 else Math.abs(0.5 - animatePercent) * 2))
        val ccY = circleRadius
        val ccRadius = circleRadius * (if (isBeforeHalf) 1.0 - animatePercent else animatePercent)

        // circular rectangle
        val rectL = ccX
        val rectT = circleRadius - circleRadius / 2
        val rectR = ocX
        val rectB = circleRadius + circleRadius / 2

        canvas?.drawCircle(ocX.toFloat(), ocY, evaluateBounceRate(ocRadius).toFloat(), switchBackgroundPaint)
        canvas?.drawCircle(ccX.toFloat(), ccY, evaluateBounceRate(ccRadius).toFloat(), switchBackgroundPaint)
        canvas?.drawRect(rectL.toFloat(), rectT, rectR.toFloat(), rectB, switchBackgroundPaint)

        canvas?.restore()

        // draw left icon
        if (leftIcon != null) {
            canvas?.save()
            leftIcon?.bounds?.set(iconMarginLeft,
                    iconMarginTop,
                    iconMarginLeft + iconWidth,
                    iconMarginTop + iconHeight
            )
            leftIcon?.alpha = if (isSwitchOn) 153 else 255
            leftIcon?.draw(canvas)
            canvas?.restore()
        }

        // draw right icon
        if (rightIcon != null) {
            canvas?.save()
            rightIcon?.bounds?.set(
                    measuredWidth - iconWidth - iconMarginRight,
                    iconMarginTop,
                    measuredWidth - iconMarginRight,
                    iconMarginTop + iconHeight
            )
            rightIcon?.alpha = if (!isSwitchOn) 153 else 255
            rightIcon?.draw(canvas)
            canvas?.restore()
        }

        // bottom space
        val bottomSpaceHeight = measuredHeight - (circleRadius * 2)

        // set text paint
        leftTextPaint.color = textColor
        leftTextPaint.alpha = leftTextAlpha
        rightTextPaint.color = textColor
        rightTextPaint.alpha = rightTextAlpha

        // set text size
        leftTextPaint.textSize = leftTextSize
        rightTextPaint.textSize = rightTextSize

        // measure text size
        measureText()

        // left text position
        val leftTextX = (circleRadius * 2 - leftTextRect.width()) * 0.5
        val leftTextY = (circleRadius * 2) + (bottomSpaceHeight * 0.5) + (leftTextRect.height() * 0.25)

        // draw left text
        canvas?.save()
        canvas?.drawText(leftText, leftTextX.toFloat(), leftTextY.toFloat(), leftTextPaint)
        canvas?.restore()

        // right text position
        val rightTextX = ((circleRadius * 2 - rightTextRect.width()) * 0.5) + (measuredWidth - (circleRadius * 2))
        val rightTextY = (circleRadius * 2) + (bottomSpaceHeight * 0.5) + (rightTextRect.height() * 0.25)

        // draw right text
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
        notifySelectedChange()

        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        // measure text size
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

    fun setDirection(direction: Direction) {
        var newSwitchState = isSwitchOn
        when (direction) {
            Direction.LEFT -> newSwitchState = false
            Direction.RIGHT -> newSwitchState = true
        }

        if (newSwitchState != isSwitchOn) {
            isSwitchOn = newSwitchState
            animateCheckState(isSwitchOn)
            notifySelectedChange()
        }
    }

    fun getDirection(): Direction {
        when (isSwitchOn) {
            true -> return Direction.RIGHT
            false -> return Direction.LEFT
        }
    }

    @JvmOverloads
    fun getText(direction: Direction = getDirection()): String {
        when (direction) {
            Direction.LEFT -> return leftText
            Direction.RIGHT -> return rightText
        }
    }

    fun setLeftIcon(@DrawableRes resourceId: Int) {
        this.leftIcon = this.getDrawable(resourceId)

    }

    fun setRightIcon(@DrawableRes resourceId: Int) {
        this.rightIcon = this.getDrawable(resourceId)
    }

    private fun getDrawable(@DrawableRes resourceId: Int): Drawable {
        if (isUnderLollipop)
            return resources.getDrawable(resourceId)
        else
            return resources.getDrawable(resourceId, null)
    }

    private fun notifySelectedChange() {
        onSelectedChangeListener?.onSelectedChange(if (isSwitchOn) Direction.RIGHT else Direction.LEFT, getText())
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

    private fun leftTextSizeAnimator(newCheckedState: Boolean): Animator {
        val toTextSize = if (newCheckedState) textSize else selectedTextSize
        val textSizeAnimator = ValueAnimator.ofFloat(leftTextSize, toTextSize.toFloat())
        textSizeAnimator.interpolator = AccelerateDecelerateInterpolator()
        textSizeAnimator.startDelay = 200
        textSizeAnimator.duration = 400
        textSizeAnimator.addUpdateListener { leftTextSize = (it.animatedValue as Float) }
        return textSizeAnimator
    }

    private fun rightTextSizeAnimator(newCheckedState: Boolean): Animator {
        val toTextSize = if (newCheckedState) selectedTextSize else textSize
        val textSizeAnimator = ValueAnimator.ofFloat(rightTextSize, toTextSize.toFloat())
        textSizeAnimator.interpolator = AccelerateDecelerateInterpolator()
        textSizeAnimator.startDelay = 200
        textSizeAnimator.duration = 400
        textSizeAnimator.addUpdateListener { rightTextSize = (it.animatedValue as Float) }
        return textSizeAnimator
    }

    private fun getLiquidAnimator(newCheckedState: Boolean): Animator {
        val liquidAnimator = ValueAnimator.ofFloat(animatePercent.toFloat(), if (newCheckedState) 1f else 0f)
        liquidAnimator.duration = 600
        liquidAnimator.interpolator = AccelerateInterpolator()
        liquidAnimator.addUpdateListener { animatePercent = (it.animatedValue as Float).toDouble() }
        return liquidAnimator
    }

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
        fun onSelectedChange(direction: Direction, text: String)
    }
}