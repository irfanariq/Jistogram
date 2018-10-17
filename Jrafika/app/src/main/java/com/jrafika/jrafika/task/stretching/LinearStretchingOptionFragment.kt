package com.jrafika.jrafika.task.stretching

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.crystal.crystalrangeseekbar.widgets.BubbleThumbRangeSeekbar
import com.jrafika.jrafika.R

class LinearStretchingOptionFragment: Fragment() {

    var inputSeekBar: BubbleThumbRangeSeekbar? = null
    var outputSeekBar: BubbleThumbRangeSeekbar? = null
    var inputMinText: TextView? = null
    var inputMaxText: TextView? = null
    var outputMinText: TextView? = null
    var outputMaxText: TextView? = null
    var proceedButton: Button? = null

    open var proceedFunction: ((inputMin: Int, inputMax: Int, outputMin: Int, outputMax: Int) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.linear_strech_option_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inputSeekBar = view.findViewById(R.id.inputSeekbar)
        outputSeekBar = view.findViewById(R.id.outputSeekbar)
        inputMinText = view.findViewById(R.id.inputMinText)
        inputMaxText = view.findViewById(R.id.inputMaxText)
        outputMinText = view.findViewById(R.id.outputMinText)
        outputMaxText = view.findViewById(R.id.outputMaxText)
        proceedButton = view.findViewById(R.id.proceedButton)

        inputSeekBar!!.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            inputMinText!!.text = minValue.toString()
            inputMaxText!!.text = maxValue.toString()
        }

        outputSeekBar!!.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            outputMinText!!.text = minValue.toString()
            outputMaxText!!.text = maxValue.toString()
        }

        proceedButton!!.setOnClickListener {
            if (proceedFunction != null) {
                proceedFunction!!(
                        inputSeekBar!!.selectedMinValue.toInt(),
                        inputSeekBar!!.selectedMaxValue.toInt(),
                        outputSeekBar!!.selectedMinValue.toInt(),
                        outputSeekBar!!.selectedMaxValue.toInt()
                )
            }
        }
    }
}