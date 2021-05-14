package com.jarvis.newbegining

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import kotlin.math.min

class CustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var SQUARE_SIZE =100F
    private val SCALE_RATIO :Float = 5/8f // width to Height
    private val MINIMUM_WIDTH = 120F
    private val MINIMUM_HEIGHT = MINIMUM_WIDTH*SCALE_RATIO
    private val GUAGE_TEXT_SIZE :Float = 50F
    private val CENTER_VALUE_TEXT_SIZE :Float = GUAGE_TEXT_SIZE.times(5)
    private val CENTER_DESCRIPTION_TEXT_SIZE :Float = GUAGE_TEXT_SIZE.times(2)
    private val FULL_RECT_OFFSSET_MULTIPLIER :Float = 0.1f

    private var labelStart :String
    private var labelEnd :String
    private var labelCenter :String
    private var labelCenterDesc :String
    private var progress :Int
    private var progressSweep :Float = 0f
    private var progressColor :Int
    private var progressTrackColor :Int
    private var progressArcThickness :Float
    private var progressTrackThickness :Float
    private var progressThumbSize :Float
    private var labelStartTextSize :Int
    private var labelEndTextSize :Int
    private var labelCenterTextSize :Int
    private var labelCenterDescTextSize :Int
    private var isThumbVisible :Boolean
        private var mThumb :Drawable?

    private var mRect :RectF
    private var mArcRect :RectF
    private var mArcFullRect :RectF

    private var mPaint :Paint
    private var mCirclePaint :Paint
    private var mArcPaint :Paint
    private var mArcPaintTrack :Paint
    private var mArcPaintBG :Paint
    private var mArcFullRectPaint :Paint
    private var starttextPaintGauge :Paint
    private var endtextPaintGauge :Paint
    private var textPaintCenter :Paint
    private var textPaintCenterDesc :Paint
    private var viewWidth: Int =120
    private var viewHeight: Int =75

    init {
        minimumHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,MINIMUM_HEIGHT,resources.displayMetrics).toInt()
        minimumWidth=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,MINIMUM_WIDTH,resources.displayMetrics).toInt()
        var a : TypedArray = context.obtainStyledAttributes(attrs,R.styleable.CustomProgressView)
        labelStart =a.getString(R.styleable.CustomProgressView_label_start) ?: "$0"
        labelEnd =a.getString(R.styleable.CustomProgressView_label_end) ?: "$0"
        labelCenter =a.getString(R.styleable.CustomProgressView_label_center) ?: "$0"
        labelCenterDesc =a.getString(R.styleable.CustomProgressView_label_center_desc) ?: "Text"
        progress = a.getInt(R.styleable.CustomProgressView_progress,0)
        progressColor = a.getColor(R.styleable.CustomProgressView_progressColor,Color.parseColor("#002D72"))
        progressTrackColor = a.getColor(R.styleable.CustomProgressView_progressTrackColor,Color.parseColor("#F1F1F1"))
        progressArcThickness = a.getDimension(R.styleable.CustomProgressView_progressArcThickness,60F)
        progressTrackThickness = a.getDimension(R.styleable.CustomProgressView_progressTrackThickness,60F)
        progressThumbSize= a.getDimension(R.styleable.CustomProgressView_progressThumbSize,50F)
        labelStartTextSize = a.getDimensionPixelSize(R.styleable.CustomProgressView_label_start_textSize,70)
        labelEndTextSize = a.getDimensionPixelSize(R.styleable.CustomProgressView_label_end_textSize,70)
        labelCenterTextSize = a.getDimensionPixelSize(R.styleable.CustomProgressView_label_center_textSize,180)
        labelCenterDescTextSize = a.getDimensionPixelSize(R.styleable.CustomProgressView_label_center_desc_textSize,98)
        isThumbVisible = a.getBoolean(R.styleable.CustomProgressView_isThumbVisible,true)
        mThumb = a.getDrawable(R.styleable.CustomProgressView_progressThumbDrawable)?:ContextCompat.getDrawable(context,R.drawable.ic_run_circle)
        var thumbHalfH =mThumb?.intrinsicHeight
        var thumbHalfW =mThumb?.intrinsicHeight
        mThumb?.setBounds(-thumbHalfW!!,-thumbHalfH!!,thumbHalfW,thumbHalfH)

        mRect =RectF()
        mArcRect =RectF()
        mArcFullRect =RectF()

        mPaint= Paint()
        mCirclePaint= Paint()
        mArcPaint= Paint()
        mArcPaintTrack= Paint()
        mArcPaintBG= Paint()
        mArcFullRectPaint = Paint()

        mPaint.color = Color.BLUE
        mCirclePaint.color= Color.GREEN

        mArcPaint.color= progressColor
        mArcPaint.style=Paint.Style.STROKE
        mArcPaint.strokeCap= Paint.Cap.ROUND
        mArcPaint.strokeWidth= progressArcThickness
        mArcPaint.isAntiAlias =true
        //mArcPaint.shader = LinearGradient(0f,0f,0f,height.toFloat(),Color.parseColor("#002D72"),Color.parseColor("#00BBF2"),Shader.TileMode.MIRROR)


        mArcPaintBG.color= Color.RED

        mArcPaintTrack.color= progressTrackColor
        mArcPaintTrack.style = Paint.Style.STROKE
        mArcPaintTrack.strokeCap = Paint.Cap.ROUND
        mArcPaintTrack.strokeWidth = progressTrackThickness
        mArcPaintTrack.isAntiAlias =true

        mArcFullRectPaint.color = Color.YELLOW

        starttextPaintGauge = Paint().apply {
            isAntiAlias =true
            textAlign = Paint.Align.CENTER
            color = Color.parseColor("#666666")
            textSize = labelStartTextSize.toFloat()
        }
        endtextPaintGauge = Paint().apply {
            isAntiAlias =true
            textAlign = Paint.Align.CENTER
            color = Color.parseColor("#666666")
            textSize = labelStartTextSize.toFloat()
        }
        textPaintCenterDesc = Paint().apply {
            isAntiAlias =true
            textAlign = Paint.Align.CENTER
            color = Color.parseColor("#666666")
            textSize = labelCenterTextSize.toFloat()
        }
        textPaintCenter = Paint().apply {
            isAntiAlias =true
            textAlign = Paint.Align.CENTER
            color = Color.BLACK
            textSize = labelCenterTextSize.toFloat()
        }

       /* setLabelStart(labelStart)
        setLabelEnd(labelEnd)
        setLabelCenter(labelCenter)
        setLabelCenterDesc(labelCenterDesc)
        setProgress(progress)
        setProgressColor(progressColor)
        setProgressTrackColor(progressTrackColor)*/



    }

    fun setLabelStart(value :String) {
        labelStart = value
        postInvalidate()
    }
    fun setLabelEnd(value :String) {
        labelEnd = value
        postInvalidate()
    }
    fun setLabelCenter(value :String) {
        labelCenter = value
        postInvalidate()
    }
    fun setLabelCenterDesc(value :String) {
        labelCenterDesc = value
        postInvalidate()
    }
    fun setProgress(value :Int) {
        //Convert 0 to 100 into 0f to 180F
        progressSweep = convertPercentageToSweepAngle(value)
        postInvalidate()
    }
    fun setProgressColor(value: Int){
        mArcPaint.color= value
        postInvalidate()
    }
    fun setProgressTrackColor(value: Int){
        mArcPaintTrack.color= value
        postInvalidate()
    }
    fun setProgressArcThickness(value: Float){
        progressArcThickness= value
        postInvalidate()
    }

    fun setProgressTrackThickness(value: Float){
        progressTrackThickness= value
        postInvalidate()
    }

    fun setThumbSize(value: Float){
        progressThumbSize= value
        postInvalidate()
    }

    fun setLabelStartTextSize(value: Int){
        labelStartTextSize= value
        postInvalidate()
    }
    fun setLabelEndTextSize(value: Int){
        labelEndTextSize= value
        postInvalidate()
    }
    fun setLabelCenterTextSize(value: Int){
        labelCenterTextSize= value
        postInvalidate()
    }

    fun isThumbVisible(value: Boolean){
        isThumbVisible= value
        postInvalidate()
    }

    fun setProgressThumbDrawable(value: Drawable){
        mThumb= value
        postInvalidate()
    }




    fun convertPercentageToSweepAngle(value: Int): Float{
        var result :Float
        if(value>100){ result= 180f}
        else if(value<0){result = 0f}
        else{result = (value*180f)/100f}
        return result
    }
    fun progressAnimation(progressVale :Int, progressDuration : Long, progressInterpolator : TimeInterpolator = AccelerateDecelerateInterpolator()): ValueAnimator{
        val anim = ObjectAnimator.ofInt(this,"progress",progressVale)
        anim.setDuration(progressDuration)
        anim.interpolator = progressInterpolator
        //anim.start()
        return anim
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //trial1(canvas)
        //trial2(canvas)
        //trial3(canvas)
        trial4(canvas)



    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var desiredWidth = suggestedMinimumWidth + paddingLeft +paddingRight
        var desiredHeight = suggestedMinimumWidth*SCALE_RATIO + paddingTop +paddingBottom
        if( MeasureSpec.getMode(heightMeasureSpec) ==MeasureSpec.AT_MOST){
            var calculatedWidth = measureDimension(desiredWidth,widthMeasureSpec)
            var calculatedHeight = calculatedWidth*SCALE_RATIO
            setMeasuredDimension(calculatedWidth,calculatedHeight.toInt())
        }else{
            setMeasuredDimension(measureDimension(desiredWidth,widthMeasureSpec),
                    measureDimension(desiredHeight.toInt(),heightMeasureSpec))
        }

        progressArcThickness = measureDimension(desiredWidth,widthMeasureSpec)*0.05f
        progressTrackThickness =measureDimension(desiredWidth,widthMeasureSpec)*0.05f
        progressThumbSize =  measureDimension(desiredWidth,widthMeasureSpec)*0.045f
        labelStartTextSize = (measureDimension(desiredWidth,widthMeasureSpec)*0.065f).toInt()
        labelEndTextSize = (measureDimension(desiredWidth,widthMeasureSpec)*0.065f).toInt()
        labelCenterTextSize = (measureDimension(desiredWidth,widthMeasureSpec)*0.15f).toInt()
        labelCenterDescTextSize= (measureDimension(desiredWidth,widthMeasureSpec)*0.08f).toInt()

    }
    fun swapColor(){
        mArcPaint.color =  Color.RED
        postInvalidate()
    }

    private fun trial4(canvas :Canvas?){
        //canvas?.drawColor(Color.WHITE)
        //var offset = 16f
        //var arcoffset = 100f


        viewWidth =width
        viewHeight =height
        //SQUARE_SIZE= minOf(viewWidth,viewHeight).toFloat()
        //mRect.set(viewWidth/2 - SQUARE_SIZE/2 +offset,viewHeight/2 - SQUARE_SIZE/2 +offset,(viewWidth/2 +SQUARE_SIZE/2 - offset).toFloat(),(viewHeight/2 +SQUARE_SIZE/2 -offset).toFloat())
        //mArcRect.set(viewWidth/2 - SQUARE_SIZE/2 +offset,viewHeight/2 - arcoffset,(viewWidth/2 +SQUARE_SIZE/2 - offset).toFloat(),(viewHeight/2 +SQUARE_SIZE -offset).toFloat())
        var fullRectOffset = viewWidth*FULL_RECT_OFFSSET_MULTIPLIER
        mArcFullRect.set(0f+fullRectOffset,0f+fullRectOffset,(viewWidth - fullRectOffset).toFloat(),(viewWidth - fullRectOffset).toFloat())
        //var fullViewRect = RectF()

       // mArcPaint.strokeWidth=  viewWidth*0.05f

        mArcPaint.strokeWidth= progressArcThickness

        //mArcPaintTrack.strokeWidth = viewWidth*0.05f

        mArcPaintTrack.strokeWidth = progressTrackThickness
        //textPaintGauge.textSize = viewWidth*0.065f
        starttextPaintGauge.textSize = labelStartTextSize.toFloat()
        endtextPaintGauge.textSize = labelEndTextSize.toFloat()
        //textPaintCenterDesc.textSize = viewWidth*0.08f
        textPaintCenterDesc.textSize = labelCenterDescTextSize.toFloat()
        textPaintCenter.textSize = labelCenterTextSize.toFloat()
        //fullViewRect.set(0f,0f,viewWidth.toFloat(),viewHeight.toFloat())
        //mArcRect.set(viewWidth/2 - SQUARE_SIZE/2,(viewHeight/2).toFloat() ,(viewWidth/2 +SQUARE_SIZE/2).toFloat(),(viewHeight/2 +SQUARE_SIZE ).toFloat())
        //canvas?.drawRect(mRect,mPaint)
        //canvas?.drawRect(mArcRect,mArcPaintBG)

        //canvas?.drawRect(mArcFullRect,mArcFullRectPaint)

        //canvas?.drawCircle((viewWidth).toFloat(),(viewHeight/2).toFloat(),SQUARE_SIZE/2,mArcPaintTrack)
        //canvas?.drawCircle((viewWidth/2).toFloat(),(viewHeight).toFloat(),SQUARE_SIZE/2,mArcPaintTrack)

        //canvas?.drawArc(mArcRect,180f,180f,false,mArcPaintTrack)
        //canvas?.drawArc(mArcRect,180f,135f,false,mArcPaint)
        mThumb?.setBounds(-progressThumbSize.toInt(),-progressThumbSize.toInt(),progressThumbSize.toInt(),progressThumbSize.toInt())

        canvas?.drawArc(mArcFullRect,180f,180f,false,mArcPaintTrack)
        canvas?.drawArc(mArcFullRect,180f,progressSweep,false,mArcPaint)
        canvas?.drawText(labelStart,0+fullRectOffset, viewWidth/2+fullRectOffset,starttextPaintGauge)
        canvas?.drawText(labelEnd,viewWidth-fullRectOffset, viewWidth/2+fullRectOffset,endtextPaintGauge)
        canvas?.drawText(labelCenter,viewWidth/2f, viewWidth/2f - fullRectOffset*0.5f,textPaintCenter)
        canvas?.drawText(labelCenterDesc,viewWidth/2f, viewWidth/2f +fullRectOffset/2f ,textPaintCenterDesc)
        var radius = (viewWidth-2*fullRectOffset)/2
        var thumbx = (radius * Math.cos(Math.toRadians(progressSweep.toDouble())))
        var thumby = (radius * Math.sin(Math.toRadians(progressSweep.toDouble())))
        if(isThumbVisible){
            canvas?.save()
            canvas?.translate(viewWidth/2 - thumbx.toFloat(), viewWidth/2 - thumby.toFloat())
            mThumb?.draw(canvas!!)
            canvas?.restore()
        }



    }
   /* private fun trial3(canvas :Canvas?){
        //canvas?.drawColor(Color.WHITE)
        //var offset = 16f
        //var arcoffset = 100f


        viewWidth =width
        viewHeight =height
        //SQUARE_SIZE= minOf(viewWidth,viewHeight).toFloat()
        //mRect.set(viewWidth/2 - SQUARE_SIZE/2 +offset,viewHeight/2 - SQUARE_SIZE/2 +offset,(viewWidth/2 +SQUARE_SIZE/2 - offset).toFloat(),(viewHeight/2 +SQUARE_SIZE/2 -offset).toFloat())
        //mArcRect.set(viewWidth/2 - SQUARE_SIZE/2 +offset,viewHeight/2 - arcoffset,(viewWidth/2 +SQUARE_SIZE/2 - offset).toFloat(),(viewHeight/2 +SQUARE_SIZE -offset).toFloat())
        var fullRectOffset = viewWidth*FULL_RECT_OFFSSET_MULTIPLIER
        mArcFullRect.set(0f+fullRectOffset,0f+fullRectOffset,(viewWidth - fullRectOffset).toFloat(),(viewWidth - fullRectOffset).toFloat())
        //var fullViewRect = RectF()
         mArcPaint.strokeWidth=  viewWidth*0.05f
        mArcPaintTrack.strokeWidth = viewWidth*0.05f
        textPaintGauge.textSize = viewWidth*0.065f
        textPaintCenterDesc.textSize = viewWidth*0.08f
        textPaintCenter.textSize = viewWidth*0.15f
        //fullViewRect.set(0f,0f,viewWidth.toFloat(),viewHeight.toFloat())
        //mArcRect.set(viewWidth/2 - SQUARE_SIZE/2,(viewHeight/2).toFloat() ,(viewWidth/2 +SQUARE_SIZE/2).toFloat(),(viewHeight/2 +SQUARE_SIZE ).toFloat())
        //canvas?.drawRect(mRect,mPaint)
        //canvas?.drawRect(mArcRect,mArcPaintBG)

        //canvas?.drawRect(mArcFullRect,mArcFullRectPaint)

        //canvas?.drawCircle((viewWidth).toFloat(),(viewHeight/2).toFloat(),SQUARE_SIZE/2,mArcPaintTrack)
        //canvas?.drawCircle((viewWidth/2).toFloat(),(viewHeight).toFloat(),SQUARE_SIZE/2,mArcPaintTrack)

        //canvas?.drawArc(mArcRect,180f,180f,false,mArcPaintTrack)
        //canvas?.drawArc(mArcRect,180f,135f,false,mArcPaint)
        mThumb?.setBounds(-(viewWidth*0.045).toInt(),-(viewWidth*0.045).toInt(),(viewWidth*0.045).toInt(),(viewWidth*0.045).toInt())

        canvas?.drawArc(mArcFullRect,180f,180f,false,mArcPaintTrack)
        canvas?.drawArc(mArcFullRect,180f,progressSweep,false,mArcPaint)
        canvas?.drawText(labelStart,0+fullRectOffset, viewWidth/2+fullRectOffset,starttextPaintGauge)
        canvas?.drawText(labelEnd,viewWidth-fullRectOffset, viewWidth/2+fullRectOffset,endtextPaintGauge)
        canvas?.drawText(labelCenter,viewWidth/2f, viewWidth/2f - fullRectOffset*0.5f,textPaintCenter)
        canvas?.drawText(labelCenterDesc,viewWidth/2f, viewWidth/2f +fullRectOffset/2f ,textPaintCenterDesc)
        var radius = (viewWidth-2*fullRectOffset)/2
        var thumbx = (radius * Math.cos(Math.toRadians(progressSweep.toDouble())))
        var thumby = (radius * Math.sin(Math.toRadians(progressSweep.toDouble())))
        canvas?.save()
        canvas?.translate(viewWidth/2 - thumbx.toFloat(), viewWidth/2 - thumby.toFloat())
        mThumb?.draw(canvas!!)
        canvas?.restore()


    }*/
   /* private fun trial2(canvas :Canvas?){
        canvas?.drawColor(Color.WHITE)
        var offset = 16f

        var arcoffset = 100f
        viewWidth =width
        viewHeight =height
        SQUARE_SIZE= minOf(viewWidth,viewHeight).toFloat()
        mRect.set(viewWidth/2 - SQUARE_SIZE/2 +offset,viewHeight/2 - SQUARE_SIZE/2 +offset,(viewWidth/2 +SQUARE_SIZE/2 - offset).toFloat(),(viewHeight/2 +SQUARE_SIZE/2 -offset).toFloat())
        mArcRect.set(viewWidth/2 - SQUARE_SIZE/2 +offset,viewHeight/2 - arcoffset,(viewWidth/2 +SQUARE_SIZE/2 - offset).toFloat(),(viewHeight/2 +SQUARE_SIZE -offset).toFloat())
        var fullRectOffset = viewWidth*0.07f
        mArcFullRect.set(0f+fullRectOffset,0f+fullRectOffset,(viewWidth - fullRectOffset).toFloat(),(viewWidth - 2*fullRectOffset).toFloat())
        var fullViewRect = RectF()
        mArcPaint.strokeWidth=  viewWidth*0.05f
        mArcPaintTrack.strokeWidth = viewWidth*0.05f
        textPaintGauge.textSize = viewWidth*0.05f
        textPaintCenterDesc.textSize = viewWidth*0.08f
        textPaintCenter.textSize = viewWidth*0.15f
        fullViewRect.set(0f,0f,viewWidth.toFloat(),viewHeight.toFloat())
        //mArcRect.set(viewWidth/2 - SQUARE_SIZE/2,(viewHeight/2).toFloat() ,(viewWidth/2 +SQUARE_SIZE/2).toFloat(),(viewHeight/2 +SQUARE_SIZE ).toFloat())
        //canvas?.drawRect(mRect,mPaint)
        //canvas?.drawRect(mArcRect,mArcPaintBG)

        canvas?.drawRect(mArcFullRect,mArcFullRectPaint)

        //canvas?.drawCircle((viewWidth).toFloat(),(viewHeight/2).toFloat(),SQUARE_SIZE/2,mArcPaintTrack)
        //canvas?.drawCircle((viewWidth/2).toFloat(),(viewHeight).toFloat(),SQUARE_SIZE/2,mArcPaintTrack)

        //canvas?.drawArc(mArcRect,180f,180f,false,mArcPaintTrack)
        //canvas?.drawArc(mArcRect,180f,135f,false,mArcPaint)

        canvas?.drawArc(mArcFullRect,180f,180f,false,mArcPaintTrack)
        canvas?.drawArc(mArcFullRect,180f,135f,false,mArcPaint)
        canvas?.drawText("$1000",0+fullRectOffset, viewWidth/2+5*fullRectOffset/4,textPaintGauge)
        canvas?.drawText("$7500",viewWidth-fullRectOffset, viewWidth/2+5*fullRectOffset/4,textPaintGauge)
        canvas?.drawText("$2500.00",viewWidth/2f, viewWidth/2f - fullRectOffset,textPaintCenter)
        canvas?.drawText("Left to Spend",viewWidth/2f, viewWidth/2f +fullRectOffset/2f ,textPaintCenterDesc)


    }*/

    /*private fun trial1(canvas :Canvas?){
        canvas?.drawColor(Color.WHITE)
        var offset = 16f
        var arcoffset = 100f
        viewWidth =width
        viewHeight =height
        SQUARE_SIZE= minOf(viewWidth,viewHeight).toFloat()
        mRect.set(viewWidth/2 - SQUARE_SIZE/2 +offset,viewHeight/2 - SQUARE_SIZE/2 +offset,(viewWidth/2 +SQUARE_SIZE/2 - offset).toFloat(),(viewHeight/2 +SQUARE_SIZE/2 -offset).toFloat())
        mArcRect.set(viewWidth/2 - SQUARE_SIZE/2 +offset,viewHeight/2 - arcoffset,(viewWidth/2 +SQUARE_SIZE/2 - offset).toFloat(),(viewHeight/2 +SQUARE_SIZE -offset).toFloat())

        //mArcRect.set(viewWidth/2 - SQUARE_SIZE/2,(viewHeight/2).toFloat() ,(viewWidth/2 +SQUARE_SIZE/2).toFloat(),(viewHeight/2 +SQUARE_SIZE ).toFloat())
        canvas?.drawRect(mRect,mPaint)
        canvas?.drawRect(mArcRect,mArcPaintBG)
        //canvas?.drawCircle((viewWidth).toFloat(),(viewHeight/2).toFloat(),SQUARE_SIZE/2,mArcPaintTrack)
        //canvas?.drawCircle((viewWidth/2).toFloat(),(viewHeight).toFloat(),SQUARE_SIZE/2,mArcPaintTrack)
        canvas?.drawArc(mArcRect,180f,180f,false,mArcPaintTrack)
        canvas?.drawArc(mArcRect,180f,135f,false,mArcPaint)

    }*/
    private fun measureDimension(desiredSize: Int,measureSpec: Int):Int{
        var result :Int
        var specMode = MeasureSpec.getMode(measureSpec)
        var specSize = MeasureSpec.getSize(measureSpec)

        when(specMode){
            MeasureSpec.EXACTLY ->{
                result = specSize
            }
            else ->{
                result = desiredSize
                if(specMode== MeasureSpec.AT_MOST){
                    result = minOf(result,specSize)
                }

            }
        }
        if(result < desiredSize){
            Log.e("CustomView","The Vieew is too small, the content might get cut")
        }
        return result
    }

}