package com.example.quizapp.presentation.screens.quizinput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.core.common.Result
import com.example.quizapp.core.common.UiText
import com.example.quizapp.core.common.toUiText
import com.example.quizapp.domain.usecase.StartQuizUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizInputViewModel @Inject constructor(
    private val startQuizUseCase: StartQuizUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(QuizInputContract.UiState())
    val uiState: StateFlow<QuizInputContract.UiState> = _uiState.asStateFlow()
    
    private val _sideEffects = Channel<QuizInputContract.SideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()
    
    fun onIntent(intent: QuizInputContract.Intent) {
        when (intent) {
            is QuizInputContract.Intent.UpdateQuizName -> updateQuizName(intent.name)
            is QuizInputContract.Intent.StartQuiz -> startQuiz()
            is QuizInputContract.Intent.ClearError -> clearError()
        }
    }
    
    private fun updateQuizName(name: String) {
        _uiState.update { 
            it.copy(
                quizName = name,
                isValid = name.trim().length >= 3,
                error = null
            )
        }
    }
    
    private fun startQuiz() {
        val quizName = _uiState.value.quizName.trim()
        
        if (quizName.length < 3) {
            _uiState.update { 
                it.copy(error = UiText.DynamicString("Quiz name must be at least 3 characters"))
            }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = startQuizUseCase(quizName)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _sideEffects.send(QuizInputContract.SideEffect.NavigateToQuiz(result.data.id))
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.error.toUiText()
                        )
                    }
                }
            }
        }
    }
    
    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
