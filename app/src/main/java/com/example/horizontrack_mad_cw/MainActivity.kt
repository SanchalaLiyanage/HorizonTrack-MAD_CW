package com.example.horizontrack_mad_cw

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val notesList = mutableListOf<Note>()
    private lateinit var adapter: NotesAdapter
    private lateinit var addEditNoteLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Back button logic
        val backIcon = findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener {
            finish() // Close the current activity
        }

        // Set up RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.notesRecyclerView)
        adapter = NotesAdapter(notesList) { position -> editNoteAt(position) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // FloatingActionButton for adding a new note
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddEditNoteActivity::class.java)
            addEditNoteLauncher.launch(intent)
        }

        // Set up ActivityResultLauncher
        addEditNoteLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    handleActivityResult(data)
                }
            }

        // Load notes from Firestore
        loadNotesFromFirestore()
    }

    private fun editNoteAt(position: Int) {
        val note = notesList[position]
        val intent = Intent(this, AddEditNoteActivity::class.java).apply {
            putExtra("note", note)
            putExtra("position", position)
        }
        addEditNoteLauncher.launch(intent)
    }

    private fun handleActivityResult(data: Intent?) {
        if (data != null) {
            val delete = data.getBooleanExtra("delete", false)
            val position = data.getIntExtra("position", -1)

            if (delete && position != -1) {
                val noteId = notesList[position].id // Assume Note class has an `id` field
                deleteNoteFromFirestore(noteId, position)
            } else {
                val note: Note? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    data.getParcelableExtra("note", Note::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    data.getParcelableExtra("note") as? Note
                }

                note?.let {
                    if (position == -1) {
                        addNoteToFirestore(it)
                    } else {
                        updateNoteInFirestore(it, position)
                    }
                }
            }
        }
    }

    private fun loadNotesFromFirestore() {
        db.collection("notes")
            .get()
            .addOnSuccessListener { result ->
                notesList.clear()
                for (document in result) {
                    val id = document.id
                    val title = document.getString("title") ?: ""
                    val content = document.getString("content") ?: ""
                    val imageUri = document.getString("imageUri")
                    notesList.add(Note(id, title, content, imageUri))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading notes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addNoteToFirestore(note: Note) {
        val noteData = hashMapOf(
            "title" to note.title,
            "content" to note.content,
            "imageUri" to note.imageUri
        )
        db.collection("notes")
            .add(noteData)
            .addOnSuccessListener { documentReference ->
                note.id = documentReference.id
                notesList.add(note)
                adapter.notifyItemInserted(notesList.size - 1)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding note: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateNoteInFirestore(note: Note, position: Int) {
        val noteData = hashMapOf(
            "title" to note.title,
            "content" to note.content,
            "imageUri" to note.imageUri
        )
        db.collection("notes")
            .document(note.id)
            .set(noteData)
            .addOnSuccessListener {
                notesList[position] = note
                adapter.notifyItemChanged(position)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating note: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteNoteFromFirestore(noteId: String, position: Int) {
        db.collection("notes")
            .document(noteId)
            .delete()
            .addOnSuccessListener {
                notesList.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting note: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
