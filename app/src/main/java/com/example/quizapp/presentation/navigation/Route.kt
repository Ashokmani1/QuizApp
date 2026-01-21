package com.example.quizapp.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using Kotlin Serialization.
 */
sealed interface Route {
    
    @Serializable
    data object ModeSelection : Route
    
    @Serializable
    data object QuizNameInput : Route
    
    @Serializable
    data class QuizPlay(val quizId: String) : Route
    
    @Serializable
    data class QuizResult(val attemptId: String) : Route
    
    @Serializable
    data object TeacherDashboard : Route
    
    @Serializable
    data class QuizAnalytics(val quizId: String) : Route
}
