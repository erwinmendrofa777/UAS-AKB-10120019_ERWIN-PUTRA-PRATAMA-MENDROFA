package dev.erwin.todo.core.domain.usecases

import com.google.firebase.auth.FirebaseUser
import dev.erwin.todo.core.domain.models.AuthStudent
import dev.erwin.todo.core.utils.UiState
import kotlinx.coroutines.flow.Flow

interface AuthInteractor {
    suspend fun loginWithEmailAndPassword(student: AuthStudent): Flow<UiState<Nothing?>>

    suspend fun registerWithEmailAndPassword(student: AuthStudent): Flow<UiState<Nothing?>>

    suspend fun loginOrRegisterWithGoogle(googleToken: String?): Flow<UiState<Nothing?>>

    suspend fun logout()

    val user: Flow<FirebaseUser>

    val isAlreadyLogin: Flow<Boolean?>
}