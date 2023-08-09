package dev.erwin.todo.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.erwin.todo.core.data.sources.realtime.RealtimeNoteDataSource
import dev.erwin.todo.core.data.sources.realtime.RealtimeNoteDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RealtimeNoteDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindRealtimeNoteDataSource(
        realtimeNoteDataSourceImpl: RealtimeNoteDataSourceImpl
    ): RealtimeNoteDataSource
}