package com.aventumapa.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aventumapa.app.ui.components.AventuBackground
import com.aventumapa.app.ui.screens.ExploreScreen
import com.aventumapa.app.ui.screens.HomeScreen
import com.aventumapa.app.ui.screens.MemoryScreen
import com.aventumapa.app.ui.screens.ParentsScreen
import com.aventumapa.app.ui.screens.QuizScreen
import com.aventumapa.app.ui.screens.WelcomeScreen

private object Route {
    const val GATE = "gate"
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val EXPLORE = "explore"
    const val QUIZ = "quiz"
    const val MEMORY = "memory"
    const val PARENTS = "parents"
}

@Composable
fun AventuMapaRoot(viewModel: AppViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.GATE) {
        composable(Route.GATE) {
            AventuBackground {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                    Text("Preparando tu aventura…", style = MaterialTheme.typography.bodyLarge)
                }
            }
            LaunchedEffect(state.isLoading) {
                if (!state.isLoading) {
                    navController.navigate(if (state.profile.hasProfile) Route.HOME else Route.WELCOME) {
                        popUpTo(Route.GATE) { inclusive = true }
                    }
                }
            }
        }

        composable(Route.WELCOME) {
            WelcomeScreen(
                existingAlias = state.profile.alias,
                existingAvatarId = state.profile.avatarId,
                onContinueExisting = {
                    navController.navigate(Route.HOME) {
                        popUpTo(Route.WELCOME) { inclusive = true }
                    }
                },
                onCreateProfile = { alias, avatar ->
                    viewModel.saveProfile(alias, avatar)
                    navController.navigate(Route.HOME) {
                        popUpTo(Route.WELCOME) { inclusive = true }
                    }
                },
            )
        }

        composable(Route.HOME) {
            HomeScreen(
                profile = state.profile,
                onExplore = { navController.navigate(Route.EXPLORE) },
                onQuiz = { navController.navigate(Route.QUIZ) },
                onMemory = { navController.navigate(Route.MEMORY) },
                onParents = { navController.navigate(Route.PARENTS) },
            )
        }

        composable(Route.EXPLORE) { ExploreScreen(onBack = navController::popBackStack) }
        composable(Route.QUIZ) {
            QuizScreen(
                onBack = navController::popBackStack,
                onRoundFinished = viewModel::recordRound,
            )
        }
        composable(Route.MEMORY) {
            MemoryScreen(
                onBack = navController::popBackStack,
                onRoundFinished = viewModel::recordRound,
            )
        }
        composable(Route.PARENTS) {
            ParentsScreen(
                profile = state.profile,
                onBack = navController::popBackStack,
                onDeleteProfile = {
                    viewModel.deleteProfile()
                    navController.navigate(Route.WELCOME) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                },
            )
        }
    }
}
