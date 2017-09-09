package com.hackthenorth.pennapps.investorbuddy

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

/**
 * Created by kevin on 2017-09-09.
 */
class DragGroup @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : ViewGroup(context, attrs, defStyle) {

    private val mDragHelper: ViewDragHelper

    private var mHeaderView: View? = null
    private var mBodyView: View? = null
    private var mBodyContent: View? = null

    private var mInitialMotionX: Float = 0f
    private var mInitialMotionY: Float = 0f

    private var mDragRange: Int = 0
    private var mTop: Int = 0
    private var mDragOffset: Float = 0f
    private var mDragDown: Boolean = false

    override fun onFinishInflate() {
        super.onFinishInflate()
        mHeaderView = findViewById(R.id.dragHeader)
        mBodyView = findViewById(R.id.dragBody)
        mBodyContent = findViewById(R.id.dragBodyContent)
    }

    init {
        mDragHelper = ViewDragHelper.create(this, 1f, DragHelperCallback())
    }

    fun maximize() {
        smoothSlideTo(0f)
    }

    internal fun smoothSlideTo(slideOffset: Float): Boolean {
        val topBound = paddingTop
        val y = (topBound + slideOffset * mDragRange).toInt()

        if (mDragHelper.smoothSlideViewTo(mHeaderView, mHeaderView!!.left, y)) {
            ViewCompat.postInvalidateOnAnimation(this)
            return true
        }
        return false
    }

    private inner class DragHelperCallback : ViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child === mHeaderView
        }

        override fun onViewPositionChanged(changedView: View?, left: Int, top: Int, dx: Int, dy: Int) {
            mTop = top

            mDragOffset = top.toFloat() / mDragRange

            mHeaderView!!.pivotY = mHeaderView!!.height.toFloat()

            mBodyContent!!.alpha = 1 - mDragOffset

            requestLayout()
        }

        override fun onViewReleased(releasedChild: View?, xvel: Float, yvel: Float) {
            var top = paddingTop
            if (yvel > 0 || yvel == 0f && mDragOffset > 0.5f) {
                top += mDragRange
            }
            mDragHelper.settleCapturedViewAt(releasedChild!!.left, top)
        }

        override fun getViewVerticalDragRange(child: View?): Int {
            return mDragRange
        }

        override fun clampViewPositionVertical(child: View?, top: Int, dy: Int): Int {
            val topBound = paddingTop
            val bottomBound = height - mHeaderView!!.height - mHeaderView!!.paddingBottom

            val newTop = Math.min(Math.max(top, topBound), bottomBound)
            return newTop
        }

    }

    override fun computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(ev)

        if (action != MotionEvent.ACTION_DOWN) {
            mDragHelper.cancel()
            return super.onInterceptTouchEvent(ev)
        }

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel()
            return false
        }

        val x = ev.x
        val y = ev.y
        var interceptTap = false

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mInitialMotionX = x
                mInitialMotionY = y
                interceptTap = mDragHelper.isViewUnder(mHeaderView, x.toInt(), y.toInt())
            }

            MotionEvent.ACTION_MOVE -> {
                val adx = Math.abs(x - mInitialMotionX)
                val ady = Math.abs(y - mInitialMotionY)
                val slop = mDragHelper.touchSlop
                if (ady > slop && adx > ady) {
                    mDragHelper.cancel()
                    return false
                }
            }
        }

        return mDragHelper.shouldInterceptTouchEvent(ev) || interceptTap
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        mDragHelper.processTouchEvent(ev)

        val action = ev.action
        val x = ev.x
        val y = ev.y

        val isHeaderViewUnder = mDragHelper.isViewUnder(mHeaderView, x.toInt(), y.toInt())
        when (action and MotionEventCompat.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mInitialMotionX = x
                mInitialMotionY = y
                mDragDown = (mDragOffset == 0f)
            }

            MotionEvent.ACTION_UP -> {
                val dx = x - mInitialMotionX
                val dy = y - mInitialMotionY
                val slop = mDragHelper.touchSlop
                if (isHeaderViewUnder) {
                    if (dx * dx + dy * dy < slop * slop) {
                        if (mDragOffset == 0f) {
                            smoothSlideTo(1f);
                        } else {
                            smoothSlideTo(0f);
                        }
                    } else {
                        var slideOffset = if (mDragDown) (if (mDragOffset > 0.15) 1f else 0f) else (if (mDragOffset < 0.85) 0f else 1f)
                        smoothSlideTo(slideOffset)
                    }
                }
            }
        }


        return isHeaderViewUnder && isViewHit(mHeaderView!!, x.toInt(), y.toInt()) || isViewHit(mBodyView!!, x.toInt(), y.toInt())
    }


    private fun isViewHit(view: View, x: Int, y: Int): Boolean {
        val viewLocation = IntArray(2)
        view.getLocationOnScreen(viewLocation)
        val parentLocation = IntArray(2)
        this.getLocationOnScreen(parentLocation)
        val screenX = parentLocation[0] + x
        val screenY = parentLocation[1] + y
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.width &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.height
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        val maxWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val maxHeight = View.MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(View.resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                View.resolveSizeAndState(maxHeight, heightMeasureSpec, 0))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mDragRange = height - mHeaderView!!.height

        mHeaderView!!.layout(
                0,
                mTop,
                r,
                mTop + mHeaderView!!.measuredHeight)

        mBodyView!!.layout(
                0,
                mTop + mHeaderView!!.measuredHeight,
                r,
                mTop + b)
    }
}