package com.example.quizapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.quizapp.data.local.entity.AttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttemptDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: AttemptEntity)
    
    @Update
    suspend fun updateAttempt(attempt: AttemptEntity)
    
    @Query("SELECT * FROM attempts WHERE id = :attemptId")
    suspend fun getAttemptById(attemptId: String): AttemptEntity?
    
    @Query("SELECT * FROM attempts WHERE quizId = :quizId ORDER BY startedAt DESC")
    suspend fun getAttemptsForQuiz(quizId: String): List<AttemptEntity>
    
    @Query("SELECT * FROM attempts WHERE quizId = :quizId ORDER BY startedAt DESC")
    fun observeAttemptsForQuiz(quizId: String): Flow<List<AttemptEntity>>
    
    @Query("SELECT * FROM attempts WHERE quizId = :quizId ORDER BY startedAt DESC LIMIT 1")
    suspend fun getLatestAttemptForQuiz(quizId: String): AttemptEntity?
    
    @Query("SELECT COUNT(*) FROM attempts WHERE quizId = :quizId AND isCompleted = 1")
    suspend fun getCompletedAttemptCount(quizId: String): Int
    
    @Query("DELETE FROM attempts WHERE id = :attemptId")
    suspend fun deleteAttempt(attemptId: String)
    
    @Query("""
        UPDATE attempts 
        SET isCompleted = 1, 
            completedAt = :completedAt, 
            totalCorrect = :totalCorrect 
        WHERE id = :attemptId
    """)
    suspend fun completeAttempt(attemptId: String, completedAt: Long, totalCorrect: Int)
}
