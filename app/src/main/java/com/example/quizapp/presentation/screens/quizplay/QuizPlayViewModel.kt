package com.example.quizapp.presentation.screens.quizplay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.core.common.Constants
import com.example.quizapp.core.common.Result
import com.example.quizapp.core.common.UiText
import com.example.quizapp.core.common.toUiText
import com.example.quizapp.domain.usecase.CompleteQuizUseCase
import com.example.quizapp.domain.usecase.GetQuizSessionUseCase
import com.example.quizapp.domain.usecase.SubmitAnswerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizPlayViewModel @Inject constructor(
    private val getQuizSessionUseCase: GetQuizSessionUseCase,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val completeQuizUseCase: CompleteQuizUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    companion object {
        private const val KEY_TIME_REMAINING = "time_remaining"
        private const val KEY_CURRENT_QUESTION = "current_question"
    }
    
    private val _uiState = MutableStateFlow(QuizPlayContract.UiState())
    val uiState: StateFlow<QuizPlayContract.UiState> = _uiState.asStateFlow()
    
    private val _sideEffects = Channel<QuizPlayContract.SideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()
    
    private var timerJob: Job? = null
    private var questionStartTime: Long = 0
    
    fun onIntent(intent: QuizPlayContract.Intent) {
        when (intent) {
            is QuizPlayContract.Intent.LoadQuiz -> loadQuiz(intent.quizId)
            is QuizPlayContract.Intent.SelectAnswer -> selectAnswer(intent.answer)
            is QuizPlayContract.Intent.SubmitAnswer -> submitAnswer()
            is QuizPlayContract.Intent.NextQuestion -> nextQuestion()
            is QuizPlayContract.Intent.TimerTick -> timerTick()
            is QuizPlayContract.Intent.PauseTimer -> pauseTimer()
            is QuizPlayContract.Intent.ResumeTimer -> resumeTimer()
            is QuizPlayContract.Intent.TimeExpired -> handleTimeExpired()
        }
    }
    
    private fun loadQuiz(quizId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = getQuizSessionUseCase(quizId)) {
                is Result.Success -> {
                    val session = result.data
                    val totalTime = Constants.DEFAULT_QUESTION_TIME_SECONDS
                    
                    // Restore state if available
                    val savedTime = savedStateHandle.get<Int>(KEY_TIME_REMAINING)
                    val savedQuestion = savedStateHandle.get<Int>(KEY_CURRENT_QUESTION)
                    
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            questions = session.questions,
                            attemptId = session.attempt.id,
                            currentQuestionIndex = savedQuestion ?: 0,
                            timeRemainingSeconds = savedTime ?: totalTime,
                            totalTimeSeconds = totalTime,
                            answeredQuestions = List(session.questions.size) { null }
                        )
                    }
                    
                    startTimer()
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.error.toUiText())
                    }
                }
            }
        }
    }
    
    private fun selectAnswer(answer: String) {
        if (_uiState.value.showResult || _uiState.value.isSubmitting) return
        
        _uiState.update { it.copy(selectedAnswer = answer) }
    }
    
    private fun submitAnswer() {
        val state = _uiState.value
        val currentQuestion = state.currentQuestion ?: return
        val selectedAnswer = state.selectedAnswer
        
        // Allow submission if answer is selected OR time has expired
        if (selectedAnswer == null && state.timeRemainingSeconds > 0) return
        if (state.isSubmitting || state.showResult) return
        
        pauseTimer()
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            
            val timeSpent = state.totalTimeSeconds - state.timeRemainingSeconds
            val wasTimedOut = state.hasTimedOut
            val answerToSubmit = selectedAnswer ?: ""
            
            val result = submitAnswerUseCase(
                attemptId = state.attemptId,
                question = currentQuestion,
                selectedAnswer = answerToSubmit,
                timeSpentSeconds = timeSpent,
                wasTimedOut = wasTimedOut
            )
            
            when (result) {
                is Result.Success -> {
                    val isCorrect = result.data.isCorrect
                    val newAnsweredQuestions = state.answeredQuestions.toMutableList()
                    newAnsweredQuestions[state.currentQuestionIndex] = isCorrect
                    
                    _uiState.update { 
                        it.copy(
                            isSubmitting = false,
                            showResult = true,
                            isCorrect = isCorrect,
                            answeredQuestions = newAnsweredQuestions
                        )
                    }
                    
                    // Send side effect for feedback
                    if (isCorrect) {
                        _sideEffects.send(QuizPlayContract.SideEffect.PlayCorrectSound)
                    } else {
                        _sideEffects.send(QuizPlayContract.SideEffect.Vibrate)
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isSubmitting = false,
                            error = result.error.toUiText()
                        )
                    }
                }
            }
        }
    }
    
    private fun nextQuestion() {
        val state = _uiState.value
        
        if (state.isLastQuestion) {
            // Complete quiz
            completeQuiz()
        } else {
            // Move to next question
            val nextIndex = state.currentQuestionIndex + 1
            val resetTime = Constants.DEFAULT_QUESTION_TIME_SECONDS
            
            _uiState.update { 
                it.copy(
                    currentQuestionIndex = nextIndex,
                    selectedAnswer = null,
                    showResult = false,
                    isCorrect = false,
                    timeRemainingSeconds = resetTime,
                    isPaused = false
                )
            }
            
            // Save state for rotation
            savedStateHandle[KEY_CURRENT_QUESTION] = nextIndex
            savedStateHandle[KEY_TIME_REMAINING] = resetTime
            
            startTimer()
        }
    }
    
    private fun completeQuiz() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = completeQuizUseCase(_uiState.value.attemptId)) {
                is Result.Success -> {
                    _sideEffects.send(
                        QuizPlayContract.SideEffect.NavigateToResult(result.data.id)
                    )
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.error.toUiText())
                    }
                }
            }
        }
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        questionStartTime = System.currentTimeMillis()
        _uiState.update { it.copy(isPaused = false) }
        
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val currentState = _uiState.value
                if (currentState.isPaused || currentState.showResult) {
                    continue
                }
                
                val newTime = currentState.timeRemainingSeconds - 1
                _uiState.update { it.copy(timeRemainingSeconds = newTime) }
                savedStateHandle[KEY_TIME_REMAINING] = newTime
                
                if (newTime <= 0) {
                    onIntent(QuizPlayContract.Intent.TimeExpired)
                    break
                }
            }
        }
    }
    
    private fun timerTick() {
        // Timer ticks are now handled directly in startTimer coroutine
    }
    
    private fun pauseTimer() {
        _uiState.update { it.copy(isPaused = true) }
    }
    
    private fun resumeTimer() {
        _uiState.update { it.copy(isPaused = false) }
    }
    
    private fun handleTimeExpired() {
        if (!_uiState.value.showResult && !_uiState.value.isSubmitting) {
            _uiState.update { it.copy(isPaused = true) }
            submitAnswer()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
