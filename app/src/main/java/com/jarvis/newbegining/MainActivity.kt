package com.jarvis.newbegining

import android.animation.ObjectAnimator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.view.updatePaddingRelative
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.Slider
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val customView : CustomView = findViewById(R.id.customView)

        //val rollButton : Button = findViewById(R.id.roll_button)
        val rollButton : MaterialButton = findViewById(R.id.roll_button)
//        val myText : TextView = findViewById(R.id.sign_textView)
        /*val cg : ChipGroup = findViewById(R.id.chip_group)
        val hs : HorizontalScrollView = findViewById(R.id.h_scroll)*/
        //val progressControll :SeekBar = findViewById(R.id.progress_controll)
        val progressControll :Slider = findViewById(R.id.progress_controll)
        val trackThicknessControll :SeekBar = findViewById(R.id.track_controll)
        val progressThicknessControll :SeekBar = findViewById(R.id.progres_thick_controll)
        val thumbControll :SeekBar = findViewById(R.id.thumb_controll)
        val gaugeSizeControll : Slider = findViewById(R.id.gaugeView_controll)
        val thumbSwitch :Switch = findViewById(R.id.thumb_switch)
        thumbSwitch.isChecked = true
        val toggleButton: MaterialButtonToggleGroup = findViewById(R.id.toggleButton)
        val toggleButton2: MaterialButtonToggleGroup = findViewById(R.id.toggleButton2)
        val toggleButton3: MaterialButtonToggleGroup = findViewById(R.id.toggleButton3)


        //rollButton.setFlex()
        rollButton.setOnClickListener{
           // rollDice()
            //customView.swapColor()
           // customView.setProgressColor(resources.getColor(R.color.design_default_color_error,theme))
            //customView.progressAnimation(80,800).start()
            val myanim = customView.progressAnimation(80f,2000)
            //myanim.setDuration(1000)
            myanim.start()
            myanim.addUpdateListener {

                customView.setLabelCenter("$${"%.2f".format(it.animatedValue)}")

            }
            /*val anim = ObjectAnimator.ofInt(customView, "progress", 80)
            anim.setDuration(800)
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.start()*/


        }
        customView.setLabelStart("$0")
        customView.setLabelEnd("$100")
        /*progressControll.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                customView.setProgress(progress)
                customView.setLabelCenter("$"+progress+".00")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

        })*/
        progressControll.addOnChangeListener { slider, value, fromUser ->
            customView.setProgress(value.toInt())
            customView.setLabelCenter("$ ${"%.2f".format(value)}")
            }

        progressThicknessControll.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                customView.setProgressArcThickness(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

        })

        trackThicknessControll.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                customView.setProgressTrackThickness(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

        })

        thumbControll.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                customView.setThumbSize  (progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

        })

        thumbSwitch.setOnCheckedChangeListener{bittonView,isChecked ->
            if(isChecked){
                customView.isThumbVisible(true)
            }else{
                customView.isThumbVisible(false)
            }
        }

        toggleButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when(checkedId){
                R.id.btn1 -> customView.setProgressThumbDrawable(resources.getDrawable(R.drawable.ic_run_circle,theme))
                R.id.btn2 -> customView.setProgressThumbDrawable(resources.getDrawable(R.drawable.ic_star,theme))
                R.id.btn3 -> customView.setProgressThumbDrawable(resources.getDrawable(R.drawable.ic_trial,theme))
            }
        }
        toggleButton2.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when(checkedId){
                R.id.btn12 -> {
                    customView.setLabelStartTextSize(resources.getDimensionPixelSize(R.dimen.dimen_15))
                    customView.setLabelEndTextSize(resources.getDimensionPixelSize(R.dimen.dimen_15))
                    customView.setLabelCenterTextSize(resources.getDimensionPixelSize(R.dimen.dimen_30))
                }
                R.id.btn22 -> {
                    customView.setLabelStartTextSize(resources.getDimensionPixelSize(R.dimen.dimen_20))
                    customView.setLabelEndTextSize(resources.getDimensionPixelSize(R.dimen.dimen_20))
                    customView.setLabelCenterTextSize(resources.getDimensionPixelSize(R.dimen.dimen_60))
                }
                R.id.btn32 -> {
                    customView.setLabelStartTextSize(resources.getDimensionPixelSize(R.dimen.dimen_30))
                    customView.setLabelEndTextSize(resources.getDimensionPixelSize(R.dimen.dimen_30))
                    customView.setLabelCenterTextSize(resources.getDimensionPixelSize(R.dimen.dimen_90))
                }
            }
        }
        toggleButton3.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when(checkedId){
                R.id.btn13 -> {
                    customView.setLabelStart("$10.0")
                    customView.setLabelEnd("$110.0")
                }
                R.id.btn23 -> {
                    customView.setLabelStart("$1234.00")
                    customView.setLabelEnd("$5678.00")

                }
                R.id.btn33 -> {
                    customView.setLabelStart("$50,000.00")
                    customView.setLabelEnd("$80,000.00")

                }
            }
        }

        gaugeSizeControll.addOnChangeListener { slider, value, fromUser ->
            customView.updateLayoutParams<LinearLayout.LayoutParams> {width =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,resources.displayMetrics).toInt() }
        }


        /*customView.setLabelStart("$20")
        customView.setLabelEnd("$200")
        customView.setLabelCenter("$250")
        customView.setLabelCenterDesc("Hello!")
        customView.setProgress(30)
        customView.setProgressColor(Color.YELLOW)*/
        /*var p :ViewGroup.MarginLayoutParams = rollButton.layoutParams as ViewGroup.MarginLayoutParams
        var marginInDP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,64F,resources.displayMetrics)
        p.setMargins(marginInDP.toInt(),0,marginInDP.toInt(),0)
        rollButton.requestLayout()*/
        val scale:  Float= resources.displayMetrics.density
        var marginInDP = (50*scale+0.5f).toInt()
        //rollButton.setPadding(marginInDP.toInt(),marginInDP.toInt(),marginInDP.toInt(),marginInDP.toInt())
        //rollButton.updatePadding(marginInDP.toInt(),marginInDP.toInt(),marginInDP.toInt(),marginInDP.toInt())
                //rollButton.updatePadding(16,16,16,16)
        //rollButton.updatePaddingRelative(marginInDP.toInt(),marginInDP.toInt(),marginInDP.toInt(),marginInDP.toInt())
        //rollButton.setPadding((Math.ceil((16*resources.displayMetrics.density).toDouble())).toInt())
        //rollButton.requestLayout()
        //myText.setPadding(marginInDP,marginInDP,marginInDP,marginInDP)
        //rollButton.setPadding(marginInDP,marginInDP,marginInDP,marginInDP)
        //rollButton.requestLayout()

       /* cg.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 0.0f }
        hs.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 1.0f }*/




    }

    /*private fun rollDice() {
       val firstDice = Dice(6)
       val secondDice = Dice(6)
        val firstDiceRoll = firstDice.roll()
        val secondDiceRoll = secondDice.roll()
        val result1 :TextView = findViewById(R.id.result_textView1)
        val result2 :TextView = findViewById(R.id.result_textView2)
        result1.text = firstDiceRoll.toString()
        result2.text = secondDiceRoll.toString()
    }*/
}