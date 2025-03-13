package org.example.project.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.example.project.data.network.model.RecipeDTO
import org.example.project.presentation.util.bouncyClickable
import org.example.project.presentation.util.tertiaryDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlternativeRecipeCard(
    recipe: RecipeDTO,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var imageLoaded by rememberSaveable { mutableStateOf(false) }
    val fadeColor =
        MaterialTheme.colorScheme.run { if (!isSystemInDarkTheme()) onBackground else background }
    val fadeInverse =
        MaterialTheme.colorScheme.run { if (isSystemInDarkTheme()) onBackground else background }



    Card(
        modifier = modifier.fillMaxWidth()
            .clickable(onClick = onClick).aspectRatio(0.6f),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp).weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier.size(36.dp).clip(CircleShape).background(color = tertiaryDark)) {

                }
                Spacer(Modifier.size(4.dp))
                Text(
                    text = recipe.author.run { firstName?.plus(" ")?.plus(lastName) ?: username },
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Box(modifier = Modifier.fillMaxWidth().weight(7f), contentAlignment = Alignment.BottomStart) {
                var lineCount by remember { mutableStateOf(1) }
                AsyncImage(
                    modifier = Modifier.fillMaxWidth().drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                0f to Color.Transparent,
                                0.65f - .15f*lineCount to Color.Transparent,
                                1f - .15f*lineCount  to fadeColor.copy(0.85f)
                            )
                        )
                    },
                    model = imageUrl,
                    contentDescription = recipe.name,
                    contentScale = ContentScale.Crop,
                    onSuccess = { imageLoaded = true })
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = recipe.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                    style = MaterialTheme.typography.displayMedium.copy(
                        lineBreak = LineBreak.Paragraph,
                        color = fadeInverse,
                        fontWeight = FontWeight.Black
                    ),
                    onTextLayout = {
                        lineCount = it.lineCount
                    },
                )
            }
            Column(modifier = Modifier.padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Ингредиенты:",
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                var expandIngridients by remember { mutableStateOf(false) }
                AnimatedContent(expandIngridients) { expanded ->
                    if (expanded) {
                        recipe.ingredients.run {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(horizontal = 12.dp).clickable { expandIngridients = !expandIngridients }) {
                                forEach {
                                    OutlinedCard(shape = MaterialTheme.shapes.large, colors = CardDefaults.outlinedCardColors(containerColor = fadeInverse, contentColor = fadeColor)) {
                                        Text(it.name, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontStyle = FontStyle.Italic)
                                    }
                                }
                            }
                        }

                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.clickable { expandIngridients = !expandIngridients }) {
                            item {
                                Spacer(Modifier.width(4.dp))
                            }
                            items(recipe.ingredients.take(7)) {
                                OutlinedCard(shape = MaterialTheme.shapes.large, colors = CardDefaults.outlinedCardColors(containerColor = fadeInverse, contentColor = fadeColor)) {
                                    Text(it.name, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontStyle = FontStyle.Italic)
                                }
                            }
                            item {
                                Spacer(Modifier.width(4.dp))
                            }
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth().weight(1f).background(color = MaterialTheme.colorScheme.surfaceContainerHigh), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton({}, modifier = Modifier.padding(start = 4.dp)) { Icon(Icons.Outlined.ThumbUp, contentDescription = "Like") }
                IconButton({}) { Icon(Icons.Outlined.Add, contentDescription = "Comment") }
                IconButton({}) { Icon(Icons.Outlined.Share, contentDescription = "Share") }
                Spacer(Modifier.weight(1f))
                Text("час назад", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.size(8.dp))

            }
        }





    }

}