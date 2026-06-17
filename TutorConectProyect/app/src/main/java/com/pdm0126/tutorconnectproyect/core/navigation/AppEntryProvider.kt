package com.tutorconnect.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.tutorconnect.presentation.booking.BookingScreen
import com.tutorconnect.presentation.calendar.CalendarScreen
import com.tutorconnect.presentation.chat.ChatScreen
import com.tutorconnect.presentation.dashboard.DashboardScreen
import com.tutorconnect.presentation.login.LoginScreen
import com.tutorconnect.presentation.post.CreatePostScreen
import com.tutorconnect.presentation.profile.ProfileScreen
import com.tutorconnect.presentation.tutor_detail.TutorDetailScreen
import com.tutorconnect.presentation.tutor_home.TutorDashboardScreen
import com.tutorconnect.presentation.tutors.TutorsScreen

@Composable
fun appEntryProvider(navigator: AppNavigator): (NavKey) -> NavEntry<NavKey> =
    entryProvider {

        entry<AppDestinations.Login> {
            LoginScreen(
                onLoginSuccess = { navigator.resetTo(AppDestinations.Dashboard) },
                viewModel = hiltViewModel(),
            )
        }

        // Dashboard del TUTORADO
        entry<AppDestinations.Dashboard> {
            DashboardScreen(
                onOpenTutors = { navigator.switchTab(AppDestinations.Tutors) },
                viewModel = hiltViewModel(),
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
                viewModel = hiltViewModel(),
            )
        }

        entry<AppDestinations.TutorDetail> { key ->
            TutorDetailScreen(
                tutorId = key.tutorId,
                onBack = navigator::pop,
                onOpenChat = { id, name -> navigator.navigateTo(AppDestinations.Chat(id, name)) },
                onBook = { id, name -> navigator.navigateTo(AppDestinations.Booking(id, name)) },
                viewModel = hiltViewModel(),
            )
        }

        entry<AppDestinations.Chat> { key ->
            ChatScreen(
                tutorId = key.tutorId,
                tutorName = key.tutorName,
                onBack = navigator::pop,
                viewModel = hiltViewModel(),
            )
        }

        entry<AppDestinations.Calendar> {
            CalendarScreen(viewModel = hiltViewModel())
        }

        entry<AppDestinations.Booking> { key ->
            BookingScreen(
                tutorId = key.tutorId,
                tutorName = key.tutorName,
                onBack = navigator::pop,
                onBooked = navigator::pop,
                viewModel = hiltViewModel(),
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
                viewModel = hiltViewModel(),
            )
        }
    }
