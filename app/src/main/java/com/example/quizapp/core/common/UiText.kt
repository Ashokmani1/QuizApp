package com.example.quizapp.core.common

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Wrapper class for strings that can be either dynamic or from resources.
 * Useful for ViewModels to emit UI-ready text without direct Context dependency.
 */
sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is StringResource) return false
            return resId == other.resId && args.contentEquals(other.args)
        }
        
        override fun hashCode(): Int {
            var result = resId
            result = 31 * result + args.contentHashCode()
            return result
        }
    }
    
    fun asString(context: Context): String = when (this) {
        is DynamicString -> value
        is StringResource -> context.getString(resId, *args)
    }
    
    @Composable
    fun asString(): String {
        val context = LocalContext.current
        return asString(context)
    }
}

/**
 * Extension to convert AppError to UiText
 */
fun AppError.toUiText(): UiText = UiText.DynamicString(message)
