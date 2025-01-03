package com.example.mad_horizontracking

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class SignUpActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Back Button functionality
        val backIcon: ImageView = findViewById(R.id.backIcon) // Ensure this ID matches the one in your XML
        backIcon.setOnClickListener {
            onBackPressed() // Go back to the previous page
        }

        // Initialize views
        val nameField: EditText = findViewById(R.id.name)
        val emailField: EditText = findViewById(R.id.email)
        val passwordField: EditText = findViewById(R.id.password)
        val confirmPasswordField: EditText = findViewById(R.id.confirm_password)
        val btnSignUp: Button = findViewById(R.id.btn_sign_up)
        val btnSignInGoogle: LinearLayout = findViewById(R.id.btn_sign_in_google)
        val tvSignIn: TextView = findViewById(R.id.tv_sign_in)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your Web Client ID
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Handle Google Sign-In button click
        btnSignInGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 1001) // Request code for Google Sign-In
        }

        // Handle SIGN UP button click
        btnSignUp.setOnClickListener {
            val name = nameField.text.toString()
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                // Navigate to Dashboard
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Handle SIGN IN link click (Navigate to Sign In Activity)
        tvSignIn.setOnClickListener {
            // Start SignInActivity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    // Handle the result of the Google Sign-In Intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001) { // Match request code
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

                if (account != null) {
                    // Successfully signed in
                    val email = account.email // Get the email address
                    Toast.makeText(this, "Welcome, $email", Toast.LENGTH_SHORT).show()

                    // Navigate to Dashboard
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

