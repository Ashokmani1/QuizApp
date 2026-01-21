package com.example.quizapp.domain.usecase

import com.example.quizapp.core.common.Result
import com.example.quizapp.domain.model.Attempt
import com.example.quizapp.domain.repository.QuizRepository
import javax.inject.Inject

/**
 * Use case for completing a quiz attempt.
 * Calculates final score and marks the attempt as completed.
 */
class CompleteQuizUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(attemptId: String): Result<Attempt> {
        return repository.completeAttempt(attemptId)
    }
}
