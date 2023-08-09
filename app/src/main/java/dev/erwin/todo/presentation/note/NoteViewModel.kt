package dev.erwin.todo.presentation.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.erwin.todo.core.domain.models.NoteDetail
import dev.erwin.todo.core.domain.usecases.AuthInteractor
import dev.erwin.todo.core.domain.usecases.NoteInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteUseCase: NoteInteractor,
    authUseCase: AuthInteractor
) : ViewModel() {
    private val _noteId: MutableStateFlow<String?> = MutableStateFlow(null)
    private val noteId get() = _noteId.asStateFlow()
    private val _noteDetail = MutableStateFlow<NoteDetail?>(null)
    val noteDetail get() = _noteDetail.asStateFlow()
    private val user = authUseCase
        .user
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    private val _isRemoveClicked = MutableStateFlow(false)
    val isRemoveClicked get() = _isRemoveClicked.asStateFlow()
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite get() = _isFavorite.asStateFlow()
    private val _title = MutableStateFlow("")
    val title get() = _title.asStateFlow()
    private val _body = MutableStateFlow("")
    val body get() = _body.asStateFlow()

    init {
        viewModelScope.launch {
            _noteDetail.value =
                user.filterNotNull().combine(noteId.filterNotNull()) { user, noteId ->
                    val noteDetail = noteUseCase.getNoteById(id = noteId, userId = user.uid).first()
                    _isFavorite.value = noteDetail?.isFavorite ?: false
                    noteDetail
                }.first()
        }
    }

    fun onTitleChanged(title: String) {
        _title.value = title
    }

    fun onBodyChanged(body: String) {
        _body.value = body
    }

    fun onNoteIdChanged(noteId: String?) {
        _noteId.value = noteId
    }

    fun onFavoriteChanged(isFavorite: Boolean) {
        _isFavorite.value = isFavorite
    }

    fun modifyNote(newTitle: String, newBody: String) = viewModelScope.launch {
        user.value?.uid?.let { userId ->
            noteDetail.collectLatest { noteDetail ->
                noteDetail?.let {
                    if (newTitle.isBlank() && newBody.isBlank())
                        noteUseCase.deleteNoteById(
                            userId = userId,
                            noteId = it.id!!
                        )
                    else
                        noteUseCase.upsertNote(
                            userId = userId,
                            note = it.copy(
                                title = newTitle.ifBlank { "No Title" },
                                body = newBody,
                                lastUpdatedAt = it.lastUpdatedAt,
                                isFavorite = isFavorite.value
                            )
                        )
                } ?: run {
                    if (newBody.isNotBlank() or newTitle.isNotBlank())
                        noteUseCase.upsertNote(
                            userId = userId,
                            note = NoteDetail(
                                title = newTitle.ifBlank { "No Title" },
                                body = newBody,
                                isFavorite = isFavorite.value
                            )
                        )
                }
            }
        }
    }

    fun deleteNote() = viewModelScope.launch {
        _isRemoveClicked.value = true
        user.value?.uid?.let { userId ->
            noteDetail.value?.let {
                noteUseCase.deleteNoteById(
                    userId = userId,
                    noteId = it.id!!
                )
            }
        }
    }
}
