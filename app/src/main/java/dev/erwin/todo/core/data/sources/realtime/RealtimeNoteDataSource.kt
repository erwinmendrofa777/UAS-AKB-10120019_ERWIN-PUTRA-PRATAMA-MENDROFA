package dev.erwin.todo.core.data.sources.realtime

import dev.erwin.todo.core.data.sources.realtime.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

interface RealtimeNoteDataSource {
    fun getNoteEntities(userId: String): Flow<List<NoteEntity>>
    fun getNoteEntityById(id: String, userId: String): Flow<NoteEntity?>
    suspend fun upsertNoteEntity(noteEntity: NoteEntity, userId: String)
    suspend fun deleteNoteEntityById(userId: String, noteId: String)
}