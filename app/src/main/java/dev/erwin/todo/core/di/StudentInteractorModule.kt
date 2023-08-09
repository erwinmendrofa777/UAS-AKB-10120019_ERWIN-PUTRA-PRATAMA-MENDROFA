package dev.erwin.todo.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.erwin.todo.core.domain.usecases.StudentInteractor
import dev.erwin.todo.core.domain.usecases.StudentUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StudentInteractorModule {

    @Singleton
    @Binds
    abstract fun bindStudentInteractor(
        studentUseCase: StudentUseCase
    ): StudentInteractor
}