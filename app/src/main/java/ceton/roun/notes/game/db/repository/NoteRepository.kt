package ceton.roun.notes.game.db.repository

import androidx.lifecycle.LiveData
import ceton.roun.notes.game.model.NoteModel

interface NoteRepository {
    val allNotes: LiveData<List<NoteModel>>
    suspend fun insertNote(noteModel: NoteModel, onSuccess: () -> Unit)
    suspend fun deleteNote(noteModel: NoteModel, onSuccess: () -> Unit)
}