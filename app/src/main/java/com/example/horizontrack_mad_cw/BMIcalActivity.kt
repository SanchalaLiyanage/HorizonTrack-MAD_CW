package com.example.horizontrack_mad_cw

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.horizontrack_mad_cw.model.UserBMI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BMICalFragment : Fragment() {

    private lateinit var gender: String
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var etAge: EditText
    private lateinit var tvBMIDetails: TextView

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.activity_bmi_cal, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onCreate(savedInstanceState)

        // Initialize views
        val llMaleCard = view.findViewById<LinearLayout>(R.id.llMaleCard)
        val llFemaleCard = view.findViewById<LinearLayout>(R.id.llFemaleCard)
        val btnCalculate = view.findViewById<Button>(R.id.btnCalculate1)
        val btnRecommendations = view.findViewById<Button>(R.id.btnRecommendations1)
        val btnReset = view.findViewById<Button>(R.id.btnReset)
        tvBMIDetails = view.findViewById(R.id.tvBMIDetails1)
        etHeight = view.findViewById(R.id.etHeight)
        etWeight = view.findViewById(R.id.etWeight)
        etAge = view.findViewById(R.id.etAge)

        gender = "Male"
        llMaleCard.setBackgroundResource(R.drawable.selected_background)
        llFemaleCard.setBackgroundResource(R.drawable.default_background)

        // Gender selection logic
        llMaleCard.setOnClickListener {
            gender = "Male"
            llMaleCard.setBackgroundResource(R.drawable.selected_background)
            llFemaleCard.setBackgroundResource(R.drawable.default_background)
        }

        llFemaleCard.setOnClickListener {
            gender = "Female"
            llFemaleCard.setBackgroundResource(R.drawable.selectedbacgroundfemale)
            llMaleCard.setBackgroundResource(R.drawable.default_background)
        }

        // Calculate BMI
        btnCalculate.setOnClickListener {
            calculateBMI()
        }

        // Reset fields
        btnReset.setOnClickListener {
            resetFields()
        }
        btnRecommendations.setOnClickListener {
            val intent = Intent(requireContext(), InfoActivity::class.java)

            // Get text from EditText fields
            val age = etAge.text.toString()
            val gender = gender // Assuming gender is already assigned from some source
            val height = etHeight.text.toString()
            val weight = etWeight.text.toString()

            // Pass data as extras
            intent.putExtra("age", age)
            intent.putExtra("gender", gender)
            intent.putExtra("height", height)
            intent.putExtra("weight", weight)

            // Start the next activity
            startActivity(intent)
        }

        val currentUser = auth.currentUser

        if(currentUser == null){
            val intent = Intent(requireContext(), LandingPageActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        // Load current user details
        loadUserDetails(view)
    }

    private fun calculateBMI() {
        val heightStr = etHeight.text.toString()
        val weightStr = etWeight.text.toString()
        val ageStr = etAge.text.toString()

        if (TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(gender)) {
            Toast.makeText(requireContext(), "Please fill all fields and select gender", Toast.LENGTH_SHORT).show()
            if(gender.isEmpty()){
                Toast.makeText(requireContext(), "Please select Gender", Toast.LENGTH_SHORT).show()
            return
            }
            return
        }

        val height = heightStr.toFloat() / 100 // Convert to meters
        val weight = weightStr.toFloat()
        val age = ageStr.toInt()

        val bmi = weight / (height * height)
        tvBMIDetails.text = "Your BMI is: %.2f".format(bmi)
        Log.d("BMI", "Your BMI is: %.2f".format(bmi))
//         Save to Firestore
        saveToFirestore(gender, heightStr.toFloat(), weight, age, bmi)
    }

    private fun resetFields() {
        etHeight.text.clear()
        etWeight.text.clear()
        etAge.text.clear()
        tvBMIDetails.text = "BMI Details Appear Here"
        gender = ""
    }

    private fun saveToFirestore(gender: String, height: Float, weight: Float, age: Int, bmi: Float) {

        val userId = auth.currentUser?.email
        val userBMI = UserBMI(gender, height, weight, age, bmi, userId.toString())

        firestore.collection("usersBMI")
            .document(userId.toString())
            .set(userBMI)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "BMI details saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error saving BMI details", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserDetails(view1: View) {
//        val userId = auth.currentUser?.uid ?: return
        Log.d("savefun", "save fun call")

        val userId = auth.currentUser?.email
        firestore.collection("usersBMI")
            .document(userId.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    val userBMI = document.toObject(UserBMI::class.java)
                    Log.d("savefun", "User BMI Details: $userBMI")

                    if (userBMI != null) {
                        etHeight.setText(userBMI.height.toString())
                        etWeight.setText(userBMI.weight.toString())
                        etAge.setText(userBMI.age.toString())
                        tvBMIDetails.text = "Your BMI is: %.2f".format(userBMI.bmi)
                        gender = userBMI.gender
                        if (gender == "Male") {
                            view1.findViewById<LinearLayout>(R.id.llMaleCard).setBackgroundResource(R.drawable.selected_background)
                            view1.findViewById<LinearLayout>(R.id.llFemaleCard).setBackgroundResource(R.drawable.default_background)
                        } else if (gender == "Female") {
                            view1.findViewById<LinearLayout>(R.id.llFemaleCard).setBackgroundResource(R.drawable.selectedbacgroundfemale)
                            view1.findViewById<LinearLayout>(R.id.llMaleCard).setBackgroundResource(R.drawable.default_background)
                        }
                    }
                }else{
                    Toast.makeText(requireContext(), "Save BMI First . . .", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Not Found Saved BMI Info . . .", Toast.LENGTH_SHORT).show()
            }
    }
}
