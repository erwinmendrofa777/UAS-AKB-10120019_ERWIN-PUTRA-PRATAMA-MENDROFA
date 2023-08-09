package dev.erwin.todo.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.erwin.todo.core.domain.usecases.AuthInteractor
import dev.erwin.todo.core.domain.usecases.NoteInteractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val noteUseCase: NoteInteractor,
    authUseCase: AuthInteractor,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val query = _query.asStateFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading get() = _isLoading.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes = authUseCase.user.flatMapLatest { user ->
        query.flatMapLatest { query ->
            val notes = noteUseCase.getNotes(userId = user.uid).map {
                it.filter { note ->
                    note.title.contains(query, ignoreCase = true)
                }
            }
            _isLoading.value = false
            notes
        }
    }.map { notes -> notes.filter { it.isFavorite } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun onQueryChanged(query: String) {
        _query.value = query
    }
}