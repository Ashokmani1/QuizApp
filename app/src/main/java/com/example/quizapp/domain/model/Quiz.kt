package com.example.quizapp.domain.model

/**
 * Domain model representing a quiz session.
 */
data class Quiz(
    val id: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val totalQuestions: Int = 10,
    val timePerQuestionSeconds: Int = 30
)
