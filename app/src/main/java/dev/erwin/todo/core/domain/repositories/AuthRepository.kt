package dev.erwin.todo.core.domain.repositories

import com.google.firebase.auth.FirebaseUser
import dev.erwin.todo.core.domain.models.AuthStudent
import dev.erwin.todo.core.utils.UiState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithEmailAndPassword(student: AuthStudent): Flow<UiState<Nothing?>>

    suspend fun signUpWithEmailAndPassword(student: AuthStudent): Flow<UiState<Nothing?>>

    suspend fun signInWithGoogle(googleToken: String?): Flow<UiState<Nothing?>>

    suspend fun signOut()

    val user: Flow<FirebaseUser>

    val isAlreadyLogin: Flow<Boolean?>
}