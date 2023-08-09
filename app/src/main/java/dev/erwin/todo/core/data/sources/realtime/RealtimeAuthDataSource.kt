package dev.erwin.todo.core.data.sources.realtime

import arrow.core.Either
import com.google.firebase.auth.FirebaseUser
import dev.erwin.todo.core.domain.models.AuthStudent
import kotlinx.coroutines.flow.Flow

interface RealtimeAuthDataSource {
    suspend fun signInWithEmailAndPassword(student: AuthStudent): Either<Exception, Nothing?>

    suspend fun signUpWithEmailAndPassword(student: AuthStudent): Either<Exception, Nothing?>

    suspend fun signInWithGoogle(googleToken: String?): Either<Exception, Nothing?>

    suspend fun signOut()

    fun getUser(): Flow<FirebaseUser?>
}