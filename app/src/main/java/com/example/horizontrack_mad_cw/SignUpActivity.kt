package com.example.horizontrack_mad_cw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.horizontrack_mad_cw.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.firestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()

        val backIcon: ImageView = findViewById(R.id.backIcon)
        backIcon.setOnClickListener {
            onBackPressed()
        }


        val nameField: EditText = findViewById(R.id.name)
        val emailField: EditText = findViewById(R.id.email)
        val passwordField: EditText = findViewById(R.id.password)
        val confirmPasswordField: EditText = findViewById(R.id.confirm_password)
        val btnSignUp: Button = findViewById(R.id.btn_sign_up)
        val btnSignInGoogle: LinearLayout = findViewById(R.id.btn_sign_in_google)
        val tvSignIn: TextView = findViewById(R.id.tv_sign_in)


        val gsoptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gsoptions)


        btnSignInGoogle.setOnClickListener {
            val currentUser = mAuth.currentUser
            if (currentUser != null) {
                mAuth.signOut()
            }

            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 1001)
        }


        btnSignUp.setOnClickListener {
            val name = nameField.text.toString()
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }

            if (!email.endsWith("@gmail.com")) {
                Toast.makeText(this, "Please enter a valid Gmail address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {


                val currentUser = mAuth.currentUser
                if (currentUser != null) {
                    mAuth.signOut()
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser

                            //store user data to firestore
                            val newUserWithData: User = User(name, email)
                            val db = Firebase.firestore

                            db.collection("user").document(user!!.uid)
                                .set(newUserWithData)
                                .addOnSuccessListener {
                                    Log.d("SignUpActivity", "User data saved successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("SignUpActivity", "Error saving user data: $e")
                                }

                            Toast.makeText(this, "Account created successfully for ${user?.email}", Toast.LENGTH_SHORT).show()


                            val intent = Intent(this, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {

                            Toast.makeText(this, "Sign-up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }


        tvSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    // Handle Continue with Google Button
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    mAuth = FirebaseAuth.getInstance()


                    val newAccountid = account.id!!
                    val name = account.displayName
                    val email = account.email

                    //checks if the user already exists in Firestore
                    if (name != null && email != null) {
                        val newUserWithData: User = User(name, email)
                        val db = Firebase.firestore

                        //check if alredy in fire store else add
                        db.collection("user").document(newAccountid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (!document.exists()) {
                                    db.collection("user").document(newAccountid)
                                        .set(newUserWithData)
                                        .addOnSuccessListener {
                                            Log.d("SignUpActivity", "User data saved successfully")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.d("SignUpActivity", "Error saving user data: $e")
                                        }
                                }
                            }
                    }
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Successfully signed in with
                                val user = mAuth.currentUser!!

                                Toast.makeText(this, "Welcome, ${user?.email}", Toast.LENGTH_SHORT).show()


                                val intent = Intent(this, DashboardActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Google Sign-In failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
