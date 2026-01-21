package com.example.quizapp.presentation.screens.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.core.common.Result
import com.example.quizapp.domain.model.QuizAnalytics
import com.example.quizapp.domain.usecase.GetAllQuizzesAnalyticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeacherDashboardUiState(
    val isLoading: Boolean = true,
    val quizzes: List<QuizAnalytics> = emptyList(),
    val error: String? = null
) {
    val totalAttempts: Int
        get() = quizzes.sumOf { it.totalAttempts }
    
    val overallAccuracy: Float
        get() {
            val totalCorrect = quizzes.sumOf { analytics ->
                analytics.questionAnalytics.sumOf { it.correctCount }
            }
            val totalAnswers = quizzes.sumOf { analytics ->
                analytics.questionAnalytics.sumOf { it.totalAnswers }
            }
            return if (totalAnswers > 0) (totalCorrect.toFloat() / totalAnswers) * 100 else 0f
        }
}

@HiltViewModel
class TeacherDashboardViewModel @Inject constructor(
    private val getAllQuizzesAnalyticsUseCase: GetAllQuizzesAnalyticsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TeacherDashboardUiState())
    val uiState: StateFlow<TeacherDashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadQuizzes()
    }
    
    fun loadQuizzes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = getAllQuizzesAnalyticsUseCase()) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            quizzes = result.data
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.error.message
                        )
                    }
                }
            }
        }
    }
}
