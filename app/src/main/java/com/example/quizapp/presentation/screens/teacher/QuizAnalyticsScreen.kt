package com.example.quizapp.presentation.screens.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.quizapp.domain.model.QuizAnalytics
import com.example.quizapp.presentation.components.AnalyticsCard
import com.example.quizapp.presentation.components.QuestionAnalyticsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizAnalyticsScreen(
    quizId: String,
    onBack: () -> Unit,
    viewModel: QuizAnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(quizId) {
        viewModel.loadAnalytics(quizId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = uiState.analytics?.quiz?.name ?: "Quiz Analytics",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "An error occurred",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                uiState.analytics != null -> {
                    AnalyticsContent(analytics = uiState.analytics!!)
                }
            }
        }
    }
}

@Composable
private fun AnalyticsContent(analytics: QuizAnalytics) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary section
        item {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // First row: Attempts and Questions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnalyticsCard(
                    title = "Attempts",
                    value = "${analytics.totalAttempts}",
                    subtitle = "${analytics.completedAttempts} completed",
                    modifier = Modifier.weight(1f)
                )
                AnalyticsCard(
                    title = "Questions",
                    value = "${analytics.quiz.totalQuestions}",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Second row: Accuracy (full width)
            AnalyticsCard(
                title = "Quiz Accuracy",
                value = "${analytics.overallAccuracy.toInt()}%",
                subtitle = "Average score across all attempts",
                valueColor = when {
                    analytics.overallAccuracy >= 70 -> Color(0xFF4CAF50)
                    analytics.overallAccuracy >= 40 -> Color(0xFFFFC107)
                    else -> MaterialTheme.colorScheme.error
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Questions section header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Questions Analytics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Showing correct answer and latest student response for each question",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Question analytics cards
        itemsIndexed(analytics.questionAnalytics) { index, questionAnalytics ->
            QuestionAnalyticsCard(
                questionNumber = index + 1,
                analytics = questionAnalytics
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
