package ceton.roun.notes.game.start

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import ceton.roun.notes.game.NoteAdapter
import ceton.roun.notes.game.addnote.AddNoteActivity
import ceton.roun.notes.databinding.FragmentScreenAllBinding
import ceton.roun.notes.game.detail.DetailActivity
import ceton.roun.notes.game.model.NoteModel

class ScreenAllFragment : Fragment(), NoteAdapter.OnNoteItemClickListener {

    private val viewModel: StartViewModel by lazy {
        ViewModelProvider(
            this,
            StartViewModelFactory(requireContext().applicationContext as Application)
        )[StartViewModel::class.java]
    }

    private var _binding: FragmentScreenAllBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScreenAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initial()
    }

    private fun initial() {
        val layoutManager = GridLayoutManager(requireContext(), 1)
        binding.rvNotes.layoutManager = layoutManager
        val adapter = NoteAdapter(this)
        binding.rvNotes.adapter = adapter
        viewModel.initDatabase()
        viewModel.getAllNotes().observe(viewLifecycleOwner) { listNotes ->
            adapter.setList(listNotes)
        }
        binding.btnNext.setOnClickListener {
            startActivity(Intent(requireActivity(), AddNoteActivity::class.java))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNoteItemClick(noteModel: NoteModel) {
        val intent = Intent(requireActivity(), DetailActivity::class.java)
        intent.putExtra("note", noteModel)
        requireActivity().startActivity(intent)
    }
}