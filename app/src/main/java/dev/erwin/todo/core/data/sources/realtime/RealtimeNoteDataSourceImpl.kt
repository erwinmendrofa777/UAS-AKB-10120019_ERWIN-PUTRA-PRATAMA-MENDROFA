package dev.erwin.todo.core.data.sources.realtime

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dev.erwin.todo.core.data.sources.realtime.entities.NoteEntity
import dev.erwin.todo.core.utils.NOTES
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RealtimeNoteDataSourceImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
) : RealtimeNoteDataSource {

    override fun getNoteEntities(userId: String): Flow<List<NoteEntity>> =
        callbackFlow {
            val noteListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(snapshot.children.mapNotNull {
                        it.getValue<NoteEntity>()
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    cancel(CancellationException(error.message, error.toException()))
                }
            }
            firebaseDatabase.reference.child(NOTES).child(userId)
                .addValueEventListener(noteListener)
            awaitClose {
                firebaseDatabase.reference.child(NOTES).child(userId)
                    .removeEventListener(noteListener)
            }
        }.distinctUntilChanged()

    override fun getNoteEntityById(id: String, userId: String): Flow<NoteEntity?> =
        callbackFlow {
            val noteListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(snapshot.getValue<NoteEntity>())
                }

                override fun onCancelled(error: DatabaseError) {
                    cancel(CancellationException(error.message, error.toException()))
                }
            }
            firebaseDatabase.reference.child(NOTES).child(userId).child(id)
                .addValueEventListener(noteListener)
            awaitClose {
                firebaseDatabase.reference.child(NOTES).child(userId).child(id)
                    .removeEventListener(noteListener)
            }
        }.distinctUntilChanged()


    override suspend fun upsertNoteEntity(noteEntity: NoteEntity, userId: String): Unit =
        withContext(Dispatchers.IO) {
            noteEntity.id?.let {
                firebaseDatabase.reference.child(NOTES).child(userId).child(it).setValue(noteEntity)
                    .await()
            } ?: run {
                noteEntity.id = firebaseDatabase.reference.push().key!!
                noteEntity.id?.let {
                    firebaseDatabase.reference.child(NOTES).child(userId).child(it)
                        .setValue(noteEntity)
                        .await()
                }
            }
        }

    override suspend fun deleteNoteEntityById(userId: String, noteId: String) {
        firebaseDatabase.reference
            .child(NOTES)
            .child(userId)
            .child(noteId)
            .removeValue()
            .await()
    }
}