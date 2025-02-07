package org.example.project.presentation.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import org.example.project.viewModels.PersonalVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewRecipeScreen(recipeId: Long, onBack: () -> Unit, modifier: Modifier = Modifier, viewModel: PersonalVM) {
    val recipe = viewModel.state.collectAsState().value.recipes.recipes.find { it.id == recipeId } ?: return
    val richTextState = rememberSaveable(recipe.text) { recipe.text.split('\n').map { md ->
        RichTextState().apply { setMarkdown(md) }
    } }
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(16.dp)) {
        item {
            CenterAlignedTopAppBar(title = {
                Text(
                    recipe.name,
                    style = MaterialTheme.typography.headlineSmall
                )
            },
                modifier = Modifier.padding(bottom = 16.dp),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back"
                        )
                    }
                })
        }
        items(richTextState, key = { it.toText() }) {
            Card(modifier = Modifier.fillParentMaxWidth()) {
                RichText(it, fontSize = MaterialTheme.typography.headlineSmall.fontSize, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.padding(bottom = 16.dp))
        }
    }
}