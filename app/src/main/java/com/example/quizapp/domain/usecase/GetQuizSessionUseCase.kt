package com.example.quizapp.domain.usecase

import com.example.quizapp.core.common.Result
import com.example.quizapp.domain.model.Attempt
import com.example.quizapp.domain.model.Question
import com.example.quizapp.domain.repository.QuizRepository
import javax.inject.Inject

/**
 * Use case for getting quiz session data including questions and creating an attempt.
 */
class GetQuizSessionUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    data class QuizSession(
        val attempt: Attempt,
        val questions: List<Question>
    )
    
    suspend operator fun invoke(quizId: String): Result<QuizSession> {
        // Get questions
        val questionsResult = repository.getQuestionsForQuiz(quizId)
        if (questionsResult is Result.Error) {
            return Result.error(questionsResult.error)
        }
        
        // Create new attempt
        val attemptResult = repository.createAttempt(quizId)
        if (attemptResult is Result.Error) {
            return Result.error(attemptResult.error)
        }
        
        return Result.success(
            QuizSession(
                attempt = (attemptResult as Result.Success).data,
                questions = (questionsResult as Result.Success).data
            )
        )
    }
}
