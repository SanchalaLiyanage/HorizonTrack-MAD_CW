package com.example.horizontrack_mad_cw

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Profile1 : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var profileImage: ImageView
    private var selectedImageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        profileImage = findViewById(R.id.profile_image)

        findViewById<Button>(R.id.save_button).setOnClickListener {
            saveProfileDataWithoutLogin()
        }

        findViewById<Button>(R.id.update_picture_button).setOnClickListener {
            selectImage()
        }
    }

    private fun selectImage() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        startActivityForResult(pickIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            val imageUri: Uri? = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            selectedImageBitmap = bitmap
            profileImage.setImageBitmap(bitmap)
        }
    }

    private fun saveProfileDataWithoutLogin() {
        val fullName = findViewById<EditText>(R.id.full_name_edit).text.toString()
        val gender = findViewById<EditText>(R.id.gender_edit).text.toString()
        val email = findViewById<EditText>(R.id.email_edit).text.toString()
        val birthday = findViewById<EditText>(R.id.birthday_edit).text.toString()

        // Generate a unique key for the user
        val userId = database.child("user").push().key ?: return

        val imageBase64 = selectedImageBitmap?.let { convertBitmapToBase64(it) } ?: ""

        val userProfile = UserProfile(fullName, gender, email, birthday, imageBase64)

        database.child("users").child(userId).setValue(userProfile)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    data class UserProfile(
        val fullName: String = "",
        val gender: String = "",
        val email: String = "",
        val birthday: String = "",
        val profileImageBase64: String = ""
    )
}
