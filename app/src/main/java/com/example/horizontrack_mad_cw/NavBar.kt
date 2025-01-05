package com.example.horizontrack_mad_cw

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

        // Set the default selected item
        bottomNavigationView.selectedItemId = R.id.nav_dashboard

        // Load the default fragment
        loadLayout(BMICalFragment())

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    loadLayout(BMICalFragment())
                    true
                }

                R.id.nav_activity -> {
                    loadLayout(SummaryFragment())
                    true
                }

                R.id.nav_stats -> {
                    loadLayout(NoteFragment())
                    true
                }

                R.id.nav_profile -> {
                    loadLayout(ProfileFragment())
                    true
                }

                else -> {
                    false
                }
            }
        }


    }

    private fun loadLayout(fragment: Fragment) {
        if (context is AppCompatActivity) {
            context.supportFragmentManager.beginTransaction()
                .replace(R.id.top_container, fragment)
                .commit()
        }
    }
}
