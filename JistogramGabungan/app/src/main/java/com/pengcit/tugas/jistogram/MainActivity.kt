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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tugas1Btn.setOnClickListener{ goToActivity(TUGAS_1) }
        tugas2Btn.setOnClickListener{ goToActivity(TUGAS_2) }
        tugas3Btn.setOnClickListener{ goToActivity(TUGAS_3) }
    }

    private fun goToActivity(destination: Int) {
        var intent : Intent? = null
        if (destination == TUGAS_1){
            intent = Intent(this, T1ShowJistogram::class.java)
        } else if (destination == TUGAS_2) {
            intent = Intent(this, T2JistogramEqualization::class.java)
        } else if(destination == TUGAS_3) {
            intent = Intent(this, T3JistogramSpecification::class.java)
        }
        if (intent != null){
            startActivity(intent)
        } else {
            Toast.makeText(this, "Failed to go to next activity", Toast.LENGTH_SHORT).show()
        }
    }
}
