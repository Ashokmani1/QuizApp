package com.example.quizapp.core.common

/**
 * A generic wrapper class for handling success and error states.
 * Provides functional operators for chaining operations.
 */
sealed class Result<out T> {

    data class Success<T>(val data: T) : Result<T>()

    data class Error(val error: AppError) : Result<Nothing>()
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    
    fun getOrNull(): T? = (this as? Success)?.data
    
    fun errorOrNull(): AppError? = (this as? Error)?.error
    
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }
    
    inline fun <R> flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
        is Success -> transform(data)
        is Error -> this
    }
    
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onError(action: (AppError) -> Unit): Result<T> {
        if (this is Error) action(error)
        return this
    }
    
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw error.toException()
    }
    
    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun <T> error(error: AppError): Result<T> = Error(error)
    }
}

/**
 * Get the value or return a default value
 */
fun <T> Result<T>.getOrDefault(default: @UnsafeVariance T): T = when (this) {
    is Result.Success -> data
    is Result.Error -> default
}

/**
 * Extension to convert Kotlin Result to our custom Result
 */
inline fun <T> runCatching(block: () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Exception) {
    Result.Error(AppError.Unknown(e))
}
