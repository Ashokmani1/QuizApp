package com.example.quizapp.domain.model

/**
 * Domain model representing a quiz question.
 */
data class Question(
    val id: String,
    val quizId: String,
    val questionText: String,
    val correctAnswer: String,
    val options: List<String>, // Shuffled options including correct answer
    val category: String,
    val difficulty: String,
    val questionOrder: Int
) {
    /**
     * Check if the given answer is correct.
     */
    fun isCorrectAnswer(answer: String): Boolean = answer == correctAnswer
    
    /**
     * Get the index of the correct answer in options.
     */
    fun correctAnswerIndex(): Int = options.indexOf(correctAnswer)
}
