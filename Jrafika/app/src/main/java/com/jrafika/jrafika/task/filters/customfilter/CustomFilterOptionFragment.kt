package com.jrafika.jrafika.task.filters.customfilter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jrafika.jrafika.R

class CustomFilterOptionFragment: Fragment() {

    val SOBEL_HORIZONTAL_KERNEL = floatArrayOf(-1.0f, -2.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f)
    val SOBEL_VERTICAL_KERNEL = floatArrayOf(-1.0f, 0.0f, 1.0f, -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f)
    val PREWITT_KERNEL = floatArrayOf(1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f)
    val FREI_CHEN_KERNEL = floatArrayOf()

    val textViewMatrix = ArrayList<EditText>()

    var proceedFunction: ((FloatArray) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.custom_filter_option_fragment_layout, container, false)
    }

    fun setMatrix(kernel: FloatArray) {
        for (i in 0..8) {
            textViewMatrix.get(i).setText("" + kernel[i])
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val matrixGridView = view.findViewById<GridView>(R.id.matrix_grid_view)
        val proceedButton = view.findViewById<Button>(R.id.proceedButton)

        val matrixTemplateOptions = view.findViewById<RadioGroup>(R.id.matrix_template)

        for (i in 0..8) {
            val editText = EditText(this.context)
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setText("0.11")
            textViewMatrix.add(editText)
        }

        matrixGridView!!.adapter = object: BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                return textViewMatrix.get(position)
            }
            override fun getItem(position: Int): Any {
                return 0
            }
            override fun getItemId(position: Int): Long {
                return 0
            }
            override fun getCount(): Int {
                return textViewMatrix.size
            }
        }

        val kernelMap = HashMap<Int, FloatArray>()
        kernelMap.put(R.string.sobel_horizontal, SOBEL_HORIZONTAL_KERNEL)
        kernelMap.put(R.string.sobel_vertical, SOBEL_VERTICAL_KERNEL)
        kernelMap.put(R.string.prewitt, PREWITT_KERNEL)

        for (name in kernelMap.keys) {
            val view = RadioButton(this.context)
            view.setText(name)
            matrixTemplateOptions.addView(view)
            val kernel = kernelMap.get(name)
            view.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    setMatrix(kernel!!)
                }
            }
        }
        matrixTemplateOptions.invalidate()

        proceedButton.setOnClickListener {
            val kernel = FloatArray(9)
            for (i in 0..8) {
                kernel[i] = textViewMatrix.get(i)!!.text.toString().toFloat()
            }
            if (proceedFunction != null) {
                proceedFunction!!(kernel)
            }
        }
    }

}