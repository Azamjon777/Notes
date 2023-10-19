package ceton.roun.notes.game.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ceton.roun.notes.databinding.ActivityDetailBinding
import ceton.roun.notes.game.model.NoteModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initial()
    }

    private fun initial() {
        val viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        val noteModel = intent.getSerializableExtra("note") as? NoteModel

        if (noteModel != null) {
            binding.etTitleDetail.setText(noteModel.title)
            binding.etDescDetail.setText(noteModel.desc)
        }

        binding.btnBackImg.setOnClickListener {
            finish()
        }
        binding.btnDelete.setOnClickListener {
            if (noteModel != null) {
                viewModel.delete(noteModel) {}
                finish()
            }
        }
        binding.btnSave.setOnClickListener {
            val title = binding.etTitleDetail.text.toString()
            val desc = binding.etDescDetail.text.toString()
            if (title.isNotEmpty() && desc.isNotEmpty()) {
                if (noteModel != null) {
                    viewModel.delete(noteModel) {}
                    viewModel.save(NoteModel(title = title, desc = desc)) {}
                    finish()
                }
            }
        }
    }
}
