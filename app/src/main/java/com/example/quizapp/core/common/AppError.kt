package com.example.quizapp.core.common

/**
 * Sealed hierarchy of application errors for type-safe error handling.
 */
sealed class AppError(
    open val message: String,
    open val cause: Throwable? = null
) {
    /**
     * Network-related errors
     */
    sealed class Network(override val message: String) : AppError(message) {
        data object NoConnection : Network("No internet connection available")
        data object Timeout : Network("Request timed out")
        data class ServerError(val code: Int, override val message: String) : Network(message)
        data class ApiError(override val message: String) : Network(message)
    }
    
    /**
     * Quiz-related domain errors
     */
    sealed class Quiz(override val message: String) : AppError(message) {
        data object NotFound : Quiz("Quiz not found")
    }
    
    /**
     * Database/Cache errors
     */
    sealed class Database(override val message: String) : AppError(message) {
        data class QueryError(override val message: String) : Database(message)
    }

    
    /**
     * Unknown/Unexpected errors
     */
    data class Unknown(override val cause: Throwable) : AppError(
        cause.message ?: "An unexpected error occurred",
        cause
    )
    
    fun toException(): Exception = when (this) {
        is Unknown -> cause as? Exception ?: Exception(message)
        else -> Exception(message, cause)
    }
}
