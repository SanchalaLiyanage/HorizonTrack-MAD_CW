package com.example.horizontrack_mad_cw

import User
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
class Profile1 : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var logoutButton: Button
    private lateinit var profileImage: ImageView
    private lateinit var fullNameEdit: EditText
    private lateinit var genderEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var birthdayEdit: EditText
    private lateinit var saveButton: Button

    // Firestore instance
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        backButton = findViewById(R.id.back_button)
        logoutButton = findViewById(R.id.logout_button)
        profileImage = findViewById(R.id.profile_image)
        fullNameEdit = findViewById(R.id.full_name_edit)
        genderEdit = findViewById(R.id.gender_edit)
        emailEdit = findViewById(R.id.email_edit)
        birthdayEdit = findViewById(R.id.birthday_edit)
        saveButton = findViewById(R.id.save_button)
        fetchUserData()
        // Set up Back Button listener
        backButton.setOnClickListener {
            onBackPressed() // This will take the user back to the previous screen
        }

        // Set up Save Button listener
        saveButton.setOnClickListener {
            saveProfileData()
        }
    }

    private fun saveProfileData() {
        // Retrieve input values from the EditText fields
        val name = fullNameEdit.text.toString()
        val gender = genderEdit.text.toString()
        val email = emailEdit.text.toString()
        val birthday = birthdayEdit.text.toString()

        // Create a User object using the data class
        val user = User(
            name = name,
            email = email,
            gender = gender,
            birthday = birthday,
            isProfileComplete = true // You can set this to true if the profile is complete
        )

        // Store the user data in Firestore
//        val userId = FirebaseAuth.getInstance().currentUser?.uid // Get user ID from Firebase Auth
        val userId ="I7vWCHtrwMgHXJO5U5ZIQmSqUnx1"
        if (userId != null) {
            firestore.collection("user").document(userId)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchUserData() {
        // Get the current user ID
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userId ="I7vWCHtrwMgHXJO5U5ZIQmSqUnx1"

        if (userId != null) {
            // Fetch the user document from Firestore
            firestore.collection("user").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Retrieve data from Firestore and populate the input fields
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            fullNameEdit.setText(user.name)
                            genderEdit.setText(user.gender)
                            emailEdit.setText(user.email)
                            birthdayEdit.setText(user.birthday)
                            // You can load the profile image if needed, e.g. from a URL or Firebase Storage
                            // Glide.with(this).load(user.profileImageUrl).into(profileImage)
                        }
                    } else {
                        Toast.makeText(this, "No profile found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
