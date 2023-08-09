package dev.erwin.todo.core.domain.models

import android.graphics.drawable.Drawable

data class StudentProfile(
    val studentId: String,
    val fullName: String,
    val profilePicture: Drawable,
    val `class`: String,
    val emailAddress: String,
)