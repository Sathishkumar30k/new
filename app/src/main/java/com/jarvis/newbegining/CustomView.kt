 package com.jarvis.newbegining

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.text.LineBreaker
import android.os.Build
import android.text.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.DimenRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.withTranslation

 @RequiresApi(Build.VERSION_CODES.P)
 class CustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.myViewStyle
) : View(context, attrs, defStyleAttr) {
    private val SCALE_RATIO :Float = 1/2f // width to Height
    private val MINIMUM_ARC_WIDTH = 141F
    private val MINIMUM_ARC_HEIGHT = MINIMUM_ARC_WIDTH * SCALE_RATIO
    private val MINIMUM_WIDTH = MINIMUM_ARC_WIDTH
    private val MINIMUM_HEIGHT = MINIMUM_ARC_HEIGHT

    private var TEXT_SIZE_FACTOR =0.05f

    private val FULL_RECT_OFFSSET_MULTIPLIER :Float = 0.1f
    private var progressArcThicknessRatio = 0.05f
    private var progressTrackThicknessRatio = 0.05f
    private var progressThumbSizeRatio =  0.045f
    /*private var labelStartTextSizeRatio = 0.065f
    private var labelEndTextSizeRatio = 0.065f
    private var labelCenterTextSizeRatio = 0.14f
    private var labelCenterDescTextSizeRatio= 0.07f*/
    private var viewOffset= 0
    private var bottomLabelOffset= 0F
    private var centerLabelOffset= 0F
    private var centerLabelDescOffset= 0F




    private var labelStart :String
    private var labelEnd :String
    private var labelCenter :String
    private var labelCenterDesc :String
   // private var progress :Int
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

   /* private var generatedLabelStartTextSize :Int = 0
    private var generatedLabelEndTextSize :Int = 0
    private var generatedLabelCenterTextSize :Int =0
    private var generatedLabelCenterDescTextSize :Int =0*/

    private var mRect :RectF
    private var mArcRect :RectF
    private var mArcFullRect :RectF
    private var labelStartBounds :Rect
    private var labelEndBounds :Rect

    private var mPaint :Paint
    private var mCirclePaint :Paint
    private var mArcPaint :Paint
    private var mArcPaintTrack :Paint
    private var mArcPaintBG :Paint
    private var mArcFullRectPaint :Paint
    private var starttextPaintGauge :TextPaint
    private var endtextPaintGauge :TextPaint
    private var textPaintCenter :TextPaint
    private var textPaintCenterDesc :TextPaint
    private var viewWidth: Int =120
    private var arcviewWidth: Int =120
    private var viewHeight: Int =75
    private var arcviewHeight: Int =75
    private lateinit var startLayout: DynamicLayout
    private lateinit var endLayout: DynamicLayout
    private lateinit var centerLayout: DynamicLayout
    private lateinit var centerDescLayout: DynamicLayout


    init {
        var a : TypedArray = context.obtainStyledAttributes(attrs,R.styleable.CustomProgressView)
        try {
            val default_padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16f,resources.displayMetrics).toInt()
            //setPadding(default_padding,default_padding,default_padding,default_padding)
            minimumHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,MINIMUM_HEIGHT,resources.displayMetrics).toInt()
            minimumWidth=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,MINIMUM_WIDTH,resources.displayMetrics).toInt()

            labelStart =a.getString(R.styleable.CustomProgressView_label_start) ?: "$0.0"
            labelEnd =a.getString(R.styleable.CustomProgressView_label_end) ?: "$0.0"
            labelCenter =a.getString(R.styleable.CustomProgressView_label_center) ?: "$0.0"
            labelCenterDesc =a.getString(R.styleable.CustomProgressView_label_center_desc) ?: "Text"
            progressSweep = convertPercentageToSweepAngle(a.getInt(R.styleable.CustomProgressView_progress,0))
            progressColor = a.getColor(R.styleable.CustomProgressView_progressColor,Color.parseColor("#002D72"))
            progressTrackColor = a.getColor(R.styleable.CustomProgressView_progressTrackColor,Color.parseColor("#F1F1F1"))
            progressArcThickness = a.getDimension(R.styleable.CustomProgressView_progressArcThickness,resources.getDimension(R.dimen.dimen_6))
            progressTrackThickness = a.getDimension(R.styleable.CustomProgressView_progressTrackThickness,resources.getDimension(R.dimen.dimen_6))
            bottomLabelOffset = a.getDimension(R.styleable.CustomProgressView_label_bottom_offset,resources.getDimension(R.dimen.dimen_10))
            progressThumbSize= a.getDimension(R.styleable.CustomProgressView_progressThumbSize,resources.getDimension(R.dimen.dimen_14))
            labelStartTextSize = a.getDimensionPixelSize(R.styleable.CustomProgressView_label_start_textSize,resources.getDimension(R.dimen.dimen_18sp).toInt())
            labelEndTextSize = a.getDimensionPixelSize(R.styleable.CustomProgressView_label_end_textSize,resources.getDimension(R.dimen.dimen_18sp).toInt())
            labelCenterTextSize = a.getDimensionPixelSize(R.styleable.CustomProgressView_label_center_textSize,resources.getDimension(R.dimen.dimen_24sp).toInt())
            labelCenterDescTextSize = a.getDimensionPixelSize(R.styleable.CustomProgressView_label_center_desc_textSize,resources.getDimension(R.dimen.dimen_12sp).toInt())
            isThumbVisible = a.getBoolean(R.styleable.CustomProgressView_isThumbVisible,true)
            centerLabelOffset=resources.getDimension(R.dimen.dimen_18)
            centerLabelDescOffset=resources.getDimension(R.dimen.dimen_8)
            mThumb = a.getDrawable(R.styleable.CustomProgressView_progressThumbDrawable)?:ContextCompat.getDrawable(context,R.drawable.ic_run_circle)
            var thumbHalfH =mThumb?.intrinsicHeight
            var thumbHalfW =mThumb?.intrinsicHeight
            mThumb?.setBounds(-thumbHalfW!!,-thumbHalfH!!,thumbHalfW,thumbHalfH)

            mRect =RectF()
            mArcRect =RectF()
            mArcFullRect =RectF()
            labelStartBounds = Rect()
            labelEndBounds = Rect()

            mPaint= Paint()
            mCirclePaint= Paint()
            mArcPaint= Paint()
            mArcPaintTrack= Paint()
            mArcPaintBG= Paint()
            mArcFullRectPaint = Paint()

            //mPaint.color = Color.BLUE
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

            starttextPaintGauge = TextPaint().apply {
                isAntiAlias =true
                textAlign = Paint.Align.LEFT
                color = Color.parseColor("#666666")
                textSize = labelStartTextSize.toFloat()
            }
            endtextPaintGauge = TextPaint().apply {
                isAntiAlias =true
                textAlign = Paint.Align.RIGHT
                color = Color.parseColor("#666666")
                textSize = labelEndTextSize.toFloat()
            }
            textPaintCenterDesc = TextPaint().apply {
                isAntiAlias =true
                textAlign = Paint.Align.CENTER
                color = Color.parseColor("#666666")
                textSize = labelCenterDescTextSize.toFloat()
            }
            textPaintCenter = TextPaint().apply {
                isAntiAlias =true
                textAlign = Paint.Align.CENTER
                color = Color.BLACK
                textSize = labelCenterTextSize.toFloat()
            }

            starttextPaintGauge.getTextBounds(labelStart,0, labelStart.length,labelStartBounds)
            endtextPaintGauge.getTextBounds(labelEnd,0, labelEnd.length,labelEndBounds)


            /* setLabelStart(labelStart)
             setLabelEnd(labelEnd)
             setLabelCenter(labelCenter)
             setLabelCenterDesc(labelCenterDesc)
             setProgress(progress)
             setProgressColor(progressColor)
             setProgressTrackColor(progressTrackColor)*/

            startLayout = DynamicLayout.Builder
                .obtain(labelStart,starttextPaintGauge,starttextPaintGauge.measureText(labelStart).toInt())
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .build()
            endLayout =DynamicLayout.Builder
                .obtain(labelEnd,endtextPaintGauge,endtextPaintGauge.measureText(labelEnd).toInt())
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .build()
            centerLayout =DynamicLayout.Builder
                .obtain(labelCenter,textPaintCenter,textPaintCenter.measureText(labelCenter).toInt())
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .build()
            centerDescLayout =DynamicLayout.Builder
                .obtain(labelCenterDesc,textPaintCenterDesc,textPaintCenterDesc.measureText(labelCenterDesc).toInt())
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .build()

        }finally {
            a.recycle()
        }


    }

    fun setLabelStart(value :String) {
        labelStart = value
        requestLayout()
        postInvalidate()
    }
    fun setLabelEnd(value :String) {
        labelEnd = value
        requestLayout()
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
    fun setProgressColor (value: Int){
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

    fun setLabelStartTextSize(@DimenRes value: Int){
        labelStartTextSize= resources.getDimension(value).toInt()
        //starttextPaintGauge.textSize = labelStartTextSize.toFloat()
        requestLayout()
        postInvalidate()
    }
     fun setLabelStartTextSize( value: Float){
         labelStartTextSize= (value *resources.displayMetrics.density).toInt()
         //starttextPaintGauge.textSize = value *resources.displayMetrics.density
         requestLayout()
         postInvalidate()
     }
    fun setLabelEndTextSize(@DimenRes value: Int){
        labelEndTextSize= resources.getDimension(value).toInt()
        requestLayout()
        postInvalidate()
    }
    fun setLabelCenterTextSize(@DimenRes value: Int){
        labelCenterTextSize= resources.getDimension(value).toInt()
        requestLayout()
        postInvalidate()
    }
    fun setLabelCenterDescTextSize(@DimenRes value: Int){
        labelCenterDescTextSize= resources.getDimension(value).toInt()
        requestLayout()
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

    fun progressAnimation(progressVale :Float, progressDuration : Long, progressInterpolator : TimeInterpolator = AccelerateDecelerateInterpolator()): ValueAnimator{
        val anim = ValueAnimator.ofFloat(progressVale)
        anim.setDuration(progressDuration)
        anim.interpolator = progressInterpolator
        
        anim.addUpdateListener { setProgress(progressVale.toInt()) }
        //anim.start()
        return anim
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //trial1(canvas)
        //trial2(canvas)
        //trial3(canvas)
        //trial4(canvas)
        trial5(canvas)



    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var StartLabelTextHeight = (-starttextPaintGauge.ascent()+starttextPaintGauge.descent()).toInt()
        var endLabelTextHeight = (-endtextPaintGauge.ascent()+endtextPaintGauge.descent()).toInt()
        var endLabelTextwidth = endtextPaintGauge.measureText(labelEnd)
        var StartLabelTextwidth = starttextPaintGauge.measureText(labelStart)

        var desiredWidth = suggestedMinimumWidth + paddingLeft +paddingRight
        if(endLabelTextwidth+StartLabelTextwidth+viewOffset+paddingStart+paddingEnd >desiredWidth){
            desiredWidth += (endLabelTextwidth + StartLabelTextwidth + viewOffset+paddingStart+paddingEnd  - desiredWidth).toInt()
        }
        var desiredHeight = (desiredWidth-paddingStart-paddingEnd)*SCALE_RATIO + paddingTop +paddingBottom + bottomLabelOffset +maxOf(StartLabelTextHeight,endLabelTextHeight)

        if( MeasureSpec.getMode(heightMeasureSpec) ==MeasureSpec.AT_MOST){
            var calculatedWidth = measureDimension(desiredWidth,widthMeasureSpec)
            if(endLabelTextwidth+StartLabelTextwidth+viewOffset+paddingStart+paddingEnd  >calculatedWidth){
                calculatedWidth += (endLabelTextwidth + StartLabelTextwidth + viewOffset+paddingStart+paddingEnd  - calculatedWidth).toInt()
            }
            var calculatedHeight = measureDimension((calculatedWidth-paddingStart-paddingEnd),widthMeasureSpec) *SCALE_RATIO + paddingTop +paddingBottom +bottomLabelOffset+maxOf(StartLabelTextHeight,endLabelTextHeight)

            setMeasuredDimension(calculatedWidth,calculatedHeight.toInt())
        }else{
            setMeasuredDimension(measureDimension(desiredWidth,widthMeasureSpec),
                    measureDimension(desiredHeight.toInt(),heightMeasureSpec))
        }


       /* progressArcThickness = measureDimension(desiredWidth,widthMeasureSpec)*progressArcThicknessRatio
        progressTrackThickness =measureDimension(desiredWidth,widthMeasureSpec)*progressTrackThicknessRatio
        progressThumbSize =  measureDimension(desiredWidth,widthMeasureSpec)*progressThumbSizeRatio*/

        /*generatedLabelStartTextSize = measureTextSize(measureDimension(desiredWidth, widthMeasureSpec),labelStartTextSize,labelStartTextSizeRatio)
        generatedLabelEndTextSize =  measureTextSize(measureDimension(desiredWidth,widthMeasureSpec),labelEndTextSize,labelEndTextSizeRatio)
        generatedLabelCenterTextSize =  measureTextSize(measureDimension(desiredWidth,widthMeasureSpec),labelCenterTextSize,labelCenterTextSizeRatio)
        generatedLabelCenterDescTextSize=  measureTextSize(measureDimension(desiredWidth,widthMeasureSpec),labelCenterDescTextSize,labelCenterDescTextSizeRatio)
*/

    }

    fun isTextOverlapping(desiredWidth :Int): Boolean{
        return (labelStartBounds.width()+ labelEndBounds.width() + paddingLeft +paddingRight )>desiredWidth
    }
    fun swapColor(){
        mArcPaint.color =  Color.RED
        postInvalidate()
    }
    fun measureTextSize(viewWidth: Int,labelTextSize :Int, defaultRatio :Float): Int{
        if(labelTextSize !=-1){
            return labelTextSize
        }
        return (viewWidth*defaultRatio).toInt()
    }

    private fun trial5(canvas :Canvas?){
        //canvas?.drawColor(Color.WHITE)
        //var offset = 16f
        //var arcoffset = 100f




        viewWidth =width
        viewHeight =height

        arcviewWidth = viewWidth
        arcviewHeight = viewHeight
        //SQUARE_SIZE= minOf(viewWidth,viewHeight).toFloat()
        //mRect.set(viewWidth/2 - SQUARE_SIZE/2 +offset,viewHeight/2 - SQUARE_SIZE/2 +offset,(viewWidth/2 +SQUARE_SIZE/2 - offset).toFloat(),(viewHeight/2 +SQUARE_SIZE/2 -offset).toFloat())
        //mArcRect.set(viewWidth/2 - SQUARE_SIZE/2 +offset,viewHeight/2 - arcoffset,(viewWidth/2 +SQUARE_SIZE/2 - offset).toFloat(),(viewHeight/2 +SQUARE_SIZE -offset).toFloat())
        var fullRectOffset = viewWidth*FULL_RECT_OFFSSET_MULTIPLIER
        //textOffset = fullRectOffset/2
        viewOffset = maxOf(paddingStart,paddingEnd,paddingTop)
        // mArcFullRect.set(0f+fullRectOffset,0f+fullRectOffset,(viewWidth - fullRectOffset).toFloat(),(viewWidth - fullRectOffset).toFloat())
        //mArcRect.set(0f+fullRectOffset,0f+fullRectOffset,(arcviewWidth - fullRectOffset).toFloat(),(arcviewWidth - fullRectOffset).toFloat())
        mArcRect.set(0f+viewOffset,0f+viewOffset,(arcviewWidth-viewOffset).toFloat(),(arcviewWidth-viewOffset).toFloat())
        //var fullViewRect = RectF()

        // mArcPaint.strokeWidth=  viewWidth*0.05f

        mArcPaint.strokeWidth= progressArcThickness
        //progressArcThicknessRatio= progressArcThickness/viewWidth
        //mArcPaintTrack.strokeWidth = viewWidth*0.05f
        mArcPaintTrack.strokeWidth = progressTrackThickness
        //progressTrackThicknessRatio = progressTrackThickness/viewWidth
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
        mThumb?.setBounds(-progressThumbSize.toInt()/2,-progressThumbSize.toInt()/2,progressThumbSize.toInt()/2,progressThumbSize.toInt()/2)
        //progressThumbSizeRatio = progressThumbSize/viewWidth
        //canvas?.drawRect(mArcRect,mPaint)
        starttextPaintGauge.getTextBounds(labelStart,0, labelStart.length,labelStartBounds)
        endtextPaintGauge.getTextBounds(labelEnd,0, labelEnd.length,labelEndBounds)
        /*var startAdj = 0f
        var endAdj = 0f
        if(labelStartBounds.width() > 175){
            startAdj = labelStartBounds.width()-fullRectOffset
        }
        if(labelEndBounds.width() > 175){
            endAdj = labelEndBounds.width()-fullRectOffset
        }*/
        //TEXT_SIZE_FACTOR= maxOf(labelStartBounds.height(),labelEndBounds.height()).toFloat()
        //mArcRect.set(0f+fullRectOffset,0f+fullRectOffset,(arcviewWidth - fullRectOffset).toFloat(),(arcviewWidth - fullRectOffset).toFloat())
        //canvas?.drawText(labelStart,0+viewOffset*0.5f, viewWidth/2f+labelStartBounds.height()+bottomLabelOffset,starttextPaintGauge)
        //canvas?.drawText(labelEnd,viewWidth-viewOffset*0.5f, viewWidth/2f+labelEndBounds.height()+bottomLabelOffset,endtextPaintGauge)
        //canvas?.drawText(labelCenter,viewWidth/2f, viewWidth/4f+viewOffset ,textPaintCenter)
        //canvas?.drawMultilineText(labelCenter,textPaintCenter,labelEndBounds.width(),viewWidth/2f, viewWidth/2f - fullRectOffset*0.5f)
        //canvas?.drawText(labelCenterDesc,viewWidth/2f, viewWidth/2f ,textPaintCenterDesc)
        canvas?.clipRect(0,0,viewWidth,(viewWidth*SCALE_RATIO+bottomLabelOffset+maxOf(startLayout.height,endLayout.height)).toInt())
        canvas?.drawArc(mArcRect,180f,180f,false,mArcPaintTrack)
        canvas?.drawArc(mArcRect,180f,progressSweep,false,mArcPaint)
        var radius = (viewWidth-2*viewOffset)/2
        var thumbx = (radius * Math.cos(Math.toRadians(progressSweep.toDouble())))
        var thumby = (radius * Math.sin(Math.toRadians(progressSweep.toDouble())))
        if(isThumbVisible){
            canvas?.save()
            canvas?.translate(viewWidth/2 - thumbx.toFloat(), viewWidth/2 - thumby.toFloat())
            mThumb?.draw(canvas!!)
            canvas?.restore()

        }
        startLayout = DynamicLayout.Builder
            .obtain(labelStart,starttextPaintGauge,starttextPaintGauge.measureText(labelStart).toInt())
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .build()
        endLayout =DynamicLayout.Builder
            .obtain(labelEnd,endtextPaintGauge,endtextPaintGauge.measureText(labelEnd).toInt())
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .build()
        centerLayout =DynamicLayout.Builder
            .obtain(labelCenter,textPaintCenter,textPaintCenter.measureText(labelCenter).toInt())
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .build()
        centerDescLayout =DynamicLayout.Builder
            .obtain(labelCenterDesc,textPaintCenterDesc,textPaintCenterDesc.measureText(labelCenterDesc).toInt())
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .build()
            startLayout.draw(canvas!!,0+viewOffset*0.5f, viewWidth/2f+bottomLabelOffset)
            endLayout.draw(canvas!!,viewWidth-viewOffset*0.5f, viewWidth/2f+bottomLabelOffset)
            centerLayout.draw(canvas!!,viewWidth/2f, viewWidth/4f+viewOffset-centerLayout.height/2)
            centerDescLayout.draw(canvas!!,viewWidth/2f, viewWidth/2f-centerDescLayout.height/2 )





    }
     fun DynamicLayout.draw(canvas: Canvas,x :Float, y:Float){
         canvas.withTranslation(x,y){
             draw(this)
         }
     }

    /*private fun trial4(canvas :Canvas?){
        //canvas?.drawColor(Color.WHITE)
        //var offset = 16f
        //var arcoffset = 100f


        viewWidth =width
        viewHeight =height

        arcviewWidth = viewWidth
        arcviewHeight = viewHeight
        //SQUARE_SIZE= minOf(viewWidth,viewHeight).toFloat()
        //mRect.set(viewWidth/2 - SQUARE_SIZE/2 +offset,viewHeight/2 - SQUARE_SIZE/2 +offset,(viewWidth/2 +SQUARE_SIZE/2 - offset).toFloat(),(viewHeight/2 +SQUARE_SIZE/2 -offset).toFloat())
        //mArcRect.set(viewWidth/2 - SQUARE_SIZE/2 +offset,viewHeight/2 - arcoffset,(viewWidth/2 +SQUARE_SIZE/2 - offset).toFloat(),(viewHeight/2 +SQUARE_SIZE -offset).toFloat())
        var fullRectOffset = viewWidth*FULL_RECT_OFFSSET_MULTIPLIER
        textOffset = fullRectOffset/2
       // mArcFullRect.set(0f+fullRectOffset,0f+fullRectOffset,(viewWidth - fullRectOffset).toFloat(),(viewWidth - fullRectOffset).toFloat())
        mArcRect.set(0f+fullRectOffset,0f+fullRectOffset,(arcviewWidth - fullRectOffset).toFloat(),(arcviewWidth - fullRectOffset).toFloat())
        //var fullViewRect = RectF()

       // mArcPaint.strokeWidth=  viewWidth*0.05f

        mArcPaint.strokeWidth= progressArcThickness
        progressArcThicknessRatio= progressArcThickness/viewWidth
        //mArcPaintTrack.strokeWidth = viewWidth*0.05f
        mArcPaintTrack.strokeWidth = progressTrackThickness
        progressTrackThicknessRatio = progressTrackThickness/viewWidth
        //textPaintGauge.textSize = viewWidth*0.065f
        starttextPaintGauge.textSize = generatedLabelStartTextSize.toFloat()
        endtextPaintGauge.textSize = generatedLabelEndTextSize.toFloat()
        //textPaintCenterDesc.textSize = viewWidth*0.08f
        textPaintCenterDesc.textSize = generatedLabelCenterDescTextSize.toFloat()
        textPaintCenter.textSize = generatedLabelCenterTextSize.toFloat()
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
        progressThumbSizeRatio = progressThumbSize/viewWidth
        //canvas?.drawRect(mArcRect,mPaint)

        starttextPaintGauge.getTextBounds(labelStart,0, labelStart.length,labelStartBounds)
        endtextPaintGauge.getTextBounds(labelEnd,0, labelEnd.length,labelEndBounds)
        *//*var startAdj = 0f
        var endAdj = 0f
        if(labelStartBounds.width() > 175){
            startAdj = labelStartBounds.width()-fullRectOffset
        }
        if(labelEndBounds.width() > 175){
            endAdj = labelEndBounds.width()-fullRectOffset
        }*//*
        TEXT_SIZE_FACTOR= maxOf(labelStartBounds.height(),labelEndBounds.height()).toFloat()
        //mArcRect.set(0f+fullRectOffset,0f+fullRectOffset,(arcviewWidth - fullRectOffset).toFloat(),(arcviewWidth - fullRectOffset).toFloat())
        canvas?.drawText(labelStart,0+fullRectOffset*0.5f, viewWidth/2f+labelStartBounds.height()+textOffset,starttextPaintGauge)
        canvas?.drawText(labelEnd,viewWidth-fullRectOffset*0.5f, viewWidth/2f+labelEndBounds.height()+textOffset,endtextPaintGauge)
         canvas?.drawText(labelCenter,viewWidth/2f, viewWidth/2f - fullRectOffset*0.5f,textPaintCenter)
        //canvas?.drawMultilineText(labelCenter,textPaintCenter,labelEndBounds.width(),viewWidth/2f, viewWidth/2f - fullRectOffset*0.5f)
        canvas?.drawText(labelCenterDesc,viewWidth/2f, viewWidth/2f +fullRectOffset/2f ,textPaintCenterDesc)
        canvas?.drawArc(mArcRect,180f,180f,false,mArcPaintTrack)
        canvas?.drawArc(mArcRect,180f,progressSweep,false,mArcPaint)
        var radius = (viewWidth-2*fullRectOffset)/2
        var thumbx = (radius * Math.cos(Math.toRadians(progressSweep.toDouble())))
        var thumby = (radius * Math.sin(Math.toRadians(progressSweep.toDouble())))
        if(isThumbVisible){
            canvas?.save()
            canvas?.translate(viewWidth/2 - thumbx.toFloat(), viewWidth/2 - thumby.toFloat())
            mThumb?.draw(canvas!!)
            canvas?.restore()

        }




    }*/


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