package dev.erwin.todo.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.erwin.todo.core.domain.usecases.NoteInteractor
import dev.erwin.todo.core.domain.usecases.NoteUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NoteInteractorModule {

    @Singleton
    @Binds
    abstract fun bindStudentInteractor(
        noteUseCase: NoteUseCase
    ): NoteInteractor
}