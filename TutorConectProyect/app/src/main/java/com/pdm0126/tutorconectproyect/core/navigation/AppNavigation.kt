package com.tutorconnect.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay

private data class TabItem(
    val destination: AppDestinations,
    val label: String,
    val icon: ImageVector,
)

private val bottomTabs = listOf(
    TabItem(AppDestinations.Dashboard,  "Inicio",     Icons.Filled.Home),
    TabItem(AppDestinations.Tutors,     "Tutores",    Icons.Filled.School),
    TabItem(AppDestinations.CreatePost, "Publicar",   Icons.Filled.AddCircle),
    TabItem(AppDestinations.Calendar,   "Calendario", Icons.Filled.CalendarMonth),
    TabItem(AppDestinations.Profile,    "Perfil",     Icons.Filled.Person),
)

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(AppDestinations.Login)
    val navigator = remember(backStack) { AppNavigator(backStack) }

    val current: NavKey? = backStack.lastOrNull()
    val showBottomBar = bottomTabs.any { it.destination == current }

    // Decoradores para manejar el estado guardado y los ViewModels por cada entrada
    val decorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator<NavKey>()
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        NavigationBarItem(
                            selected = current == tab.destination,
                            onClick = { navigator.switchTab(tab.destination) },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { navigator.pop() },
            entryDecorators = decorators,
            modifier = Modifier.padding(innerPadding),
            entryProvider = appEntryProvider(navigator),
        )
    }
}
