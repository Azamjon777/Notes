package ceton.roun.notes.game.addnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ceton.roun.notes.game.REPOSITORY
import ceton.roun.notes.game.model.NoteModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddNoteViewModel : ViewModel() {
    fun insert(noteModel: NoteModel, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            REPOSITORY.insertNote(noteModel) {
                onSuccess()
            }
        }
    }
}