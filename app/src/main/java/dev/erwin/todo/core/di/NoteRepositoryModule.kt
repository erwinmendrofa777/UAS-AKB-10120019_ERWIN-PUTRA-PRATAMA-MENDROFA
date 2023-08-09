package dev.erwin.todo.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.erwin.todo.core.data.repositories.NoteRepositoryImpl
import dev.erwin.todo.core.domain.repositories.NoteRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NoteRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindNoteRepository(
        noteRepositoryImpl: NoteRepositoryImpl
    ): NoteRepository
}