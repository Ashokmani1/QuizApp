package com.example.quizapp.domain.usecase

import com.example.quizapp.core.common.Result
import com.example.quizapp.domain.model.QuizAnalytics
import com.example.quizapp.domain.repository.QuizRepository
import javax.inject.Inject

/**
 * Use case for getting quiz analytics for Teacher mode.
 * Aggregates data across all attempts without making API calls.
 */
class GetQuizAnalyticsUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(quizId: String): Result<QuizAnalytics> {
        return repository.getQuizAnalytics(quizId)
    }
}
