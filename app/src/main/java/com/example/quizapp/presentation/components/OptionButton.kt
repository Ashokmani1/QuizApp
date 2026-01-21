package com.example.quizapp.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class OptionState {
    DEFAULT,
    SELECTED,
    CORRECT,
    INCORRECT
}

@Composable
fun OptionButton(
    option: String,
    index: Int,
    state: OptionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val backgroundColor by animateColorAsState(
        targetValue = when (state) {
            OptionState.DEFAULT -> MaterialTheme.colorScheme.surface
            OptionState.SELECTED -> MaterialTheme.colorScheme.primaryContainer
            OptionState.CORRECT -> Color(0xFF4CAF50).copy(alpha = 0.2f)
            OptionState.INCORRECT -> MaterialTheme.colorScheme.errorContainer
        },
        animationSpec = tween(300),
        label = "option_bg_color"
    )
    
    val borderColor by animateColorAsState(
        targetValue = when (state) {
            OptionState.DEFAULT -> MaterialTheme.colorScheme.outline
            OptionState.SELECTED -> MaterialTheme.colorScheme.primary
            OptionState.CORRECT -> Color(0xFF4CAF50)
            OptionState.INCORRECT -> MaterialTheme.colorScheme.error
        },
        animationSpec = tween(300),
        label = "option_border_color"
    )
    
    val optionLabel = listOf("A", "B", "C", "D").getOrElse(index) { "${index + 1}" }
    
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Option label circle
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (state) {
                        OptionState.SELECTED -> MaterialTheme.colorScheme.primary
                        OptionState.CORRECT -> Color(0xFF4CAF50)
                        OptionState.INCORRECT -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Text(
                    text = optionLabel,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (state) {
                        OptionState.DEFAULT -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> Color.White
                    }
                )
            }
            
            // Option text
            Text(
                text = option,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Result icon
            when (state) {
                OptionState.CORRECT -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Correct",
                        tint = Color(0xFF4CAF50)
                    )
                }
                OptionState.INCORRECT -> {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Incorrect",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                else -> {}
            }
        }
    }
}
