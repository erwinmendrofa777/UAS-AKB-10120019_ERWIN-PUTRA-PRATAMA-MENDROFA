package dev.erwin.todo.core.domain.usecases

import dev.erwin.todo.core.domain.models.StudentProfile

interface StudentInteractor {
    val studentProfile: StudentProfile
}