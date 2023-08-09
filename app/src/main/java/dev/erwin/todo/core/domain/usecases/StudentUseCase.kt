package dev.erwin.todo.core.domain.usecases

import dev.erwin.todo.core.domain.models.StudentProfile
import dev.erwin.todo.core.domain.repositories.StudentRepository
import javax.inject.Inject

class StudentUseCase @Inject constructor(
    private val studentRepository: StudentRepository
) : StudentInteractor {
    override val studentProfile: StudentProfile
        get() = studentRepository.studentProfile
}