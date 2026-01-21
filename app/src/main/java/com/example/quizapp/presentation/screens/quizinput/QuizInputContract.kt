package com.example.quizapp.presentation.screens.quizinput

import com.example.quizapp.core.common.UiText

/**
 * MVI Contract for Quiz Name Input Screen
 */
object QuizInputContract {
    
    data class UiState(
        val quizName: String = "",
        val isLoading: Boolean = false,
        val error: UiText? = null,
        val isValid: Boolean = false
    )
    
    sealed interface Intent {
        data class UpdateQuizName(val name: String) : Intent
        data object StartQuiz : Intent
        data object ClearError : Intent
    }
    
    sealed interface SideEffect {
        data class NavigateToQuiz(val quizId: String) : SideEffect
        data class ShowError(val message: UiText) : SideEffect
    }
}
