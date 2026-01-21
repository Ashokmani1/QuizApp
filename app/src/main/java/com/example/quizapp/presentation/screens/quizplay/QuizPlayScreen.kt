package com.example.quizapp.presentation.screens.quizplay

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.quizapp.presentation.components.OptionButton
import com.example.quizapp.presentation.components.OptionState
import com.example.quizapp.presentation.components.QuizProgressBar
import com.example.quizapp.presentation.components.TimerDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPlayScreen(
    quizId: String,
    onQuizComplete: (attemptId: String) -> Unit,
    onBack: () -> Unit,
    viewModel: QuizPlayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    
    // Load quiz on first composition
    LaunchedEffect(quizId) {
        viewModel.onIntent(QuizPlayContract.Intent.LoadQuiz(quizId))
    }
    
    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is QuizPlayContract.SideEffect.NavigateToResult -> {
                    onQuizComplete(effect.attemptId)
                }
                is QuizPlayContract.SideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message.asString(context))
                }
                is QuizPlayContract.SideEffect.PlayCorrectSound -> {
                    // Could play a sound here
                }
                is QuizPlayContract.SideEffect.PlayIncorrectSound -> {
                    // Could play a sound here
                }
                is QuizPlayContract.SideEffect.Vibrate -> {
                    try {
                        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            context.getSystemService<VibratorManager>()?.defaultVibrator
                        } else {
                            @Suppress("DEPRECATION")
                            context.getSystemService<Vibrator>()
                        }
                        vibrator?.let {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                            } else {
                                @Suppress("DEPRECATION")
                                it.vibrate(100)
                            }
                        }
                    } catch (e: Exception) {
                        // Ignore vibration errors
                    }
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Question ${uiState.currentQuestionIndex + 1} of ${uiState.questions.size}",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TimerDisplay(
                        timeRemainingSeconds = uiState.timeRemainingSeconds,
                        totalTimeSeconds = uiState.totalTimeSeconds,
                        size = 48.dp,
                        strokeWidth = 4.dp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading && uiState.questions.isEmpty()) {
                // Loading state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading questions...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (uiState.error != null && uiState.questions.isEmpty()) {
                // Error state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "😕",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.error?.asString() ?: "An error occurred",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { viewModel.onIntent(QuizPlayContract.Intent.LoadQuiz(quizId)) }) {
                        Text("Retry")
                    }
                }
            } else {
                // Quiz content
                QuizContent(
                    uiState = uiState,
                    onAnswerSelected = { viewModel.onIntent(QuizPlayContract.Intent.SelectAnswer(it)) },
                    onSubmit = { viewModel.onIntent(QuizPlayContract.Intent.SubmitAnswer) },
                    onNext = { viewModel.onIntent(QuizPlayContract.Intent.NextQuestion) }
                )
            }
        }
    }
}

@Composable
private fun QuizContent(
    uiState: QuizPlayContract.UiState,
    onAnswerSelected: (String) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit
) {
    val currentQuestion = uiState.currentQuestion ?: return
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Progress bar
        QuizProgressBar(
            currentQuestion = uiState.currentQuestionIndex,
            totalQuestions = uiState.questions.size
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Category & Difficulty badge
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = currentQuestion.category,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (currentQuestion.difficulty.lowercase()) {
                        "easy" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        "medium" -> Color(0xFFFFC107).copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Text(
                    text = currentQuestion.difficulty.replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = when (currentQuestion.difficulty.lowercase()) {
                        "easy" -> Color(0xFF4CAF50)
                        "medium" -> Color(0xFFA67C00)
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Question text
        AnimatedContent(
            targetState = currentQuestion.questionText,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "question_animation"
        ) { questionText ->
            Text(
                text = questionText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Options
        currentQuestion.options.forEachIndexed { index, option ->
            val optionState = when {
                uiState.showResult && option == currentQuestion.correctAnswer -> OptionState.CORRECT
                uiState.showResult && option == uiState.selectedAnswer && !uiState.isCorrect -> OptionState.INCORRECT
                option == uiState.selectedAnswer -> OptionState.SELECTED
                else -> OptionState.DEFAULT
            }
            
            OptionButton(
                option = option,
                index = index,
                state = optionState,
                onClick = { onAnswerSelected(option) },
                enabled = !uiState.showResult && !uiState.isSubmitting && !uiState.hasTimedOut
            )
            
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Result feedback
        if (uiState.showResult) {
            val backgroundColor by animateColorAsState(
                targetValue = if (uiState.isCorrect) 
                    Color(0xFF4CAF50).copy(alpha = 0.1f) 
                else 
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                label = "result_bg"
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (uiState.isCorrect) "🎉 Correct!" else "❌ Incorrect",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Timed out message
        if (uiState.hasTimedOut && !uiState.showResult) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = "⏰ Time's up!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Action button
        Button(
            onClick = {
                if (uiState.showResult) {
                    onNext()
                } else {
                    onSubmit()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = when {
                uiState.isSubmitting -> false
                uiState.showResult -> true
                uiState.hasTimedOut -> true
                uiState.selectedAnswer != null -> true
                else -> false
            },
            shape = RoundedCornerShape(12.dp)
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uiState.showResult) {
                        Text(
                            text = if (uiState.isLastQuestion) "Finish Quiz" else "Next Question",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (uiState.isLastQuestion) 
                                Icons.Default.Check 
                            else 
                                Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                        Text(
                            text = if (uiState.hasTimedOut) "Continue" else "Submit Answer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
