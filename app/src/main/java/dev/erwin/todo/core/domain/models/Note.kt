package dev.erwin.todo.core.domain.models

data class Note(
    val id: String? = null,
    val title: String,
    val lastUpdatedAt: Long,
    val isFavorite: Boolean,
)
