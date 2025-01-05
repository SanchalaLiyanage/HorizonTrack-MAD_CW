package com.example.horizontrack_mad_cw

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class DietPlanFragment : Fragment(R.layout.fragment_diet_plan) {

    private var bmiValue: Float = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_diet_plan, container, false)

        // Initialize the TextViews
        val tvBreakfast1: TextView = rootView.findViewById(R.id.tvBreakfast1)
        val tvLunch1: TextView = rootView.findViewById(R.id.tvLunch1)
        val tvDinner1: TextView = rootView.findViewById(R.id.tvDinner1)
        val tvSnacks1: TextView = rootView.findViewById(R.id.tvSnacks1)
        val tvWaterIntake1: TextView = rootView.findViewById(R.id.tvWaterIntake1)

        // Retrieve the BMI value passed from the Activity
        arguments?.let {
            bmiValue = it.getFloat("BMI", 0.0f)
        }

        // Categorize BMI
        val bmiCategory = categorizeBMI(bmiValue)

        // Fetch a random diet plan based on the BMI category
        fetchRandomDietPlan(bmiCategory, tvBreakfast1, tvLunch1, tvDinner1, tvSnacks1, tvWaterIntake1)

        return rootView
    }

    // Categorize BMI
    private fun categorizeBMI(bmi: Float): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi in 18.5..24.9 -> "Normal"
            bmi in 25.0..29.9 -> "Overweight"
            else -> "Obesity"
        }
    }

    // Fetch a random diet plan from Firestore based on BMI category
    private fun fetchRandomDietPlan(
        category: String,
        tvBreakfast1: TextView,
        tvLunch1: TextView,
        tvDinner1: TextView,
        tvSnacks1: TextView,
        tvWaterIntake1: TextView
    ) {
        val db = FirebaseFirestore.getInstance()

        // Reference to the DietPlans collection and filter by BMI category
        db.collection("dietPlans")
            .whereEqualTo("bmicategory", category) // Filter by BMI category
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    // Get a random document from the query result
                    val randomDocument = result.documents.random()

                    // Log success message
                    Log.d("DietPlanFragment", "Data successfully fetched from Firestore.")

                    // Log the entire fetched document to see all the data
                    Log.d("DietPlanFragment", "Fetched Data: ${randomDocument.data}")

                    // Retrieve the fields from the document
                    val breakfastPlan = randomDocument["breakfastPlan"] as? String ?: "No plan available"
                    val lunchPlan = randomDocument["lunchPlan"] as? String ?: "No plan available"
                    val dinnerPlan = randomDocument["dinnerPlan"] as? String ?: "No plan available"
                    val snacksPlan = randomDocument["snacksPlan"] as? String ?: "No plan available"
                    val waterIntake = randomDocument["waterIntake"] as? String ?: "No plan available"

                    // Ensure updates are on the main thread
                    requireActivity().runOnUiThread {
                        // Set the text for each TextView
                        tvBreakfast1.text = breakfastPlan
                        tvLunch1.text = lunchPlan
                        tvDinner1.text = dinnerPlan
                        tvSnacks1.text = snacksPlan
                        tvWaterIntake1.text = waterIntake
                    }
                } else {
                    // Handle case when no documents are found
                    Log.d("DietPlanFragment", "No diet plans found for this category.")
                    Toast.makeText(requireContext(), "No diet plans found for this category.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.e("DietPlanFragment", "Error fetching diet plan: ${exception.message}")
                Toast.makeText(requireContext(), "Error fetching diet plan: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
