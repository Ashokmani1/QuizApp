package com.example.quizapp.core.network

import com.example.quizapp.core.common.AppError
import com.example.quizapp.core.common.Result
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Safe wrapper for API calls that converts exceptions to AppError.
 */
suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> T
): Result<T> = try {
    Result.Success(apiCall())
} catch (e: UnknownHostException) {
    Result.Error(AppError.Network.NoConnection)
} catch (e: SocketTimeoutException) {
    Result.Error(AppError.Network.Timeout)
} catch (e: IOException) {
    Result.Error(AppError.Network.NoConnection)
} catch (e: HttpException) {
    val error = when (e.code()) {
        404 -> AppError.Quiz.NotFound
        in 500..599 -> AppError.Network.ServerError(e.code(), "Server error: ${e.message()}")
        else -> AppError.Network.ApiError("API error: ${e.message()}")
    }
    Result.Error(error)
} catch (e: Exception) {
    Result.Error(AppError.Unknown(e))
}

/**
 * Safe wrapper for database calls that converts exceptions to AppError.
 */
suspend inline fun <T> safeDbCall(
    crossinline dbCall: suspend () -> T
): Result<T> = try {
    Result.Success(dbCall())
} catch (e: Exception) {
    Result.Error(AppError.Database.QueryError(e.message ?: "Database error"))
}
