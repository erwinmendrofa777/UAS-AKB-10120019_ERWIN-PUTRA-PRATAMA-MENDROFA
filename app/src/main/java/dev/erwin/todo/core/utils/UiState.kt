package dev.erwin.todo.core.utils

sealed class UiState<out T : Any?> {
    data object Initialize : UiState<Nothing>()

    data object Loading : UiState<Nothing>()

    data class Success<out T : Any>(val data: T? = null) : UiState<T>()

    data class Error(val error: Exception?) : UiState<Nothing>()
}