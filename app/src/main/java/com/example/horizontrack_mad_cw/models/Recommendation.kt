package com.example.horizontrack_mad_cw.models

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

data class Recommendation(
    val category: String,
    val interpretation: String,
    val healthImplications: String,
    val recommendations: String
)

fun uploadRecommendationsToFirestore() {
    // Initialize Firestore instance
    val firestore = FirebaseFirestore.getInstance()

    // Create a list of Recommendation objects
    val recommendations = listOf(
        Recommendation(
            category = "Underweight",
            interpretation = "A BMI under 18.5 means you are underweight.",
            healthImplications = "Potential issues include malnutrition, weakened immunity, and bone density problems.",
            recommendations = "Focus on increasing caloric intake, nutrient-dense foods, and resistance training."
        ),
        Recommendation(
            category = "Normal Weight",
            interpretation = "A BMI between 18.5 and 24.9 is considered healthy.",
            healthImplications = "Lower risk of chronic diseases when paired with a healthy lifestyle.",
            recommendations = "Maintain a balanced diet, regular exercise, and monitor overall health."
        ),
        Recommendation(
            category = "Overweight",
            interpretation = "A BMI between 25.0 and 29.9 indicates being overweight.",
            healthImplications = "Increased risk of metabolic conditions, cardiovascular issues, and joint problems.",
            recommendations = "Aim for gradual weight loss through dietary changes and regular physical activity."
        ),
        Recommendation(
            category = "Obese",
            interpretation = "A BMI of 30 or higher indicates obesity.",
            healthImplications = "Higher risk of serious health conditions such as type 2 diabetes, heart disease, and sleep apnea.",
            recommendations = "Pursue a comprehensive weight loss strategy, including diet, exercise, and medical intervention."
        )
    )

    // Upload each recommendation to Firestore
    recommendations.forEachIndexed { index, recommendation ->
        val documentId = "category${index + 1}" // Generate document ID
        firestore.collection("Recommendation")
            .document(documentId)
            .set(recommendation)
            .addOnSuccessListener {
                Log.d("Firestore", "Document $documentId successfully uploaded.")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error uploading document $documentId", e)
            }
    }
}