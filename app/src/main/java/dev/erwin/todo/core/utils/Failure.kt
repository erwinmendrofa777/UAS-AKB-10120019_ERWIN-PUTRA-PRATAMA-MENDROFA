package dev.erwin.todo.core.utils

sealed class Failure(override var message: String?) : Exception(message) {
    data class ConnectionFailure(
        override var message: String? = "😞No internet connection",
    ) : Failure(message)
}
