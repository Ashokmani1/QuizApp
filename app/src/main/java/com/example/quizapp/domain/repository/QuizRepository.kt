package com.example.quizapp.domain.repository

import com.example.quizapp.core.common.Result
import com.example.quizapp.domain.model.Answer
import com.example.quizapp.domain.model.Attempt
import com.example.quizapp.domain.model.Question
import com.example.quizapp.domain.model.Quiz
import com.example.quizapp.domain.model.QuizAnalytics
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for quiz operations.
 * Defined in domain layer, implemented in data layer.
 */
interface QuizRepository {
    
    // Quiz Operations
    suspend fun createQuizWithQuestions(quizName: String): Result<Quiz>
    suspend fun getQuizById(quizId: String): Result<Quiz>
    suspend fun getQuizByName(name: String): Result<Quiz?>
    fun observeAllQuizzes(): Flow<List<Quiz>>
    suspend fun getAllQuizzes(): Result<List<Quiz>>
    
    // Question Operations
    suspend fun getQuestionsForQuiz(quizId: String): Result<List<Question>>
    fun observeQuestionsForQuiz(quizId: String): Flow<List<Question>>
    
    // Attempt Operations
    suspend fun createAttempt(quizId: String): Result<Attempt>
    suspend fun completeAttempt(attemptId: String): Result<Attempt>
    suspend fun getAttemptById(attemptId: String): Result<Attempt>
    suspend fun getAttemptsForQuiz(quizId: String): Result<List<Attempt>>
    fun observeAttemptsForQuiz(quizId: String): Flow<List<Attempt>>
    
    // Answer Operations
    suspend fun saveAnswer(answer: Answer): Result<Answer>
    suspend fun getAnswersForAttempt(attemptId: String): Result<List<Answer>>
    fun observeAnswersForAttempt(attemptId: String): Flow<List<Answer>>
    
    // Analytics Operations (Teacher Mode)
    suspend fun getQuizAnalytics(quizId: String): Result<QuizAnalytics>
    suspend fun getAllQuizzesWithAnalytics(): Result<List<QuizAnalytics>>
}
