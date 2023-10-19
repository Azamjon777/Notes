package ceton.roun.notes.game

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ceton.roun.notes.databinding.ItemLayoutBinding
import ceton.roun.notes.game.model.NoteModel

class NoteAdapter(private val clickListener: OnNoteItemClickListener) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    var listNote = emptyList<NoteModel>()

    class NoteViewHolder(val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = listNote[position]
        val binding = holder.binding
        binding.itemTitle.text = currentNote.title
        binding.itemTitle.setOnClickListener {
            clickListener.onNoteItemClick(currentNote)
        }
    }

    override fun getItemCount(): Int {
        return listNote.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<NoteModel>) {
        listNote = list
        notifyDataSetChanged()
    }

    interface OnNoteItemClickListener {
        fun onNoteItemClick(noteModel: NoteModel)
    }
}