package com.example.horizontrack_mad_cw


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.horizontrack_mad_cw.models.Recommendation
import com.example.horizontrack_mad_cw.models.saveDietPlansToFirestore
import com.google.firebase.firestore.FirebaseFirestore
import com.example.horizontrack_mad_cw.models.saveDietPlansToFirestore
import com.example.horizontrack_mad_cw.models.uploadRecommendationsToFirestore

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
        }

        btnFourth.setOnClickListener {
            // Add logic for fourth button
        }
    }












}
