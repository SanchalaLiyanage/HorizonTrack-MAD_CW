package com.example.horizontrack_mad_cw

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavBar(private val context: Context) {

    fun initializeActivityContainerAndNavBar() {
        val inflater = LayoutInflater.from(context)
        val activityContainerView = inflater.inflate(R.layout.activity_container, null)

        if (context is AppCompatActivity) {
            context.setContentView(activityContainerView)
        }

        val bottomNavigationView: BottomNavigationView =
            activityContainerView.findViewById(R.id.btmNavBar)

        loadLayout(R.layout.test)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    loadLayout(R.layout.test)
                    true
                }

                R.id.nav_activity -> {
                    loadLayout(R.layout.activity_fitness)
                    val trackBeginBtn: Button =
                        activityContainerView.findViewById(R.id.track_begin_btn)
                    trackBeginBtn.setOnClickListener {
                        val intent =
                            Intent(activityContainerView.context, FitnessActivity::class.java)
                        activityContainerView.context.startActivity(intent)
                    }
                    true
                }

                R.id.nav_stats -> {
                    loadLayout(R.layout.test)
                    true
                }

                R.id.nav_profile -> {
                    loadLayout(R.layout.test)
                    true
                }

                else -> {
                    false
                }
            }
        }


    }

    private fun loadLayout(layoutResId: Int) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutResId, null)
        val frameLayout: FrameLayout? =
            (context as? AppCompatActivity)?.findViewById(R.id.top_container)
        frameLayout?.removeAllViews()
        frameLayout?.addView(view)
    }
}
