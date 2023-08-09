package dev.erwin.todo.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.erwin.todo.core.data.sources.realtime.RealtimeAuthDataSource
import dev.erwin.todo.core.data.sources.realtime.RealtimeAuthDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RealtimeAuthDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindRealtimeAuthDataSource(
        realtimeAuthDataSourceImpl: RealtimeAuthDataSourceImpl
    ): RealtimeAuthDataSource
}