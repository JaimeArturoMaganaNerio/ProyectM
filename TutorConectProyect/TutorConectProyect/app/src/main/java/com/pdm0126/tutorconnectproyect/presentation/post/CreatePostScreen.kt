package com.pdm0126.tutorconnectproyect.presentation.post

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pdm0126.tutorconnectproyect.core.components.AppBottomBar
import com.pdm0126.tutorconnectproyect.core.components.AppTextField
import com.pdm0126.tutorconnectproyect.core.components.PrimaryButton
import com.pdm0126.tutorconnectproyect.core.navigation.AppDestinations
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onPublished: () -> Unit,
    onNavigate: (AppDestinations) -> Unit,
    viewModel: CreatePostViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    // Photo Picker moderno (no necesita permiso en API 33+)
    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onAction(CreatePostUiAction.AttachFile(uri, "imagen_${System.currentTimeMillis()}.jpg"))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                CreatePostUiEvent.Published -> onPublished()
                is CreatePostUiEvent.ShowMessage -> snackbar.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = { TopAppBar(title = { Text("Crear Publicación") }) },
        bottomBar = {
            AppBottomBar(
                isTutor = true,
                current = AppDestinations.CreatePost,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AppTextField(
                value = state.title,
                onValueChange = { viewModel.onAction(CreatePostUiAction.TitleChanged(it)) },
                label = "Título",
                leadingIcon = Icons.Filled.Title,
                isError = state.titleError,
                supportingText = if (state.titleError) "Escribe un título" else null,
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onAction(CreatePostUiAction.DescriptionChanged(it)) },
                label = { Text("Descripción") },
                isError = state.descriptionError,
                supportingText = if (state.descriptionError) {
                    { Text("Escribe una descripción") }
                } else null,
                minLines = 4,
                modifier = Modifier.fillMaxWidth(),
            )

            // Botón adjuntar o preview de imagen seleccionada
            if (state.attachmentUri == null) {
                OutlinedButton(
                    onClick = {
                        pickImage.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Filled.Image, contentDescription = null)
                    Text("  Adjuntar imagen")
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Preview de la imagen antes de publicar
                    AsyncImage(
                        model = state.attachmentUri,
                        contentDescription = "Vista previa",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(12.dp)),
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AssistChip(
                            onClick = {},
                            label = { Text(state.attachmentName ?: "imagen.jpg") },
                            leadingIcon = { Icon(Icons.Filled.AttachFile, contentDescription = null) },
                        )
                        IconButton(onClick = { viewModel.onAction(CreatePostUiAction.RemoveAttachment) }) {
                            Icon(Icons.Filled.Close, contentDescription = "Quitar imagen")
                        }
                    }
                }
            }

            PrimaryButton(
                text = if (state.isSubmitting) "Publicando…" else "Publicar",
                onClick = { viewModel.onAction(CreatePostUiAction.Submit) },
                loading = state.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                "Tu publicación aparecerá en el foro de Preguntas Destacadas.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
