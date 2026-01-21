package com.example.quizapp.domain.model

/**
 * Domain model representing a student's answer to a question.
 */
data class Answer(
    val id: String,
    val attemptId: String,
    val questionId: String,
    val selectedAnswer: String,
    val isCorrect: Boolean,
    val answeredAt: Long = System.currentTimeMillis(),
    val timeSpentSeconds: Int = 0,
    val wasTimedOut: Boolean = false
)
