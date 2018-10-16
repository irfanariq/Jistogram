package com.jrafika.jrafika

import android.content.Context
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.navigation_layout.*
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View


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
            Log.d("wow", "menu selected " + it.itemId)
            if (it.itemId == R.id.task1MenuOption) {
                val intent = Intent(this.getApplicationContext(), Task1Activity::class.java)
                startActivity(intent)
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Log.d("wow", "option item selected " + item!!.itemId)
        if (mToggle!!.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
