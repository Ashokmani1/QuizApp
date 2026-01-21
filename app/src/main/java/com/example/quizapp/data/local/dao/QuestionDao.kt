package com.example.quizapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizapp.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)
    
    @Query("SELECT * FROM questions WHERE quizId = :quizId ORDER BY questionOrder ASC")
    suspend fun getQuestionsForQuiz(quizId: String): List<QuestionEntity>
    
    @Query("SELECT * FROM questions WHERE quizId = :quizId ORDER BY questionOrder ASC")
    fun observeQuestionsForQuiz(quizId: String): Flow<List<QuestionEntity>>
    
    @Query("SELECT * FROM questions WHERE id = :questionId")
    suspend fun getQuestionById(questionId: String): QuestionEntity?
    
    @Query("DELETE FROM questions WHERE quizId = :quizId")
    suspend fun deleteQuestionsForQuiz(quizId: String)
    
    @Query("SELECT COUNT(*) FROM questions WHERE quizId = :quizId")
    suspend fun getQuestionCount(quizId: String): Int
}
