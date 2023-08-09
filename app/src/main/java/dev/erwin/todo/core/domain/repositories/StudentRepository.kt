package dev.erwin.todo.core.domain.repositories

import dev.erwin.todo.core.domain.models.StudentProfile

interface StudentRepository {
    val studentProfile: StudentProfile
}