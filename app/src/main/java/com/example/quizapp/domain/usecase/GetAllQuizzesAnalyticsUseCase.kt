package com.example.quizapp.domain.usecase

import com.example.quizapp.core.common.Result
import com.example.quizapp.domain.model.QuizAnalytics
import com.example.quizapp.domain.repository.QuizRepository
import javax.inject.Inject

/**
 * Use case for getting all quizzes with their analytics for Teacher dashboard.
 * Reads only from cached data, no API calls.
 */
class GetAllQuizzesAnalyticsUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(): Result<List<QuizAnalytics>> {
        return repository.getAllQuizzesWithAnalytics()
    }
}
