package org.example.project.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.example.project.domain.GlobalEvent
import org.example.project.domain.UI
import org.example.project.presentation.util.onSecondaryContainerLight
import org.example.project.viewModels.ProfileScreenEvent
import org.example.project.viewModels.ProfileVM

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, profileVM: ProfileVM = viewModel()) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        val uiState by profileVM.state.collectAsState()
        uiState.run {
            AvatarWithText(
                avatarUrl = avatarUrl, name = name, surname = surname, username = username
            )
        }
        TextButton(onClick = profileVM::logout) { Text("Выйти") }
    }
}

@Composable
fun AvatarWithText(avatarUrl: String, name: String, surname: String, username: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.clip(shape = CircleShape).size(160.dp)
            )
        }
        Text(
            text = "$name $surname",
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = username,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}
