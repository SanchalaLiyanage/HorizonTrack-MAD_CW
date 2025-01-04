package com.example.horizontrack_mad_cw

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BMIcalActivity : AppCompatActivity() {

    private lateinit var gender: String
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var etAge: EditText
    private lateinit var tvBMIDetails: TextView

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_cal)

        // Initialize views
        val llMaleCard = findViewById<LinearLayout>(R.id.llMaleCard)
        val llFemaleCard = findViewById<LinearLayout>(R.id.llFemaleCard)
        val btnCalculate = findViewById<Button>(R.id.btnCalculate1)
        val btnReset = findViewById<Button>(R.id.btnReset)
        tvBMIDetails = findViewById(R.id.tvBMIDetails1)
        etHeight = findViewById(R.id.etHeight)
        etWeight = findViewById(R.id.etWeight)
        etAge = findViewById(R.id.etAge)

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

        // Load current user details
//        loadUserDetails()
    }

    private fun calculateBMI() {
        val heightStr = etHeight.text.toString()
        val weightStr = etWeight.text.toString()
        val ageStr = etAge.text.toString()

        if (TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(gender)) {
            Toast.makeText(this, "Please fill all fields and select gender", Toast.LENGTH_SHORT).show()
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
//        val userId = auth.currentUser?.uid ?: return
        val userId = "User123" // Hardcoded for testing
        val userBMI = UserBMI(gender, height, weight, age, bmi, userId)

        firestore.collection("usersBMI")
            .document(userId)
            .set(userBMI)
            .addOnSuccessListener {
                Toast.makeText(this, "BMI details saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error saving BMI details", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserDetails() {
//        val userId = auth.currentUser?.uid ?: return

        val userId = "User123" // Hardcoded for testing
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userBMI = document.toObject(UserBMI::class.java)
                    if (userBMI != null) {
                        etHeight.setText(userBMI.height.toString())
                        etWeight.setText(userBMI.weight.toString())
                        etAge.setText(userBMI.age.toString())
                        tvBMIDetails.text = "Your BMI is: %.2f".format(userBMI.bmi)
                        gender = userBMI.gender
                        if (gender == "Male") {
                            findViewById<LinearLayout>(R.id.llMaleCard).setBackgroundResource(R.drawable.selected_background)
                        } else if (gender == "Female") {
                            findViewById<LinearLayout>(R.id.llFemaleCard).setBackgroundResource(R.drawable.selected_background)
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading user details", Toast.LENGTH_SHORT).show()
            }
    }
}
