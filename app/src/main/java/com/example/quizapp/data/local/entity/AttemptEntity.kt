package com.example.quizapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a quiz attempt.
 * Each time a student takes a quiz, a new attempt is created.
 */
@Entity(
    tableName = "attempts",
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
data class AttemptEntity(
    @PrimaryKey
    val id: String,
    val quizId: String,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val totalCorrect: Int = 0,
    val totalQuestions: Int = 10,
    val isCompleted: Boolean = false
)
