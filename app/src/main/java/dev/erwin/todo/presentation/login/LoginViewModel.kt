package dev.erwin.todo.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.erwin.todo.core.domain.models.AuthStudent
import dev.erwin.todo.core.domain.usecases.AuthUseCase
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
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {
    private val _emailLoginState = MutableStateFlow<UiState<Nothing?>>(UiState.Initialize)
    val emailLoginState get() = _emailLoginState.asStateFlow()
    private val _googleLoginState = MutableStateFlow<UiState<Nothing?>>(UiState.Initialize)
    val googleLoginState get() = _googleLoginState.asStateFlow()
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

    fun doLoginWithEmailAndPassword() = viewModelScope.launch {
        authStudent.value?.let {
            val result = authUseCase.loginWithEmailAndPassword(student = it)
            result.collectLatest { uiState ->
                _emailLoginState.value = uiState
            }
        }
    }

    fun doLoginWithGoogle(googleToken: String?) = viewModelScope.launch {
        val result = authUseCase.loginOrRegisterWithGoogle(googleToken = googleToken)
        result.collectLatest { uiState ->
            _googleLoginState.value = uiState
        }
    }
}