package com.example.quizapp.data.repository

import com.example.quizapp.core.common.AppError
import com.example.quizapp.core.common.Constants
import com.example.quizapp.core.common.Result
import com.example.quizapp.core.di.IoDispatcher
import com.example.quizapp.core.network.safeApiCall
import com.example.quizapp.core.network.safeDbCall
import com.example.quizapp.data.local.dao.AnswerDao
import com.example.quizapp.data.local.dao.AttemptDao
import com.example.quizapp.data.local.dao.QuestionDao
import com.example.quizapp.data.local.dao.QuizDao
import com.example.quizapp.data.local.entity.QuizEntity
import com.example.quizapp.data.mapper.toDomain
import com.example.quizapp.data.mapper.toEntity
import com.example.quizapp.data.remote.QuizApiService
import com.example.quizapp.domain.model.Answer
import com.example.quizapp.domain.model.Attempt
import com.example.quizapp.domain.model.Question
import com.example.quizapp.domain.model.Quiz
import com.example.quizapp.domain.model.QuizAnalytics
import com.example.quizapp.domain.model.QuestionAnalytics
import com.example.quizapp.domain.repository.QuizRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val quizApiService: QuizApiService,
    private val quizDao: QuizDao,
    private val questionDao: QuestionDao,
    private val attemptDao: AttemptDao,
    private val answerDao: AnswerDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : QuizRepository {

    // ==================== Quiz Operations ====================
    
    override suspend fun createQuizWithQuestions(quizName: String): Result<Quiz> = withContext(ioDispatcher) {
        // Check if quiz with same name exists
        val existingQuiz = quizDao.getQuizByName(quizName)
        if (existingQuiz != null) {
            return@withContext Result.success(existingQuiz.toDomain())
        }
        
        // Fetch questions from API
        val questionsResult = safeApiCall { quizApiService.getQuestions(Constants.QUESTIONS_LIMIT) }
        
        when (questionsResult) {
            is Result.Error -> return@withContext Result.error(questionsResult.error)
            is Result.Success -> {
                val quizId = UUID.randomUUID().toString()
                val quiz = QuizEntity(
                    id = quizId,
                    name = quizName,
                    totalQuestions = questionsResult.data.size,
                    timePerQuestionSeconds = Constants.DEFAULT_QUESTION_TIME_SECONDS
                )
                
                // Save quiz
                quizDao.insertQuiz(quiz)
                
                // Save questions
                val questionEntities = questionsResult.data.mapIndexed { index, dto ->
                    dto.toEntity(quizId, index)
                }
                questionDao.insertQuestions(questionEntities)
                
                return@withContext Result.success(quiz.toDomain())
            }
        }
    }
    
    override suspend fun getQuizById(quizId: String): Result<Quiz> = withContext(ioDispatcher) {
        safeDbCall {
            quizDao.getQuizById(quizId)?.toDomain() 
                ?: throw Exception("Quiz not found")
        }.let { result ->
            when (result) {
                is Result.Success -> result
                is Result.Error -> Result.error(AppError.Quiz.NotFound)
            }
        }
    }
    
    override suspend fun getQuizByName(name: String): Result<Quiz?> = withContext(ioDispatcher) {
        safeDbCall { quizDao.getQuizByName(name)?.toDomain() }
    }
    
    override fun observeAllQuizzes(): Flow<List<Quiz>> =
        quizDao.getAllQuizzes()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    
    override suspend fun getAllQuizzes(): Result<List<Quiz>> = withContext(ioDispatcher) {
        safeDbCall { quizDao.getAllQuizzesList().map { it.toDomain() } }
    }

    // ==================== Question Operations ====================
    
    override suspend fun getQuestionsForQuiz(quizId: String): Result<List<Question>> = withContext(ioDispatcher) {
        safeDbCall { 
            questionDao.getQuestionsForQuiz(quizId).map { it.toDomain() }
        }
    }
    
    override fun observeQuestionsForQuiz(quizId: String): Flow<List<Question>> =
        questionDao.observeQuestionsForQuiz(quizId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    // ==================== Attempt Operations ====================
    
    override suspend fun createAttempt(quizId: String): Result<Attempt> = withContext(ioDispatcher) {
        val quiz = quizDao.getQuizById(quizId) ?: return@withContext Result.error(AppError.Quiz.NotFound)
        
        val attempt = Attempt(
            id = UUID.randomUUID().toString(),
            quizId = quizId,
            startedAt = System.currentTimeMillis(),
            totalQuestions = quiz.totalQuestions
        )
        
        safeDbCall {
            attemptDao.insertAttempt(attempt.toEntity())
            attempt
        }
    }
    
    override suspend fun completeAttempt(attemptId: String): Result<Attempt> = withContext(ioDispatcher) {
        val correctCount = answerDao.getCorrectAnswerCount(attemptId)
        val completedAt = System.currentTimeMillis()
        
        safeDbCall {
            attemptDao.completeAttempt(attemptId, completedAt, correctCount)
            attemptDao.getAttemptById(attemptId)?.toDomain()
                ?: throw Exception("Attempt not found")
        }
    }
    
    override suspend fun getAttemptById(attemptId: String): Result<Attempt> = withContext(ioDispatcher) {
        safeDbCall {
            attemptDao.getAttemptById(attemptId)?.toDomain()
                ?: throw Exception("Attempt not found")
        }
    }
    
    override suspend fun getAttemptsForQuiz(quizId: String): Result<List<Attempt>> = withContext(ioDispatcher) {
        safeDbCall { attemptDao.getAttemptsForQuiz(quizId).map { it.toDomain() } }
    }
    
    override fun observeAttemptsForQuiz(quizId: String): Flow<List<Attempt>> =
        attemptDao.observeAttemptsForQuiz(quizId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    // ==================== Answer Operations ====================
    
    override suspend fun saveAnswer(answer: Answer): Result<Answer> = withContext(ioDispatcher) {
        safeDbCall {
            answerDao.insertAnswer(answer.toEntity())
            answer
        }
    }
    
    override suspend fun getAnswersForAttempt(attemptId: String): Result<List<Answer>> = withContext(ioDispatcher) {
        safeDbCall { answerDao.getAnswersForAttempt(attemptId).map { it.toDomain() } }
    }
    
    override fun observeAnswersForAttempt(attemptId: String): Flow<List<Answer>> =
        answerDao.observeAnswersForAttempt(attemptId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    // ==================== Analytics Operations (Teacher Mode) ====================
    
    override suspend fun getQuizAnalytics(quizId: String): Result<QuizAnalytics> = withContext(ioDispatcher) {
        safeDbCall {
            val quiz = quizDao.getQuizById(quizId)?.toDomain() 
                ?: throw Exception("Quiz not found")
            val questions = questionDao.getQuestionsForQuiz(quizId).map { it.toDomain() }
            val attempts = attemptDao.getAttemptsForQuiz(quizId).map { it.toDomain() }
            val completedAttempts = attempts.filter { it.isCompleted }
            
            val questionAnalytics = questions.map { question ->
                val allAnswers = answerDao.getAllAnswersForQuestion(quizId, question.id).map { it.toDomain() }
                val correctCount = allAnswers.count { it.isCorrect }
                val wrongCount = allAnswers.size - correctCount
                val latestAnswer = allAnswers.maxByOrNull { it.answeredAt }
                
                QuestionAnalytics(
                    question = question,
                    totalAnswers = allAnswers.size,
                    correctCount = correctCount,
                    wrongCount = wrongCount,
                    accuracy = if (allAnswers.isNotEmpty()) (correctCount.toFloat() / allAnswers.size) * 100 else 0f,
                    latestAnswer = latestAnswer
                )
            }
            
            QuizAnalytics(
                quiz = quiz,
                totalAttempts = attempts.size,
                completedAttempts = completedAttempts.size,
                questionAnalytics = questionAnalytics
            )
        }
    }
    
    override suspend fun getAllQuizzesWithAnalytics(): Result<List<QuizAnalytics>> = withContext(ioDispatcher) {
        safeDbCall {
            val quizzes = quizDao.getAllQuizzesList()
            quizzes.map { quizEntity ->
                val quiz = quizEntity.toDomain()
                val questions = questionDao.getQuestionsForQuiz(quiz.id).map { it.toDomain() }
                val attempts = attemptDao.getAttemptsForQuiz(quiz.id).map { it.toDomain() }
                val completedAttempts = attempts.filter { it.isCompleted }
                
                val questionAnalytics = questions.map { question ->
                    val allAnswers = answerDao.getAllAnswersForQuestion(quiz.id, question.id).map { it.toDomain() }
                    val correctCount = allAnswers.count { it.isCorrect }
                    val wrongCount = allAnswers.size - correctCount
                    val latestAnswer = allAnswers.maxByOrNull { it.answeredAt }
                    
                    QuestionAnalytics(
                        question = question,
                        totalAnswers = allAnswers.size,
                        correctCount = correctCount,
                        wrongCount = wrongCount,
                        accuracy = if (allAnswers.isNotEmpty()) (correctCount.toFloat() / allAnswers.size) * 100 else 0f,
                        latestAnswer = latestAnswer
                    )
                }
                
                QuizAnalytics(
                    quiz = quiz,
                    totalAttempts = attempts.size,
                    completedAttempts = completedAttempts.size,
                    questionAnalytics = questionAnalytics
                )
            }
        }
    }
}
