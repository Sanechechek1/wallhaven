package com.netsoftware.wallhaven.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.view.ViewCompat
import com.dinuscxj.refresh.IRefreshStatus

class MaterialRefreshView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr), IRefreshStatus {

    private val mArcBounds = RectF()
    private val mPaint = Paint()

    private var mStartDegrees: Float = 0.toFloat()
    private var mSwipeDegrees: Float = 0.toFloat()

    private var mStrokeWidth: Float = 0.toFloat()
    private var mShadowRadius: Int = 0

    private var mHasTriggeredRefresh: Boolean = false

    private var mRotateAnimator: ValueAnimator? = null

    private var mColor: Int = Color.parseColor("#FFD72263")

    init {
        initData()
        initPaint()
    }

    constructor(context: Context, color: Int): this(context){
        this.mColor = color
        initPaint()
    }

    private fun initData() {
        val density = resources.displayMetrics.density
        mStrokeWidth = density * DEFAULT_STROKE_WIDTH

        mStartDegrees = DEFAULT_START_DEGREES.toFloat()
        mSwipeDegrees = 0.0f
    }

    private fun initPaint() {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mStrokeWidth
        mPaint.color = mColor
    }

    private fun initBackground(radius: Float) {
        val density = context.resources.displayMetrics.density
        val diameter = (radius * density * 2f).toInt()
        val shadowYOffset = (density * Y_OFFSET).toInt()
        val shadowXOffset = (density * X_OFFSET).toInt()

        val color = Color.parseColor("#FFFAFAFA")

        mShadowRadius = (density * SHADOW_RADIUS).toInt()

        val circle: ShapeDrawable
        if (elevationSupported()) {
            circle = ShapeDrawable(OvalShape())
            ViewCompat.setElevation(this, SHADOW_ELEVATION * density)
        } else {
            val oval = OvalShadow(mShadowRadius, diameter)
            circle = ShapeDrawable(oval)
            circle.paint.setShadowLayer(
                mShadowRadius.toFloat(), shadowXOffset.toFloat(), shadowYOffset.toFloat(),
                KEY_SHADOW_COLOR
            )
            val padding = mShadowRadius
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding)
        }
        circle.paint.color = color
        setBackgroundDrawable(circle)
    }

    private fun elevationSupported(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= 21
    }

    private fun startAnimator() {
        mRotateAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        mRotateAnimator!!.interpolator = LinearInterpolator()
        mRotateAnimator!!.addUpdateListener { animation ->
            val rotateProgress = animation.animatedValue as Float
            setStartDegrees(DEFAULT_START_DEGREES + rotateProgress * 360)
        }
        mRotateAnimator!!.repeatMode = ValueAnimator.RESTART
        mRotateAnimator!!.repeatCount = ValueAnimator.INFINITE
        mRotateAnimator!!.duration = ANIMATION_DURATION.toLong()

        mRotateAnimator!!.start()
    }

    private fun resetAnimator() {
        if (mRotateAnimator != null) {
            mRotateAnimator!!.cancel()
            mRotateAnimator!!.removeAllUpdateListeners()

            mRotateAnimator = null
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawArc(canvas)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        resetAnimator()
    }

    private fun drawArc(canvas: Canvas) {
        canvas.drawArc(mArcBounds, mStartDegrees, mSwipeDegrees, false, mPaint)
    }

    private fun setStartDegrees(startDegrees: Float) {
        mStartDegrees = startDegrees
        postInvalidate()
    }

    fun setSwipeDegrees(swipeDegrees: Float) {
        this.mSwipeDegrees = swipeDegrees
        postInvalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val radius = Math.min(w, h) / 2.0f
        val centerX = w / 2.0f
        val centerY = h / 2.0f

        mArcBounds.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        mArcBounds.inset(radius * 0.333f, radius * 0.333f)

        initBackground(radius)
    }

    override fun reset() {
        resetAnimator()

        mHasTriggeredRefresh = false
        mStartDegrees = DEFAULT_START_DEGREES.toFloat()
        mSwipeDegrees = 0.0f
    }

    override fun refreshing() {
        mHasTriggeredRefresh = true

        startAnimator()
    }

    override fun refreshComplete() {

    }

    override fun pullToRefresh() {

    }

    override fun releaseToRefresh() {}

    override fun pullProgress(pullDistance: Float, pullProgress: Float) {
        if (!mHasTriggeredRefresh) {
            val swipeProgress = Math.min(1.0f, pullProgress)
            setSwipeDegrees(swipeProgress * MAX_ARC_DEGREE)
        }
    }

    private inner class OvalShadow(shadowRadius: Int, private val mCircleDiameter: Int) : OvalShape() {
        private val mRadialGradient: RadialGradient
        private val mShadowPaint: Paint

        init {
            mShadowPaint = Paint()
            mShadowRadius = shadowRadius
            mRadialGradient = RadialGradient(
                (mCircleDiameter / 2).toFloat(), (mCircleDiameter / 2).toFloat(),
                mShadowRadius.toFloat(), intArrayOf(FILL_SHADOW_COLOR, Color.TRANSPARENT), null, Shader.TileMode.CLAMP
            )
            mShadowPaint.shader = mRadialGradient
        }

        override fun draw(canvas: Canvas, paint: Paint) {
            val viewWidth = this@MaterialRefreshView.width
            val viewHeight = this@MaterialRefreshView.height
            canvas.drawCircle(
                (viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), (mCircleDiameter / 2 + mShadowRadius).toFloat(),
                mShadowPaint
            )
            canvas.drawCircle(
                (viewWidth / 2).toFloat(),
                (viewHeight / 2).toFloat(),
                (mCircleDiameter / 2).toFloat(),
                paint
            )
        }
    }

    companion object {
        private val KEY_SHADOW_COLOR = 0x1E000000
        private val FILL_SHADOW_COLOR = 0x3D000000
        // PX
        private val X_OFFSET = 0f
        private val Y_OFFSET = 0f
        private val SHADOW_RADIUS = 3.5f
        private val SHADOW_ELEVATION = 4


        private val MAX_ARC_DEGREE = 330
        private val ANIMATION_DURATION = 888
        private val DEFAULT_START_DEGREES = 285
        private val DEFAULT_STROKE_WIDTH = 2
    }
}