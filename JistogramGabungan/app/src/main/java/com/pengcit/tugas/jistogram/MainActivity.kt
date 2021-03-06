package com.pengcit.tugas.jistogram

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TUGAS_1 = 1
    private val TUGAS_2 = 2
    private val TUGAS_3 = 3
    private val TUGAS_4 = 4
    private val TUGAS_5 = 5
    private val TUGAS_6 = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tugas1Btn.setOnClickListener{ goToActivity(TUGAS_1) }
        tugas2Btn.setOnClickListener{ goToActivity(TUGAS_2) }
        tugas3Btn.setOnClickListener{ goToActivity(TUGAS_3) }
        tugas4Btn.setOnClickListener{ goToActivity(TUGAS_4) }
        tugas5Btn.setOnClickListener{ goToActivity(TUGAS_5) }
        tugas6Btn.setOnClickListener{ goToActivity(TUGAS_6) }
    }

    private fun goToActivity(destination: Int) {
        var intent : Intent? = null
        if (destination == TUGAS_1){
            intent = Intent(this, T1ShowJistogram::class.java)
        } else if (destination == TUGAS_2) {
            intent = Intent(this, T2JistogramEqualization::class.java)
        } else if(destination == TUGAS_3) {
            intent = Intent(this, T3JistogramSpecification::class.java)
        } else if(destination == TUGAS_4) {
            intent = Intent(this, T4ChainCodeArialNumber::class.java)
        } else if(destination == TUGAS_5) {
            intent = Intent(this, T5ThinningImage::class.java)
        } else if(destination == TUGAS_6) {
            intent = Intent(this, T6PredictThinning::class.java)
        }

        if (intent != null){
            startActivity(intent)
        } else {
            Toast.makeText(this, "Failed to go to next activity", Toast.LENGTH_SHORT).show()
        }
    }
}
