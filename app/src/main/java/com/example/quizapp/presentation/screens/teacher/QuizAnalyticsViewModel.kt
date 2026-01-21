package com.example.quizapp.presentation.screens.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.core.common.Result
import com.example.quizapp.domain.model.QuizAnalytics
import com.example.quizapp.domain.usecase.GetQuizAnalyticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizAnalyticsUiState(
    val isLoading: Boolean = true,
    val analytics: QuizAnalytics? = null,
    val error: String? = null
)

@HiltViewModel
class QuizAnalyticsViewModel @Inject constructor(
    private val getQuizAnalyticsUseCase: GetQuizAnalyticsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(QuizAnalyticsUiState())
    val uiState: StateFlow<QuizAnalyticsUiState> = _uiState.asStateFlow()
    
    fun loadAnalytics(quizId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = getQuizAnalyticsUseCase(quizId)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            analytics = result.data
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
