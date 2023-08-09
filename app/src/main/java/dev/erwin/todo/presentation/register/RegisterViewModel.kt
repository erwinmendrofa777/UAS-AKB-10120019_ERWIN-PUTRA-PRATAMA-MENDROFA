package dev.erwin.todo.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.erwin.todo.core.domain.models.AuthStudent
import dev.erwin.todo.core.domain.usecases.AuthInteractor
import dev.erwin.todo.core.utils.UiState
import dev.erwin.todo.presentation.utils.isEmail
import dev.erwin.todo.presentation.utils.isPasswordSecure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authUseCase: AuthInteractor
) : ViewModel() {
    private val _emailRegisterState = MutableStateFlow<UiState<Nothing?>>(UiState.Initialize)
    val emailRegisterState get() = _emailRegisterState.asStateFlow()
    private val _googleRegisterState = MutableStateFlow<UiState<Nothing?>>(UiState.Initialize)
    val googleRegisterState get() = _googleRegisterState.asStateFlow()
    private val _emailAddress = MutableStateFlow("")
    val emailAddress
        get() = _emailAddress
            .drop(1)
            .distinctUntilChanged()
    private val _password = MutableStateFlow("")
    val password
        get() = _password
            .drop(1)
            .distinctUntilChanged()
    private val authStudent = emailAddress.filter { it.isEmail() }
        .combine(password.filter { it.isPasswordSecure() }) { emailAddress, password ->
            AuthStudent(emailAddress = emailAddress, password = password)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    fun onEmailAddressChanged(emailAddress: String) {
        _emailAddress.value = emailAddress
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun doRegisterWithEmailAndPassword() = viewModelScope.launch {
        authStudent.value?.let {
            val result = authUseCase.registerWithEmailAndPassword(student = it)
            result.collectLatest { uiState ->
                _emailRegisterState.value = uiState
            }
        }
    }

    fun doRegisterWithGoogle(googleToken: String?) = viewModelScope.launch {
        val result = authUseCase.loginOrRegisterWithGoogle(googleToken = googleToken)
        result.collectLatest { uiState ->
            _googleRegisterState.value = uiState
        }
    }
}