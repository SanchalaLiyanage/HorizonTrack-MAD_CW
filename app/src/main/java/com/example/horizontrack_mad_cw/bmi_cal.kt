package com.example.horizontrack_mad_cw

import android.os.Bundle
import android.widget.*
import com.example.horizontrack_mad_cw.model.bmirequest_model
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class bmi_cal: AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firestore = FirebaseFirestore.getInstance()

        val heightInput = findViewById<EditText>(R.id.etHeight)
        val weightInput = findViewById<EditText>(R.id.etWeight)
        val ageInput = findViewById<EditText>(R.id.etAge)
        val calculateButton = findViewById<Button>(R.id.btnCalculate1)
        val bmiDetails = findViewById<TextView>(R.id.tvBMIDetails1)

        calculateButton.setOnClickListener {
            val height = heightInput.text.toString().toDoubleOrNull() ?: 0.0
            val weight = weightInput.text.toString().toDoubleOrNull() ?: 0.0
            val age = ageInput.text.toString().toIntOrNull() ?: 0

            if (height > 0 && weight > 0) {
                val bmi = calculateBMI(height, weight)
                val recommendation = getRecommendation(bmi)
                val gender = "Male" // Retrieve gender from user input

                val bmiRecord = BMIRecord(
                    height = height,
                    weight = weight,
                    age = age,
                    gender = gender,
                    bmi = bmi,
                    recommendation = recommendation
                )

                saveToFirestore(bmiRecord)

                bmiDetails.text = "Your BMI: %.2f\nRecommendation: %s".format(bmi, recommendation)
            } else {
                Toast.makeText(this, "Please enter valid values", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateBMI(height: Double, weight: Double): Double {
        val heightInMeters = height / 100
        return weight / (heightInMeters * heightInMeters)
    }

    private fun getRecommendation(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi in 18.5..24.9 -> "Normal weight"
            bmi in 25.0..29.9 -> "Overweight"
            else -> "Obesity"
        }
    }

    private fun saveToFirestore(bmiRecord: BMIRecord) {
        firestore.collection("bmi_records")
            .add(bmiRecord)
            .addOnSuccessListener {
                Toast.makeText(this, "Record saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save record.", Toast.LENGTH_SHORT).show()
            }
    }
}
