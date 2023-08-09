package dev.erwin.todo.core.domain.usecases

import dev.erwin.todo.core.domain.models.Note
import dev.erwin.todo.core.domain.models.NoteDetail
import kotlinx.coroutines.flow.Flow

interface NoteInteractor {
    fun getNotes(userId: String): Flow<List<Note>>
    fun getNoteById(id: String, userId: String): Flow<NoteDetail?>
    suspend fun upsertNote(note: NoteDetail, userId: String)
    suspend fun deleteNoteById(userId: String, noteId: String)
}