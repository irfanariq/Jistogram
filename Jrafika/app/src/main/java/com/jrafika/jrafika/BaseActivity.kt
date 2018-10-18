package com.jrafika.jrafika

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import com.jrafika.jrafika.task.histogram.HistogramActivity
import com.jrafika.jrafika.task.histogram.HistogramEqualizationActivity
import com.jrafika.jrafika.task.histogram.specification.HistogramSpecificationActivity
import com.jrafika.jrafika.task.histogram.stretching.LinearStretchingActivity
import com.jrafika.jrafika.task.ocr.chaincode.ImageChainCodeActivity
import kotlinx.android.synthetic.main.navigation_layout.*


abstract open class BaseActivity : AppCompatActivity() {

    abstract val contentViewId: Int

    var mToggle: ActionBarDrawerToggle? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_layout)

        mToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(mToggle!!)
        mToggle!!.syncState()
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)

        val inflater: LayoutInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView: View = inflater.inflate(contentViewId, null, false)
        drawerLayout.addView(contentView, 0)

        navigationView.setNavigationItemSelectedListener {
            var intent: Intent? = null
            if (it.itemId == R.id.task1MenuOption) {
                intent = Intent(this, HistogramActivity::class.java)
            } else if (it.itemId == R.id.task2MenuOption) {
                intent = Intent(this, HistogramEqualizationActivity::class.java)
            } else if (it.itemId == R.id.task3MenuOption) {
                intent = Intent(this, LinearStretchingActivity::class.java)
            } else if (it.itemId == R.id.task4MenuOption) {
                intent = Intent(this, HistogramSpecificationActivity::class.java)
            } else if (it.itemId == R.id.task5MenuOption) {
                intent = Intent(this, ImageChainCodeActivity::class.java)
            }

            if (intent != null) {
                startActivity(intent)
                if (this.javaClass != MainActivity::class.java) {
                    finish()
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (mToggle!!.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
