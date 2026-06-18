package com.pdm0126.tutorconnectproyect.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.pdm0126.tutorconnectproyect.presentation.booking.BookingViewModel
import com.pdm0126.tutorconnectproyect.presentation.calendar.CalendarViewModel
import com.pdm0126.tutorconnectproyect.presentation.chat.ChatViewModel
import com.pdm0126.tutorconnectproyect.presentation.booking.BookingScreen
import com.pdm0126.tutorconnectproyect.presentation.calendar.CalendarScreen
import com.pdm0126.tutorconnectproyect.presentation.chat.ChatScreen
import com.pdm0126.tutorconnectproyect.presentation.dashboard.DashboardScreen
import com.pdm0126.tutorconnectproyect.presentation.login.LoginViewModel
import com.pdm0126.tutorconnectproyect.presentation.login.LoginScreen
import com.pdm0126.tutorconnectproyect.presentation.post.CreatePostScreen
import com.pdm0126.tutorconnectproyect.presentation.profile.ProfileViewModel
import com.pdm0126.tutorconnectproyect.presentation.tutor_detail.TutorDetailViewModel
import com.pdm0126.tutorconnectproyect.presentation.tutors.TutorsViewModel
import com.pdm0126.tutorconnectproyect.presentation.profile.ProfileScreen
import com.pdm0126.tutorconnectproyect.presentation.tutor_detail.TutorDetailScreen
import com.pdm0126.tutorconnectproyect.presentation.tutor_home.TutorDashboardScreen
import com.pdm0126.tutorconnectproyect.presentation.tutors.TutorsScreen


@Composable
fun appEntryProvider(navigator: AppNavigator): (NavKey) -> NavEntry<NavKey> =
    entryProvider {

        entry<AppDestinations.Login> {
            LoginScreen(
                onLoginSuccess = { navigator.resetTo(AppDestinations.Dashboard) },
                viewModel = hiltViewModel<LoginViewModel>(),
            )
        }

        // Dashboard del TUTORADO
        entry<AppDestinations.Dashboard> {
            DashboardScreen(
                onOpenTutors = { navigator.switchTab(AppDestinations.Tutors) },
                viewModel = hiltViewModel(),
                onNavigateToCreatePost = { navigator.navigateTo(AppDestinations.CreatePost) }
            )
        }

        // Dashboard del TUTOR
        entry<AppDestinations.TutorDashboard> {
            TutorDashboardScreen(
                onOpenCalendar = { navigator.switchTab(AppDestinations.Calendar) },
                onOpenCreatePost = { navigator.switchTab(AppDestinations.CreatePost) },
            )
        }

        entry<AppDestinations.Tutors> {
            TutorsScreen(
                onTutorClick = { id -> navigator.navigateTo(AppDestinations.TutorDetail(id)) },
                viewModel = hiltViewModel<TutorsViewModel>(),
            )
        }

        entry<AppDestinations.TutorDetail> { key ->
            TutorDetailScreen(
                tutorId = key.tutorId,
                onBack = navigator::pop,
                onOpenChat = { id, name -> navigator.navigateTo(AppDestinations.Chat(id, name)) },
                onBook = { id, name -> navigator.navigateTo(AppDestinations.Booking(id, name)) },
                viewModel = hiltViewModel<TutorDetailViewModel>(),
            )
        }

        entry<AppDestinations.Chat> { key ->
            ChatScreen(
                tutorId = key.tutorId,
                tutorName = key.tutorName,
                onBack = navigator::pop,
                viewModel = hiltViewModel<ChatViewModel>(),
            )
        }

        entry<AppDestinations.Calendar> {
            CalendarScreen(viewModel = hiltViewModel<CalendarViewModel>())
        }

        entry<AppDestinations.Booking> { key ->
            BookingScreen(
                tutorId = key.tutorId,
                tutorName = key.tutorName,
                onBack = navigator::pop,
                onBooked = navigator::pop,
                viewModel = hiltViewModel<BookingViewModel>(),
            )
        }

        entry<AppDestinations.CreatePost> {
            CreatePostScreen(
                onPublished = { navigator.switchTab(AppDestinations.Dashboard) },
                viewModel = hiltViewModel(),
            )
        }

        entry<AppDestinations.Profile> {
            ProfileScreen(
                onLogout = { navigator.resetTo(AppDestinations.Login) },
                viewModel = hiltViewModel<ProfileViewModel>(),
            )
        }
    }
