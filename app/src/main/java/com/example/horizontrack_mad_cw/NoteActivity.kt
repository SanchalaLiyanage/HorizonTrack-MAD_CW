package com.example.horizontrack_mad_cw

import android.app.Activity.RESULT_OK
import androidx.fragment.app.Fragment
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.horizontrack_mad_cw.model.Note
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class NoteFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private val notesList = mutableListOf<Note>()
    private lateinit var adapter: NotesAdapter
    private lateinit var addEditNoteLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.activity_note_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onCreate(savedInstanceState)

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.notesRecyclerView)
        adapter = NotesAdapter(notesList) { position -> editNoteAt(position) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // FloatingActionButton for adding a new note
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(requireContext(), AddEditNoteActivity::class.java)
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


        loadNotesFromFirestore()
    }

    private fun editNoteAt(position: Int) {
        val note = notesList[position]
        val intent = Intent(requireContext(), AddEditNoteActivity::class.java).apply {
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
                val noteId = notesList[position].id
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
                        addNoteToFirestore(it) // Use this function to handle addition
                    } else {
                        updateNoteInFirestore(it, position) // Update Firestore and the list
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
                Toast.makeText(requireContext(), "Error loading notes: ${e.message}", Toast.LENGTH_SHORT).show()
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
                notesList.add(note) // Add the new note to the list
                adapter.notifyItemInserted(notesList.size - 1) // Notify adapter about the new item
                Toast.makeText(requireContext(), "Note added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error adding note: ${e.message}", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "Error updating note: ${e.message}", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "Error deleting note: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
