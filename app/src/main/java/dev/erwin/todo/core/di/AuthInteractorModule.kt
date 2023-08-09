package dev.erwin.todo.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.erwin.todo.core.domain.usecases.AuthInteractor
import dev.erwin.todo.core.domain.usecases.AuthUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthInteractorModule {

    @Singleton
    @Binds
    abstract fun bindAuthInteractor(
        authUseCase: AuthUseCase
    ): AuthInteractor
}
