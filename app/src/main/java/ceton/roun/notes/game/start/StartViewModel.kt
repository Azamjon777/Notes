package ceton.roun.notes.game.start

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ceton.roun.notes.game.REPOSITORY
import ceton.roun.notes.game.db.NoteDatabase
import ceton.roun.notes.game.db.repository.NoteRealization
import ceton.roun.notes.game.model.NoteModel

class StartViewModel(application: Application) : ViewModel() {
    val context = application

    fun initDatabase() {
        val daoNote = NoteDatabase.getInstance(context).getNoteDao()
        REPOSITORY = NoteRealization(daoNote)
    }

    fun getAllNotes(): LiveData<List<NoteModel>> {
        return REPOSITORY.allNotes
    }
}