package com.example.quizapp.domain.model

/**
 * Domain model representing a quiz attempt.
 * Each time a student takes a quiz, a new attempt is created.
 */
data class Attempt(
    val id: String,
    val quizId: String,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val totalCorrect: Int = 0,
    val totalQuestions: Int = 10,
    val isCompleted: Boolean = false
) {
    /**
     * Calculate the score percentage.
     */
    val scorePercentage: Float
        get() = if (totalQuestions > 0) (totalCorrect.toFloat() / totalQuestions) * 100 else 0f
    
    /**
     * Get formatted score string.
     */
    val scoreText: String
        get() = "$totalCorrect / $totalQuestions"
    
    /**
     * Duration of the attempt in milliseconds.
     */
    val durationMs: Long?
        get() = completedAt?.let { it - startedAt }
}
