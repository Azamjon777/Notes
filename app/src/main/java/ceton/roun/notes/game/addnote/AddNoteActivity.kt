package ceton.roun.notes.game.addnote

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ceton.roun.notes.databinding.ActivityAddNoteBinding
import ceton.roun.notes.game.model.NoteModel

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initial()
    }

    private fun initial() {
        val viewModel = ViewModelProvider(this)[AddNoteViewModel::class.java]
        binding.btnAddNote.setOnClickListener {
            val title = binding.etAddTitle.text.toString()
            val desc = binding.etAddDesc.text.toString()
            if (title.isNotEmpty() && desc.isNotEmpty()) {
                viewModel.insert(NoteModel(title = title, desc = desc)) {}
                finish()
            }
        }
        binding.btnBackImg.setOnClickListener {
            finish()
        }
    }
}
