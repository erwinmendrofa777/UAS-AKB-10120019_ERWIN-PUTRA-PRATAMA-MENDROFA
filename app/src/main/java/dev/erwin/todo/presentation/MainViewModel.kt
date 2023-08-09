package dev.erwin.todo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.erwin.todo.core.domain.usecases.AuthInteractor
import dev.erwin.todo.core.domain.usecases.StudentInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authUseCase: AuthInteractor,
    private val studentUseCase: StudentInteractor,
) : ViewModel() {
    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen = _isDrawerOpen.asStateFlow()
    val isAlreadyLogin
        get() = authUseCase.isAlreadyLogin
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val studentProfile get() = studentUseCase.studentProfile

    fun updaterDrawerState(isOpen: Boolean) {
        _isDrawerOpen.value = isOpen
    }

    fun logout() = viewModelScope.launch {
        authUseCase.logout()
    }
}