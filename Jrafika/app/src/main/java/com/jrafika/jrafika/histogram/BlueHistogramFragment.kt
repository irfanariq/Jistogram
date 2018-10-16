package com.jrafika.jrafika.histogram

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.jrafika.jrafika.R

class BlueHistogramFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.histogram_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val titleView = view.findViewById<TextView>(R.id.histogramChartTitle)
        titleView.text = resources.getText(R.string.blue_histogram)
    }

    fun showLoadingHistogram() {
        val chart = view!!.findViewById<LineChart>(R.id.histogramLineChart)
        val progressBar = view!!.findViewById<ProgressBar>(R.id.histogramProgressBar)
        chart.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        chart.invalidate()
        progressBar.invalidate()
    }

    fun showHistogram(histogram: Array<Int>) {
        val chart = view!!.findViewById<LineChart>(R.id.histogramLineChart)
        val progressBar = view!!.findViewById<ProgressBar>(R.id.histogramProgressBar)

        val entries = ArrayList<Entry>()
        for (value in histogram)
            entries.add(Entry(entries.size.toFloat() , value.toFloat()))
        val dataSet = LineDataSet(entries, resources.getText(R.string.blue_histogram).toString())
        dataSet.color = R.color.colorBlue
        chart.data = LineData(dataSet)

        chart.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE

        chart.invalidate()
        progressBar.invalidate()
    }
}