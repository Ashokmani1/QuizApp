package com.example.quizapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a student's answer to a question in an attempt.
 * Stores the selected answer and whether it was correct.
 */
@Entity(
    tableName = "answers",
    foreignKeys = [
        ForeignKey(
            entity = AttemptEntity::class,
            parentColumns = ["id"],
            childColumns = ["attemptId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("attemptId"), Index("questionId")]
)
data class AnswerEntity(
    @PrimaryKey
    val id: String,
    val attemptId: String,
    val questionId: String,
    val selectedAnswer: String,
    val isCorrect: Boolean,
    val answeredAt: Long = System.currentTimeMillis(),
    val timeSpentSeconds: Int = 0,
    val wasTimedOut: Boolean = false
)
