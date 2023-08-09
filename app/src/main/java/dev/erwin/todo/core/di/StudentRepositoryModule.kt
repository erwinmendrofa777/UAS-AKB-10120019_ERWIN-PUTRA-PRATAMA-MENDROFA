package dev.erwin.todo.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.erwin.todo.core.data.repositories.StudentRepositoryImpl
import dev.erwin.todo.core.domain.repositories.StudentRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StudentRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindStudentRepository(
        studentRepositoryImpl: StudentRepositoryImpl
    ): StudentRepository
}