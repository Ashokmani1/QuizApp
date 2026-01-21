package com.example.quizapp.data.mapper

import com.example.quizapp.data.local.entity.AnswerEntity
import com.example.quizapp.data.local.entity.AttemptEntity
import com.example.quizapp.data.local.entity.QuestionEntity
import com.example.quizapp.data.local.entity.QuizEntity
import com.example.quizapp.data.remote.dto.TriviaQuestionDto
import com.example.quizapp.domain.model.Answer
import com.example.quizapp.domain.model.Attempt
import com.example.quizapp.domain.model.Question
import com.example.quizapp.domain.model.Quiz
import com.google.gson.Gson

private val gson = Gson()

// DTO to Entity Mappers
fun TriviaQuestionDto.toEntity(quizId: String, order: Int): QuestionEntity = QuestionEntity(
    id = id,
    quizId = quizId,
    questionText = question.text,
    correctAnswer = correctAnswer,
    incorrectAnswers = gson.toJson(incorrectAnswers),
    category = category,
    difficulty = difficulty,
    questionOrder = order
)

// Entity to Domain Mappers
fun QuizEntity.toDomain(): Quiz = Quiz(
    id = id,
    name = name,
    createdAt = createdAt,
    totalQuestions = totalQuestions,
    timePerQuestionSeconds = timePerQuestionSeconds
)

fun QuestionEntity.toDomain(): Question {
    val incorrectList: List<String> = gson.fromJson(incorrectAnswers, Array<String>::class.java).toList()
    val allAnswers = (incorrectList + correctAnswer).shuffled()
    
    return Question(
        id = id,
        quizId = quizId,
        questionText = questionText,
        correctAnswer = correctAnswer,
        options = allAnswers,
        category = category,
        difficulty = difficulty,
        questionOrder = questionOrder
    )
}

fun AttemptEntity.toDomain(): Attempt = Attempt(
    id = id,
    quizId = quizId,
    startedAt = startedAt,
    completedAt = completedAt,
    totalCorrect = totalCorrect,
    totalQuestions = totalQuestions,
    isCompleted = isCompleted
)

fun AnswerEntity.toDomain(): Answer = Answer(
    id = id,
    attemptId = attemptId,
    questionId = questionId,
    selectedAnswer = selectedAnswer,
    isCorrect = isCorrect,
    answeredAt = answeredAt,
    timeSpentSeconds = timeSpentSeconds,
    wasTimedOut = wasTimedOut
)

// Domain to Entity Mappers
fun Quiz.toEntity(): QuizEntity = QuizEntity(
    id = id,
    name = name,
    createdAt = createdAt,
    totalQuestions = totalQuestions,
    timePerQuestionSeconds = timePerQuestionSeconds
)

fun Attempt.toEntity(): AttemptEntity = AttemptEntity(
    id = id,
    quizId = quizId,
    startedAt = startedAt,
    completedAt = completedAt,
    totalCorrect = totalCorrect,
    totalQuestions = totalQuestions,
    isCompleted = isCompleted
)

fun Answer.toEntity(): AnswerEntity = AnswerEntity(
    id = id,
    attemptId = attemptId,
    questionId = questionId,
    selectedAnswer = selectedAnswer,
    isCorrect = isCorrect,
    answeredAt = answeredAt,
    timeSpentSeconds = timeSpentSeconds,
    wasTimedOut = wasTimedOut
)
