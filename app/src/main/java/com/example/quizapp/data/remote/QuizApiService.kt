package com.example.quizapp.data.remote

import com.example.quizapp.data.remote.dto.TriviaQuestionDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for The Trivia API.
 * API Documentation: https://the-trivia-api.com/docs/v2/
 */
interface QuizApiService {
    
    @GET("v2/questions")
    suspend fun getQuestions(
        @Query("limit") limit: Int = 10
    ): List<TriviaQuestionDto>
}
