package com.example.quizapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a question in a quiz.
 * Questions are fetched from the API and cached locally.
 */
@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = QuizEntity::class,
            parentColumns = ["id"],
            childColumns = ["quizId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("quizId")]
)
data class QuestionEntity(
    @PrimaryKey
    val id: String,
    val quizId: String,
    val questionText: String,
    val correctAnswer: String,
    val incorrectAnswers: String, // JSON array stored as string
    val category: String,
    val difficulty: String,
    val questionOrder: Int // Order of question in the quiz (0-9)
)
