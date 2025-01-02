package com.example.horizontrack_mad_cw

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val notesList = mutableListOf<Note>()
    private lateinit var adapter: NotesAdapter
    private lateinit var addEditNoteLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the Back Button and set its OnClickListener
        val backIcon = findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener {
            finish() // Closes the current activity
        }

        val recyclerView = findViewById<RecyclerView>(R.id.notesRecyclerView)
        adapter = NotesAdapter(notesList) { position -> editNoteAt(position) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddEditNoteActivity::class.java)
            addEditNoteLauncher.launch(intent)
        }

        addEditNoteLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    handleActivityResult(data)
                }
            }
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
                notesList.removeAt(position)
                adapter.notifyItemRemoved(position)
            } else {
                val note: Note? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    data.getParcelableExtra("note", Note::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    data.getParcelableExtra("note") as? Note
                }

                note?.let {
                    if (position == -1) {
                        notesList.add(it)
                        adapter.notifyItemInserted(notesList.size - 1)
                    } else {
                        notesList[position] = it
                        adapter.notifyItemChanged(position)
                    }
                }
            }
        }
    }
}
