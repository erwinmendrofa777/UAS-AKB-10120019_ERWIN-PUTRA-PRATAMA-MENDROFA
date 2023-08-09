package dev.erwin.todo.core.data.repositories

import com.google.firebase.auth.FirebaseUser
import dev.erwin.todo.core.data.sources.realtime.RealtimeAuthDataSource
import dev.erwin.todo.core.domain.models.AuthStudent
import dev.erwin.todo.core.domain.repositories.AuthRepository
import dev.erwin.todo.core.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val realtimeAuthDataSource: RealtimeAuthDataSource,
) : AuthRepository {
    override suspend fun signInWithEmailAndPassword(student: AuthStudent): Flow<UiState<Nothing?>> =
        flow {
            emit(UiState.Loading)
            val result = realtimeAuthDataSource.signInWithEmailAndPassword(student = student)
            result.fold({
                emit(UiState.Error(it))
            }, {
                emit(UiState.Success(it))
            })
        }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override suspend fun signUpWithEmailAndPassword(student: AuthStudent): Flow<UiState<Nothing?>> =
        flow {
            emit(UiState.Loading)
            val result = realtimeAuthDataSource.signUpWithEmailAndPassword(student = student)
            result.fold({
                emit(UiState.Error(it))
            }, {
                emit(UiState.Success(it))
            })
        }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override suspend fun signInWithGoogle(googleToken: String?): Flow<UiState<Nothing?>> =
        flow {
            emit(UiState.Loading)
            val result = realtimeAuthDataSource.signInWithGoogle(googleToken = googleToken)
            result.fold({
                emit(UiState.Error(it))
            }, {
                emit(UiState.Success(it))
            })
        }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override suspend fun signOut() = realtimeAuthDataSource.signOut()

    override val isAlreadyLogin: Flow<Boolean?>
        get() =
            realtimeAuthDataSource.getUser()
                .distinctUntilChanged()
                .map {
                    it?.uid != null
                }
                .flowOn(Dispatchers.IO)

    override val user: Flow<FirebaseUser>
        get() = realtimeAuthDataSource.getUser()
            .filterNotNull()
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
}
