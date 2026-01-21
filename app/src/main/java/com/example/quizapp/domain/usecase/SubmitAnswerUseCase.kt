package com.example.quizapp.domain.usecase

import com.example.quizapp.core.common.Result
import com.example.quizapp.domain.model.Answer
import com.example.quizapp.domain.model.Question
import com.example.quizapp.domain.repository.QuizRepository
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for submitting an answer to a question.
 * Validates the answer and stores the result.
 */
class SubmitAnswerUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(
        attemptId: String,
        question: Question,
        selectedAnswer: String,
        timeSpentSeconds: Int,
        wasTimedOut: Boolean
    ): Result<Answer> {
        val isCorrect = question.isCorrectAnswer(selectedAnswer) && !wasTimedOut
        
        val answer = Answer(
            id = UUID.randomUUID().toString(),
            attemptId = attemptId,
            questionId = question.id,
            selectedAnswer = selectedAnswer,
            isCorrect = isCorrect,
            answeredAt = System.currentTimeMillis(),
            timeSpentSeconds = timeSpentSeconds,
            wasTimedOut = wasTimedOut
        )
        
        return repository.saveAnswer(answer)
    }
}
