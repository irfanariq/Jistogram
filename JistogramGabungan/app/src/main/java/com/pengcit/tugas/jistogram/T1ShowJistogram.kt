package com.pengcit.tugas.jistogram

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_t1_show_jistogram.*

class T1ShowJistogram : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_t1_show_jistogram)

        selectImageBtn.setOnClickListener { IntentHelper.showPictureDialog(this) }
    }


}
