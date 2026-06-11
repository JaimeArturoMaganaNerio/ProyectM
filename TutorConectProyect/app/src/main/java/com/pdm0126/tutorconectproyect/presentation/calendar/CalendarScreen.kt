package com.tutorconnect.presentation.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tutorconnect.core.components.EmptyState
import com.tutorconnect.core.components.ErrorState
import com.tutorconnect.core.components.LoadingState
import com.tutorconnect.data.model.TutoringSession
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CalendarUiEvent.ShowMessage -> snackbar.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = { TopAppBar(title = { Text("Calendario") }) },
    ) { padding ->
        when {
            state.isLoading -> LoadingState(Modifier.padding(padding))
            state.error != null -> ErrorState(state.error!!, Modifier.padding(padding)) {
                viewModel.onAction(CalendarUiAction.Retry)
            }
            state.sessions.isEmpty() -> EmptyState("No tienes tutorías programadas.", Modifier.padding(padding))
            else -> LazyColumn(
                Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { Text("Próximas sesiones", style = MaterialTheme.typography.titleMedium) }
                items(state.sessions.distinctBy { it.id }, key = { it.id }) { session ->
                    SessionCard(session) { viewModel.onAction(CalendarUiAction.SessionClicked(session.id)) }
                }
            }
        }
    }
}

@Composable
private fun SessionCard(session: TutoringSession, onClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DateBadge(session.date)
            Column(Modifier.weight(1f)) {
                Text(session.subject, style = MaterialTheme.typography.titleMedium)
                Text("con ${session.tutorName}", style = MaterialTheme.typography.bodyMedium)
                Text("${session.date} · ${session.time}", style = MaterialTheme.typography.labelMedium)
            }
            val statusText = if (session.confirmed) "Confirmada" else "Pendiente"
            val statusColor =
                if (session.confirmed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
            Surface(color = statusColor.copy(alpha = 0.15f), contentColor = statusColor, shape = RoundedCornerShape(50)) {
                Text(statusText, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun DateBadge(date: String) {
    val day = date.takeLast(2)
    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(48.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(day, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        }
    }
}
