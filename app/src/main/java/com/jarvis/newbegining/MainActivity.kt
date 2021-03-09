package com.jarvis.newbegining

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.setPadding
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val customView : CustomView = findViewById(R.id.customView2)

        //val rollButton : Button = findViewById(R.id.roll_button)
        val rollButton : MaterialButton = findViewById(R.id.roll_button)

        //rollButton.setFlex()
        rollButton.setOnClickListener{
            rollDice()
            customView.swapColor()
        }
        customView.setLabelStart("$20")
        customView.setLabelEnd("$200")
        customView.setLabelCenter("$250")
        customView.setLabelCenterDesc("Hello!")
        customView.setProgress(30)
        customView.setProgressColor(Color.YELLOW)
        /*var p :ViewGroup.MarginLayoutParams = rollButton.layoutParams as ViewGroup.MarginLayoutParams
        var marginInDP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,64F,resources.displayMetrics)
        p.setMargins(marginInDP.toInt(),0,marginInDP.toInt(),0)
        rollButton.requestLayout()*/
        val scale:  Float= resources.displayMetrics.density
        var marginInDP = (16.0f*scale+0.5f).toInt()
        rollButton.setPadding(marginInDP)
        //rollButton.requestLayout()
    }

    private fun rollDice() {
       val firstDice = Dice(6)
       val secondDice = Dice(6)
        val firstDiceRoll = firstDice.roll()
        val secondDiceRoll = secondDice.roll()
        val result1 :TextView = findViewById(R.id.result_textView1)
        val result2 :TextView = findViewById(R.id.result_textView2)
        result1.text = firstDiceRoll.toString()
        result2.text = secondDiceRoll.toString()
    }
}