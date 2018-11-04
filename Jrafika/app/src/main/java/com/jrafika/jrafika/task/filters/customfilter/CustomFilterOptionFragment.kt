package com.jrafika.jrafika.task.filters.customfilter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.RadioButton
import android.widget.TextView
import com.jrafika.jrafika.R

class CustomFilterOptionFragment: Fragment() {

    var matrixGridView: GridView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.custom_filter_option_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        matrixGridView = view.findViewById(R.id.matrix_grid_view)

        val sobelHorizontalOption = view.findViewById<RadioButton>(R.id.horizontal_sobel_matrix)
        val sobelVerticalOption = view.findViewById<RadioButton>(R.id.vertical_sobel_matrix)

        var context = this.context
        matrixGridView!!.adapter = object: BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val textView= TextView(context)
                return textView
            }
            override fun getItem(position: Int): Any {
                return 0
            }
            override fun getItemId(position: Int): Long {
                return 0
            }
            override fun getCount(): Int {
                return 9
            }
        }
    }

}