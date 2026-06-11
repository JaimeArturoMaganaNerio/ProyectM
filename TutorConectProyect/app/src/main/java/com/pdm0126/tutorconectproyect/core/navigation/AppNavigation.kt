package com.pdm0126.tutorconectproyect.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdm0126.tutorconectproyect.presentation.login.LoginScreen
import com.pdm0126.tutorconectproyect.presentation.dashboard.DashboardScreen
import com.pdm0126.tutorconectproyect.presentation.post.CreatePostScreen
import com.tutorconnect.presentation.dashboard.DashboardScreen
import com.tutorconnect.presentation.login.LoginScreen
import com.tutorconnect.presentation.post.CreatePostScreen

@Composable
fun AppNavigation() {
    // Inicializamos el stack de navegación en el Login
    val backStack = rememberNavBackStack(initialRoute = AppRoute.Login)

    NavDisplay(
        backstack = backStack,
        entryProvider = entryProvider {

            route<AppRoute.Login> {
                LoginScreen(
                    viewModel = hiltViewModel(),
                    onLoginSuccess = {
                        // Limpiamos el backstack al ir al dashboard para no volver al login con el botón de "atrás"
                        backStack.clear()
                        backStack.push(AppRoute.Dashboard)
                    }
                )
            }

            route<AppRoute.Dashboard> {
                DashboardScreen(
                    viewModel = hiltViewModel(),
                    onNavigateToTutors = { backStack.push(AppRoute.Tutors) },
                    onNavigateToCreatePost = { backStack.push(AppRoute.CreatePost) }
                )
            }

            route<AppRoute.CreatePost> {
                CreatePostScreen(
                    viewModel = hiltViewModel(),
                    onPostCreated = { backStack.pop() },
                    onNavigateBack = { backStack.pop() }
                )
            }

            // Aquí se irán agregando las demás rutas (Chat, TutorDetail) a medida que ajustemos sus ViewModels
        }
    )
}