package dev.erwin.todo.core.domain.usecases

import com.google.firebase.auth.FirebaseUser
import dev.erwin.todo.core.domain.models.AuthStudent
import dev.erwin.todo.core.domain.repositories.AuthRepository
import dev.erwin.todo.core.utils.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : AuthInteractor {
    override suspend fun loginWithEmailAndPassword(student: AuthStudent): Flow<UiState<Nothing?>> =
        authRepository.signInWithEmailAndPassword(student = student)

    override suspend fun registerWithEmailAndPassword(student: AuthStudent): Flow<UiState<Nothing?>> {
        return authRepository.signUpWithEmailAndPassword(student = student)
    }


    override suspend fun loginOrRegisterWithGoogle(googleToken: String?): Flow<UiState<Nothing?>> =
        authRepository.signInWithGoogle(googleToken = googleToken)

    override suspend fun logout() =
        authRepository.signOut()

    override val isAlreadyLogin: Flow<Boolean?>
        get() =
            authRepository.isAlreadyLogin

    override val user: Flow<FirebaseUser> get() = authRepository.user
}