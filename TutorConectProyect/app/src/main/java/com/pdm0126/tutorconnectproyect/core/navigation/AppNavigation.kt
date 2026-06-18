package com.pdm0126.tutorconnectproyect.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.pdm0126.tutorconnectproyect.presentation.dashboard.DashboardViewModel
import com.pdm0126.tutorconnectproyect.presentation.login.LoginViewModel
import com.pdm0126.tutorconnectproyect.presentation.post.CreatePostViewModel
import com.pdm0126.tutorconnectproyect.presentation.dashboard.DashboardScreen
import com.pdm0126.tutorconnectproyect.presentation.login.LoginScreen
import com.pdm0126.tutorconnectproyect.presentation.post.CreatePostScreen
import com.pdm0126.tutorconnectproyect.presentation.tutor_detail.TutorDetailScreen
import com.pdm0126.tutorconnectproyect.presentation.tutors.TutorsScreen

@Composable
fun AppNavigation() {
    // 1. Inicializamos el stack de navegación crudo de la librería
    val backStack = rememberNavBackStack(AppDestinations.Tutors)

    // 2. Creamos TU instancia de AppNavigator usando remember para que no se recree en cada recomposición
    val navigator = remember { AppNavigator(backStack) }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {

            entry<AppDestinations.Login> {
                LoginScreen(
                    viewModel = hiltViewModel<LoginViewModel>(),
                    onLoginSuccess = {
                        navigator.resetTo(AppDestinations.Dashboard)
                    }
                )
            }

            entry<AppDestinations.Dashboard> {
                DashboardScreen(
                    viewModel = hiltViewModel<DashboardViewModel>(),
                    onOpenTutors = { navigator.navigateTo(AppDestinations.Tutors) },
                    onNavigateToCreatePost = { navigator.navigateTo(AppDestinations.CreatePost) }
                )
            }

            entry<AppDestinations.CreatePost> {
                CreatePostScreen(
                    viewModel = hiltViewModel<CreatePostViewModel>(),
                    // ✅ Usamos tu función pop() definida en la clase
                    onPublished = { navigator.pop() },
                )
            }

            entry<AppDestinations.Tutors> {
                TutorsScreen(
                    onTutorClick = { id ->
                        navigator.navigateTo(AppDestinations.TutorDetail(id))
                    }
                )
            }

            entry<AppDestinations.TutorDetail> { dest ->

                TutorDetailScreen(
                    tutorId = dest.tutorId,
                    onBack = { navigator.pop() },
                    onOpenChat = { id, name -> }, // Vacío por ahora
                    onBook = { id, name -> }      // Vacío por ahora
                )
            }

            // Futuras pantallas...
        }
    )
}