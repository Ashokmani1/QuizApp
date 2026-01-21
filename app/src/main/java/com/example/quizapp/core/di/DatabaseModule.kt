package com.example.quizapp.core.di

import android.content.Context
import androidx.room.Room
import com.example.quizapp.core.common.Constants
import com.example.quizapp.data.local.QuizDatabase
import com.example.quizapp.data.local.dao.AnswerDao
import com.example.quizapp.data.local.dao.AttemptDao
import com.example.quizapp.data.local.dao.QuestionDao
import com.example.quizapp.data.local.dao.QuizDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideQuizDatabase(
        @ApplicationContext context: Context
    ): QuizDatabase = Room.databaseBuilder(
        context,
        QuizDatabase::class.java,
        Constants.DATABASE_NAME,
    ).fallbackToDestructiveMigration().build()
    
    @Provides
    fun provideQuizDao(database: QuizDatabase): QuizDao = database.quizDao()
    
    @Provides
    fun provideQuestionDao(database: QuizDatabase): QuestionDao = database.questionDao()
    
    @Provides
    fun provideAttemptDao(database: QuizDatabase): AttemptDao = database.attemptDao()
    
    @Provides
    fun provideAnswerDao(database: QuizDatabase): AnswerDao = database.answerDao()
}
