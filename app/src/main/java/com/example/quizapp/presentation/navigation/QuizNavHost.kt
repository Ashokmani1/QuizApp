package com.example.quizapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.quizapp.presentation.screens.modeselection.ModeSelectionScreen
import com.example.quizapp.presentation.screens.quizinput.QuizNameInputScreen
import com.example.quizapp.presentation.screens.quizplay.QuizPlayScreen
import com.example.quizapp.presentation.screens.quizresult.QuizResultScreen
import com.example.quizapp.presentation.screens.teacher.TeacherDashboardScreen
import com.example.quizapp.presentation.screens.teacher.QuizAnalyticsScreen

@Composable
fun QuizNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.ModeSelection,
        modifier = modifier
    ) {
        composable<Route.ModeSelection> {
            ModeSelectionScreen(
                onStudentModeSelected = {
                    navController.navigate(Route.QuizNameInput)
                },
                onTeacherModeSelected = {
                    navController.navigate(Route.TeacherDashboard)
                }
            )
        }
        
        composable<Route.QuizNameInput> {
            QuizNameInputScreen(
                onQuizStart = { quizId ->
                    navController.navigate(Route.QuizPlay(quizId)) {
                        popUpTo(Route.QuizNameInput) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable<Route.QuizPlay> { backStackEntry ->
            val route: Route.QuizPlay = backStackEntry.toRoute()
            QuizPlayScreen(
                quizId = route.quizId,
                onQuizComplete = { attemptId ->
                    navController.navigate(Route.QuizResult(attemptId)) {
                        popUpTo(Route.ModeSelection)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable<Route.QuizResult> { backStackEntry ->
            val route: Route.QuizResult = backStackEntry.toRoute()
            QuizResultScreen(
                attemptId = route.attemptId,
                onRetakeQuiz = { quizId ->
                    navController.navigate(Route.QuizPlay(quizId)) {
                        popUpTo(Route.ModeSelection)
                    }
                },
                onBackToHome = {
                    navController.navigate(Route.ModeSelection) {
                        popUpTo(Route.ModeSelection) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Route.TeacherDashboard> {
            TeacherDashboardScreen(
                onQuizSelected = { quizId ->
                    navController.navigate(Route.QuizAnalytics(quizId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable<Route.QuizAnalytics> { backStackEntry ->
            val route: Route.QuizAnalytics = backStackEntry.toRoute()
            QuizAnalyticsScreen(
                quizId = route.quizId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
