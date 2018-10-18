package com.jrafika.jrafika.task.histogram.specification

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.jrafika.jrafika.R
import com.jrafika.jrafika.core.Util

class HistogramSpecificationOptionFragment: Fragment() {

    var pivot1SeekBar: SeekBar? = null
    var pivot2SeekBar: SeekBar? = null
    var pivot3SeekBar: SeekBar? = null
    var pivot4SeekBar: SeekBar? = null
    var targetChart: LineChart? = null
    var proceedButton: Button? = null

    var targetHistogram: IntArray? = null
    open var proceedFunction: ((target: IntArray) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.histogram_specification_option_fragment_layout, container, false)
    }

    fun redrawChart() {
        val p1 = pivot1SeekBar!!.progress
        val p2 = pivot2SeekBar!!.progress
        val p3 = pivot3SeekBar!!.progress
        val p4 = pivot4SeekBar!!.progress

        object: AsyncTask<Int, Unit, List<Double>>() {
            override fun doInBackground(vararg params: Int?): List<Double> {
                val result = ArrayList<Double>()
                var coef = Util.curveEquation(0.0, 0.0, 51.0, p1.toDouble())
                for (i in 0..50) {
                    result.add(coef[0] + coef[1] * i + coef[2] * i * i + coef[3] * i * i * i)
                }

                coef = Util.curveEquation(51.0, p1.toDouble(), 102.0, p2.toDouble())
                for (i in 51..101) {
                    result.add(coef[0] + coef[1] * i + coef[2] * i * i + coef[3] * i * i * i)
                }

                coef = Util.curveEquation(102.0, p2.toDouble(), 153.0, p3.toDouble())
                for (i in 102..152) {
                    result.add(coef[0] + coef[1] * i + coef[2] * i * i + coef[3] * i * i * i)
                }

                coef = Util.curveEquation(153.0, p3.toDouble(), 204.0, p4.toDouble())
                for (i in 153..203) {
                    result.add(coef[0] + coef[1] * i + coef[2] * i * i + coef[3] * i * i * i)
                }

                coef = Util.curveEquation(204.0, p4.toDouble(), 255.0, 0.0)
                for (i in 204..255) {
                    result.add(coef[0] + coef[1] * i + coef[2] * i * i + coef[3] * i * i * i)
                }
                return result
            }

            override fun onPostExecute(result: List<Double>?) {
                targetHistogram = IntArray(result!!.size)
                for (i in 0..result.size-1) {
                    targetHistogram!![i] = result[i].toInt()
                }

                val entries = ArrayList<Entry>()
                for (value in result!!)
                    entries.add(Entry(entries.size.toFloat() , value.toFloat()))
                val dataSet = LineDataSet(entries, "Target")
                targetChart!!.data = LineData(dataSet)
                targetChart!!.invalidate()
            }
        }.execute(p1, p2, p3, p4)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pivot1SeekBar = view.findViewById(R.id.pivot1Seekbar)
        pivot2SeekBar = view.findViewById(R.id.pivot2Seekbar)
        pivot3SeekBar = view.findViewById(R.id.pivot3Seekbar)
        pivot4SeekBar = view.findViewById(R.id.pivot4Seekbar)
        targetChart = view.findViewById(R.id.targetHistogramChart)
        proceedButton = view.findViewById(R.id.proceedButton)

        val updateHandler = object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    redrawChart()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        }

        pivot1SeekBar!!.setOnSeekBarChangeListener(updateHandler)
        pivot2SeekBar!!.setOnSeekBarChangeListener(updateHandler)
        pivot3SeekBar!!.setOnSeekBarChangeListener(updateHandler)
        pivot4SeekBar!!.setOnSeekBarChangeListener(updateHandler)

        proceedButton!!.setOnClickListener {
            if (proceedFunction != null && targetHistogram != null) {
                proceedFunction!!(targetHistogram!!)
            }
        }

        redrawChart()
    }
}