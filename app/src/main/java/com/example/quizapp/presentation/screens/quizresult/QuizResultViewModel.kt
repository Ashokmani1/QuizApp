package com.example.quizapp.presentation.screens.quizresult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.core.common.Result
import com.example.quizapp.domain.model.Attempt
import com.example.quizapp.domain.model.Quiz
import com.example.quizapp.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizResultUiState(
    val isLoading: Boolean = true,
    val attempt: Attempt? = null,
    val quiz: Quiz? = null,
    val previousAttempts: List<Attempt> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class QuizResultViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(QuizResultUiState())
    val uiState: StateFlow<QuizResultUiState> = _uiState.asStateFlow()
    
    fun loadResult(attemptId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val attemptResult = repository.getAttemptById(attemptId)) {
                is Result.Success -> {
                    val attempt = attemptResult.data
                    
                    // Load quiz details
                    val quizResult = repository.getQuizById(attempt.quizId)
                    val quiz = (quizResult as? Result.Success)?.data
                    
                    // Load previous attempts for this quiz
                    val previousAttemptsResult = repository.getAttemptsForQuiz(attempt.quizId)
                    val previousAttempts = (previousAttemptsResult as? Result.Success)?.data
                        ?.filter { it.id != attemptId && it.isCompleted }
                        ?: emptyList()
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            attempt = attempt,
                            quiz = quiz,
                            previousAttempts = previousAttempts
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = attemptResult.error.message
                        )
                    }
                }
            }
        }
    }
}
