package com.example.horizontrack_mad_cw

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dash)

        Handler(Looper.getMainLooper()).postDelayed({
            NavBar(this).initializeActivityContainerAndNavBar()
        }, 1500)
    }
}
