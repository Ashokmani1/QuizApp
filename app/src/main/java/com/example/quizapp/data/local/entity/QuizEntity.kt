package com.example.quizapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a quiz session.
 * A quiz is created when a student enters a quiz name and fetches questions.
 */
@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val totalQuestions: Int = 10,
    val timePerQuestionSeconds: Int = 30
)
