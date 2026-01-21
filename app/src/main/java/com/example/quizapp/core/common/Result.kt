package com.example.quizapp.core.common

/**
 * A generic wrapper class for handling success and error states.
 * Provides functional operators for chaining operations.
 */
sealed class Result<out T> {

    data class Success<T>(val data: T) : Result<T>()

    data class Error(val error: AppError) : Result<Nothing>()

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun <T> error(error: AppError): Result<T> = Error(error)
    }
}

