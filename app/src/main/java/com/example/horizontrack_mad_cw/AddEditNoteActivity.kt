package com.example.horizontrack_mad_cw

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddEditNoteActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private var isEdit = false
    private var position: Int = -1
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        imageView = findViewById(R.id.imageView)

        val note: Note? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("note", Note::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("note") as? Note
        }

        position = intent.getIntExtra("position", -1)

        note?.let {
            isEdit = true
            titleEditText.setText(it.title)
            contentEditText.setText(it.content)
            imageUri = it.imageUri?.let { uriString -> Uri.parse(uriString) }
            imageUri?.let { uri -> imageView.setImageURI(uri) }
        }

        findViewById<Button>(R.id.saveButton).apply {
            text = if (isEdit) "Update" else "Save"
            setOnClickListener { saveOrUpdateNote() }
        }

        findViewById<Button>(R.id.deleteButton).apply {
            visibility = if (isEdit) Button.VISIBLE else Button.GONE
            setOnClickListener { deleteNote() }
        }

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    imageUri = result.data?.data
                    imageView.setImageURI(imageUri)
                }
            }

        imageView.setOnClickListener { selectImage() }

        // Back button logic
        val backIcon = findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener {
            finish() // Close the current activity
        }
    }

    private fun saveOrUpdateNote() {
        val title = titleEditText.text.toString()
        val content = contentEditText.text.toString()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and Content cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri != null) {
            val imageRef = storage.reference.child("images/${UUID.randomUUID()}.jpg")
            val uploadTask = imageRef.putFile(imageUri!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveNoteToFirestore(title, content, uri.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            saveNoteToFirestore(title, content, null)
        }
    }

    private fun saveNoteToFirestore(title: String, content: String, imageUrl: String?) {
        val note = hashMapOf(
            "title" to title,
            "content" to content,
            "imageUri" to imageUrl
        )

        db.collection("notes")
            .add(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving note: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteNote() {
        val resultIntent = Intent().apply {
            putExtra("delete", true)
            putExtra("position", position)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }
}
