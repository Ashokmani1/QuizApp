package com.example.quizapp.domain.model

/**
 * Analytics data for Teacher mode.
 */
data class QuizAnalytics(
    val quiz: Quiz,
    val totalAttempts: Int,
    val completedAttempts: Int,
    val questionAnalytics: List<QuestionAnalytics>
) {
    /**
     * Overall accuracy across all questions and attempts.
     */
    val overallAccuracy: Float
        get() {
            val totalCorrect = questionAnalytics.sumOf { it.correctCount }
            val totalAnswers = questionAnalytics.sumOf { it.totalAnswers }
            return if (totalAnswers > 0) (totalCorrect.toFloat() / totalAnswers) * 100 else 0f
        }
}

/**
 * Analytics data for a single question.
 */
data class QuestionAnalytics(
    val question: Question,
    val totalAnswers: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val accuracy: Float,
    val latestAnswer: Answer?
)
