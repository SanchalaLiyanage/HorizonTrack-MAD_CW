package com.example.horizontrack_mad_cw

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)  // Ensure this matches your layout file

        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser == null) {
            // User is not signed in
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        // Back Button functionality
        val backIcon: ImageView = findViewById(R.id.backIcon) // Use findViewById directly for activities
        backIcon.setOnClickListener {
            onBackPressed() // Go back to the previous page
        }

        // Get the user's name from Intent or SharedPreferences
        val userName = mAuth.currentUser!!.displayName

        // Set welcome message
        val welcomeTextView: TextView = findViewById(R.id.welcomeTextView)
        welcomeTextView.text = "Hello $userName!"

        // Setup click listeners for categories
        val fitnessCard: CardView = findViewById(R.id.fitnessCard)
        val diaryCard: CardView = findViewById(R.id.diaryCard)
        val dietPlanCard: CardView = findViewById(R.id.dietPlanCard)

        fitnessCard.setOnClickListener {
            Toast.makeText(this, "Fitness selected", Toast.LENGTH_SHORT).show()
            // Navigate to FitnessActivity
            startActivity(Intent(this, FitnessActivity::class.java))
        }

        diaryCard.setOnClickListener {
            Toast.makeText(this, "Diary selected", Toast.LENGTH_SHORT).show()
            // Navigate to DiaryActivity
            startActivity(Intent(this, DiaryActivity::class.java))
        }

        dietPlanCard.setOnClickListener {
            Toast.makeText(this, "Diet Plan selected", Toast.LENGTH_SHORT).show()
            // Navigate to DietPlanActivity
            startActivity(Intent(this, DietPlanActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed() // Navigate to the previous screen
    }
}
