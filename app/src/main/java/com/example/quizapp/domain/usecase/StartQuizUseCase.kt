package com.example.quizapp.domain.usecase

import com.example.quizapp.core.common.Result
import com.example.quizapp.domain.model.Quiz
import com.example.quizapp.domain.repository.QuizRepository
import javax.inject.Inject

/**
 * Use case for starting a new quiz or resuming an existing one.
 * Fetches questions from API and caches them if the quiz is new.
 */
class StartQuizUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(quizName: String): Result<Quiz> {
        return repository.createQuizWithQuestions(quizName)
    }
}
