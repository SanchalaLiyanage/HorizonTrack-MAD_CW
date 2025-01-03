package com.example.horizontrack_mad_cw// Replace with your actual package name

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.horizontrack_mad_cw.R


class LandingPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landingpage)

        // Back Button functionality
        val backIcon: ImageView = findViewById(R.id.backIcon)
        backIcon.setOnClickListener {
            onBackPressed() // Navigate back to the previous page
        }

        // Get Started Button functionality
        val getStartedButton: Button = findViewById(R.id.btn_started)
        getStartedButton.setOnClickListener {
            // Navigate to SignUpActivity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}


