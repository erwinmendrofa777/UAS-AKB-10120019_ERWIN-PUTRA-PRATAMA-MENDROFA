package dev.erwin.todo.core.data.sources.realtime.entities

data class NoteEntity(
    var id: String? = null,
    val title: String? = null,
    val body: String? = null,
    val createdAt: Long? = null,
    val lastUpdatedAt: Long? = null,
    val favorite: Boolean? = false
)
