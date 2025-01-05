package com.example.horizontrack_mad_cw

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.horizontrack_mad_cw.model.Note

class NotesAdapter(
    private val notes: MutableList<Note>,
    private val onNoteClick: (Int) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val imageView: ImageView = itemView.findViewById(R.id.noteImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content
        if (note.imageUri != null) {
            holder.imageView.setImageURI(Uri.parse(note.imageUri))
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_image)
        }

        holder.itemView.setOnClickListener { onNoteClick(position) }
    }

    override fun getItemCount() = notes.size
}
