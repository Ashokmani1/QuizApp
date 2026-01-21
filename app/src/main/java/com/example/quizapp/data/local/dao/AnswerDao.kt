package com.example.quizapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizapp.data.local.entity.AnswerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: AnswerEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<AnswerEntity>)
    
    @Query("SELECT * FROM answers WHERE attemptId = :attemptId ORDER BY answeredAt ASC")
    suspend fun getAnswersForAttempt(attemptId: String): List<AnswerEntity>
    
    @Query("SELECT * FROM answers WHERE attemptId = :attemptId ORDER BY answeredAt ASC")
    fun observeAnswersForAttempt(attemptId: String): Flow<List<AnswerEntity>>
    
    @Query("SELECT * FROM answers WHERE questionId = :questionId AND attemptId = :attemptId")
    suspend fun getAnswerForQuestion(attemptId: String, questionId: String): AnswerEntity?
    
    @Query("""
        SELECT a.* FROM answers a
        INNER JOIN attempts att ON a.attemptId = att.id
        WHERE att.quizId = :quizId
        ORDER BY a.answeredAt DESC
    """)
    suspend fun getAllAnswersForQuiz(quizId: String): List<AnswerEntity>
    
    @Query("""
        SELECT a.* FROM answers a
        INNER JOIN attempts att ON a.attemptId = att.id
        WHERE a.questionId = :questionId AND att.quizId = :quizId
    """)
    suspend fun getAllAnswersForQuestion(quizId: String, questionId: String): List<AnswerEntity>
    
    @Query("SELECT COUNT(*) FROM answers WHERE attemptId = :attemptId AND isCorrect = 1")
    suspend fun getCorrectAnswerCount(attemptId: String): Int
    
    @Query("DELETE FROM answers WHERE attemptId = :attemptId")
    suspend fun deleteAnswersForAttempt(attemptId: String)
}
