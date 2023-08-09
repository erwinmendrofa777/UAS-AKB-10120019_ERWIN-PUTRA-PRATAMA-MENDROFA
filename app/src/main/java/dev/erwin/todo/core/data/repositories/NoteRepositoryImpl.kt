package dev.erwin.todo.core.data.repositories

import dev.erwin.todo.core.data.sources.realtime.RealtimeNoteDataSource
import dev.erwin.todo.core.data.sources.realtime.entities.NoteEntity
import dev.erwin.todo.core.domain.models.Note
import dev.erwin.todo.core.domain.models.NoteDetail
import dev.erwin.todo.core.domain.repositories.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val realtimeNoteDataSource: RealtimeNoteDataSource,
) : NoteRepository {
    override fun getNotes(userId: String): Flow<List<Note>> =
        realtimeNoteDataSource.getNoteEntities(userId = userId).map { noteEntities ->
            noteEntities.map {
                Note(
                    id = it.id,
                    title = it.title!!,
                    isFavorite = it.favorite!!,
                    lastUpdatedAt = it.lastUpdatedAt!!,
                )
            }
        }

    override fun getNoteById(id: String, userId: String): Flow<NoteDetail?> =
        realtimeNoteDataSource.getNoteEntityById(id = id, userId = userId).map { noteEntity ->
            noteEntity?.let {
                NoteDetail(
                    id = it.id,
                    title = it.title!!,
                    body = it.body!!,
                    isFavorite = it.favorite!!,
                    createdAt = it.createdAt!!,
                    lastUpdatedAt = it.lastUpdatedAt!!
                )
            }
        }

    override suspend fun upsertNote(note: NoteDetail, userId: String) {
        val noteEntity = NoteEntity(
            id = note.id,
            title = note.title,
            body = note.body,
            favorite = note.isFavorite,
            createdAt = note.createdAt,
            lastUpdatedAt = note.lastUpdatedAt
        )
        realtimeNoteDataSource.upsertNoteEntity(noteEntity = noteEntity, userId = userId)
    }

    override suspend fun deleteNoteById(userId: String, noteId: String) =
        realtimeNoteDataSource.deleteNoteEntityById(userId = userId, noteId = noteId)
}