package com.example.horizontrack_mad_cw

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infor)

        // Get references to views
        val btnGenerateDietPlan = findViewById<Button>(R.id.btnGenerateDietPlan)
        val fragmentContainer = findViewById<FrameLayout>(R.id.fragmentContainer)


        // Get references to TextViews
        val tvAgeValue: TextView = findViewById(R.id.tvAgevalue)
        val tvGenderValue: TextView = findViewById(R.id.tvGendervalue)
        val tvHeightValue: TextView = findViewById(R.id.tvHeightvalue)
        val tvWeightValue: TextView = findViewById(R.id.tvWeightvalue)
        val tvBMIValue: TextView = findViewById(R.id.tvBMIvalue)

        // Get Intent data from the previous screen
        val age = intent.getStringExtra("age") ?: "--"
        val gender = intent.getStringExtra("gender") ?: "--"
        val height = intent.getStringExtra("height") ?: "--"
        val weight = intent.getStringExtra("weight") ?: "--"


        // Display received data
        tvAgeValue.text = age
        tvGenderValue.text = gender
        tvHeightValue.text = height
        tvWeightValue.text = weight


        // Calculate BMI
        val heightInMeters = height.toFloat() / 100
        val weightInKg = weight.toFloat()
        val bmi = weightInKg / (heightInMeters * heightInMeters)


        tvBMIValue.text = bmi.toString()

        fetchRecommendationByBMI(bmi = bmi.toFloat())


        // Set up the button click listener to show the DietPlanFragment
        btnGenerateDietPlan.setOnClickListener {
            // Create an instance of the DietPlanFragment
            val dietPlanFragment = DietPlanFragment()

            // Begin a transaction to add the fragment to the container
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, dietPlanFragment)
            transaction.addToBackStack(null)  // Allow back navigation
            transaction.commit()
        }
    }



    // Function to fetch recommendation based on BMI
    fun fetchRecommendationByBMI(bmi: Float) {
        // Determine BMI category
        val category = when {
            bmi < 18.5 -> "category1"
            bmi in 18.5..24.9 -> "category2"
            bmi in 25.0..29.9 -> "category3"
            else -> "category4"
        }

        // Firestore instance
        val firestore = FirebaseFirestore.getInstance()

        // Reference to 'Recommendation' collection
        val collectionRef = firestore.collection("Recommendation")

        // Query Firestore for the specific category
        collectionRef.document(category)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Map Firestore fields to UI elements
                    val tvCategory = findViewById<TextView>(R.id.tvCategoryinfor)
                    val tvInterpretation = findViewById<TextView>(R.id.tvInterpretationinfor)
                    val tvHealthImplications = findViewById<TextView>(R.id.tvHealthImplicationsinfor)
                    val tvRecommendations = findViewById<TextView>(R.id.tvRecommendationsinfor)

                    // Assign values from Firestore document
                    tvCategory.text = document.getString("category") ?: "No data"
                    tvInterpretation.text = document.getString("interpretation") ?: "No data"
                    tvHealthImplications.text = document.getString("healthImplications") ?: "No data"
                    tvRecommendations.text = document.getString("recommendations") ?: "No data"
                } else {
                    // Handle case where no document is found
                    showError("No recommendation found for category: $category")
                }
            }
            .addOnFailureListener { exception ->
                // Handle Firestore query failure
                showError("Error fetching recommendation: ${exception.message}")
            }
    }

    // Utility function to display error messages
    fun showError(message: String) {
        // For simplicity, using a Toast to display errors
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
