package ceton.roun.notes.game.db.repository

import androidx.lifecycle.LiveData
import ceton.roun.notes.game.db.dao.NoteDao
import ceton.roun.notes.game.model.NoteModel

class NoteRealization(private val noteDao: NoteDao) : NoteRepository {
    override val allNotes: LiveData<List<NoteModel>>
        get() = noteDao.getAllNotes()

    override suspend fun insertNote(noteModel: NoteModel, onSuccess: () -> Unit) {
        noteDao.insert(noteModel)
        onSuccess()
    }

    override suspend fun deleteNote(noteModel: NoteModel, onSuccess: () -> Unit) {
        noteDao.delete(noteModel)
        onSuccess()
    }

}