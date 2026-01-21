package com.example.quizapp.presentation.screens.quizplay

import com.example.quizapp.core.common.UiText
import com.example.quizapp.domain.model.Question

/**
 * MVI Contract for Quiz Play Screen
 */
object QuizPlayContract {
    
    data class UiState(
        val isLoading: Boolean = true,
        val questions: List<Question> = emptyList(),
        val currentQuestionIndex: Int = 0,
        val selectedAnswer: String? = null,
        val timeRemainingSeconds: Int = 30,
        val totalTimeSeconds: Int = 30,
        val isSubmitting: Boolean = false,
        val showResult: Boolean = false,
        val isCorrect: Boolean = false,
        val answeredQuestions: List<Boolean?> = emptyList(), // null = not answered
        val attemptId: String = "",
        val error: UiText? = null,
        val isPaused: Boolean = false
    ) {
        val currentQuestion: Question?
            get() = questions.getOrNull(currentQuestionIndex)
        
        val isLastQuestion: Boolean
            get() = currentQuestionIndex == questions.size - 1
        
        val progress: Float
            get() = if (questions.isNotEmpty()) 
                (currentQuestionIndex + 1).toFloat() / questions.size else 0f
        
        val hasTimedOut: Boolean
            get() = timeRemainingSeconds <= 0
    }
    
    sealed interface Intent {
        data class LoadQuiz(val quizId: String) : Intent
        data class SelectAnswer(val answer: String) : Intent
        data object SubmitAnswer : Intent
        data object NextQuestion : Intent
        data object TimerTick : Intent
        data object PauseTimer : Intent
        data object ResumeTimer : Intent
        data object TimeExpired : Intent
    }
    
    sealed interface SideEffect {
        data class NavigateToResult(val attemptId: String) : SideEffect
        data class ShowError(val message: UiText) : SideEffect
        data object PlayCorrectSound : SideEffect
        data object PlayIncorrectSound : SideEffect
        data object Vibrate : SideEffect
    }
}
