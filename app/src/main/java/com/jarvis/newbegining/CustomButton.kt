package com.jarvis.newbegining

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.Button

class CustomButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatButton(context, attrs, defStyleAttr) {


    init {
        var a :TypedArray = context.obtainStyledAttributes(attrs,R.styleable.CustomButton)
        var type =a.getInt(R.styleable.CustomButton_customType,2)

        when(type){
            1 -> {/*donothing*/ }
            2 -> setFlex()

        }
        a.recycle()
    }

    fun setFlex(){
        layoutParams?.let {
            var p : ViewGroup.MarginLayoutParams = it as ViewGroup.MarginLayoutParams
            var marginInDP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,64F,resources.displayMetrics)
            p.setMargins(marginInDP.toInt(),0,marginInDP.toInt(),0)
            requestLayout()
        }

    }

}
