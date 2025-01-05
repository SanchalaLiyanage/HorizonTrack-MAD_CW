package com.example.horizontrack_mad_cw


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize buttons
        val btnBMICal = findViewById<Button>(R.id.btnBMICal)
        val btnSecond = findViewById<Button>(R.id.btnSecond)
        val btnThird = findViewById<Button>(R.id.btnThird)
        val btnFourth = findViewById<Button>(R.id.btnFourth)

        // Button actions
        btnBMICal.setOnClickListener {
            // Navigate to BMICalActivity
            val intent = Intent(this, BMIcalActivity::class.java)
            startActivity(intent)
        }

        btnSecond.setOnClickListener {
//            uploadRecommendationsToFirestore()
        }

        btnThird.setOnClickListener {
//            saveDietPlansToFirestore()
            val intent = Intent(this, Profile1::class.java)
            startActivity(intent)
        }

        btnFourth.setOnClickListener {
            // Add logic for fourth button
        }
    }












}
