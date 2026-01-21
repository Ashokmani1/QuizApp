package com.example.quizapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO matching the Trivia API v2 response structure.
 * Example response:
 * {
 *   "category": "Geography",
 *   "id": "622a1c357cc59eab6f950358",
 *   "correctAnswer": "Paris",
 *   "incorrectAnswers": ["London", "Berlin", "Madrid"],
 *   "question": { "text": "What is the capital of France?" },
 *   "difficulty": "easy"
 * }
 */
data class TriviaQuestionDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("correctAnswer")
    val correctAnswer: String,
    
    @SerializedName("incorrectAnswers")
    val incorrectAnswers: List<String>,
    
    @SerializedName("question")
    val question: QuestionTextDto,
    
    @SerializedName("difficulty")
    val difficulty: String
)

data class QuestionTextDto(
    @SerializedName("text")
    val text: String
)
