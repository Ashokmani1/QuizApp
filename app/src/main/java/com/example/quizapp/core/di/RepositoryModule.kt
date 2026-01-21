package com.example.quizapp.core.di

import com.example.quizapp.data.repository.QuizRepositoryImpl
import com.example.quizapp.domain.repository.QuizRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindQuizRepository(
        impl: QuizRepositoryImpl
    ): QuizRepository
}
