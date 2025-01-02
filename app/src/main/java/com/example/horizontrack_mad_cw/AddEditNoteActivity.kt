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

class AddEditNoteActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private var isEdit = false
    private var position: Int = -1
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        imageView = findViewById(R.id.imageView)

        // Use the updated method to retrieve Parcelable
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
    }

    private fun saveOrUpdateNote() {
        val title = titleEditText.text.toString()
        val content = contentEditText.text.toString()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and Content cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val resultIntent = Intent().apply {
            putExtra("note", Note(title, content, imageUri?.toString()))
            if (isEdit) putExtra("position", position)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
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
