package com.example.horizontrack_mad_cw

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.horizontrack.MapActivity

class FitnessActivity : AppCompatActivity() {

    private lateinit var greeting: TextView
    private lateinit var calories: TextView
    private lateinit var distance: TextView
    private lateinit var navActivity: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fitness)

        greeting = findViewById(R.id.Greeting)
        calories = findViewById(R.id.Calories)
        distance = findViewById(R.id.Distance)
        navActivity = findViewById(R.id.nav_activity)

        // Dummy data
        greeting.text = "Hello Layla!"
        calories.text = "546 kcal"
        distance.text = "3.5 km"

        navActivity.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }
}



