package org.example.project.presentation.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.example.project.presentation.util.onSecondaryContainerLight

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxSize()) {
    AvatarWithText(
        avatarUrl =
            "https://www.thesun.co.uk/wp-content/uploads/2022/08/OP-GORD.jpg?strip=all&quality=100&w=1920&h=1080&crop=1",
        name = "Дмитрий",
        surname = "Гордон",
        username = "DimochkaCOOCKING")
  }
}

@Composable
fun AvatarWithText(avatarUrl: String, name: String, surname: String, username: String) {
  Column(
      modifier = Modifier.fillMaxWidth().padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      //        verticalArrangement = Arrangement.Center
  ) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
      AsyncImage(
          model = avatarUrl,
          contentDescription = "avatar",
          contentScale = ContentScale.Crop,
          modifier = Modifier.clip(shape = CircleShape).size(160.dp))
    }
    Spacer(Modifier.size(32.dp))
    Text(
        text = "$name $surname",
        // modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.size(4.dp))
    Text(
        text = username,
        // modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSecondaryContainer)
  }
}
