package com.example.horizontrack_mad_cw
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class LandingPageActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landingpage)

        mAuth = FirebaseAuth.getInstance()


        val backIcon: ImageView = findViewById(R.id.backIcon)
        backIcon.setOnClickListener {
            onBackPressed()
            finish()
        }

        val getStartedButton: Button = findViewById(R.id.btn_started)
        getStartedButton.setOnClickListener {

            if (mAuth.currentUser != null) {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        val logoutBtn: Button = findViewById(R.id.logout)
        logoutBtn.visibility = View.INVISIBLE;
        if(mAuth.currentUser != null){
            logoutBtn.visibility = View.VISIBLE;
            logoutBtn.setOnClickListener{
                mAuth.signOut()
                val intent = Intent(this, LandingPageActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }
}


