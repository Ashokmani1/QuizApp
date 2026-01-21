package com.example.quizapp.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.quizapp.core.common.Constants

@Composable
fun TimerDisplay(
    timeRemainingSeconds: Int,
    totalTimeSeconds: Int = Constants.DEFAULT_QUESTION_TIME_SECONDS,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp
) {
    val progress = timeRemainingSeconds.toFloat() / totalTimeSeconds
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 100),
        label = "timer_progress"
    )
    
    val isUrgent = timeRemainingSeconds <= Constants.URGENT_TIME_THRESHOLD_SECONDS
    val timerColor = if (isUrgent) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }
    
    val backgroundColor = if (isUrgent) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    }
    
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .drawBehind {
                val sweepAngle = animatedProgress * 360f
                drawArc(
                    color = timerColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = timeRemainingSeconds.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = timerColor
        )
    }
}

@Composable
fun TimerBar(
    timeRemainingSeconds: Int,
    totalTimeSeconds: Int = Constants.DEFAULT_QUESTION_TIME_SECONDS,
    modifier: Modifier = Modifier
) {
    val isUrgent = timeRemainingSeconds <= Constants.URGENT_TIME_THRESHOLD_SECONDS
    val timerColor = if (isUrgent) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TimerDisplay(
            timeRemainingSeconds = timeRemainingSeconds,
            totalTimeSeconds = totalTimeSeconds,
            size = 56.dp,
            strokeWidth = 6.dp
        )
        
        Text(
            text = if (isUrgent) "Hurry up!" else "seconds left",
            style = MaterialTheme.typography.bodyMedium,
            color = timerColor
        )
    }
}
