package dev.erwin.todo.core.domain.usecases

import dev.erwin.todo.core.domain.models.Note
import dev.erwin.todo.core.domain.models.NoteDetail
import dev.erwin.todo.core.domain.repositories.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) : NoteInteractor {
    override fun getNotes(userId: String): Flow<List<Note>> =
        noteRepository.getNotes(userId = userId)


    override fun getNoteById(id: String, userId: String): Flow<NoteDetail?> =
        noteRepository.getNoteById(id = id, userId = userId)

    override suspend fun upsertNote(note: NoteDetail, userId: String) =
        noteRepository.upsertNote(note = note, userId = userId)

    override suspend fun deleteNoteById(userId: String, noteId: String) =
        noteRepository.deleteNoteById(userId = userId, noteId = noteId)
}