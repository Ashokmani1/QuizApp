package com.example.quizapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.quizapp.data.local.dao.AnswerDao
import com.example.quizapp.data.local.dao.AttemptDao
import com.example.quizapp.data.local.dao.QuestionDao
import com.example.quizapp.data.local.dao.QuizDao
import com.example.quizapp.data.local.entity.AnswerEntity
import com.example.quizapp.data.local.entity.AttemptEntity
import com.example.quizapp.data.local.entity.QuestionEntity
import com.example.quizapp.data.local.entity.QuizEntity

@Database(
    entities = [
        QuizEntity::class,
        QuestionEntity::class,
        AttemptEntity::class,
        AnswerEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
    abstract fun questionDao(): QuestionDao
    abstract fun attemptDao(): AttemptDao
    abstract fun answerDao(): AnswerDao
}
