package ceton.roun.notes.game.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ceton.roun.notes.game.REPOSITORY
import ceton.roun.notes.game.model.NoteModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    fun delete(noteModel: NoteModel, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            REPOSITORY.deleteNote(noteModel) {
                onSuccess()
            }
        }
    }

    fun save(noteModel: NoteModel, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            REPOSITORY.insertNote(noteModel) {
                onSuccess()
            }
        }
    }
}