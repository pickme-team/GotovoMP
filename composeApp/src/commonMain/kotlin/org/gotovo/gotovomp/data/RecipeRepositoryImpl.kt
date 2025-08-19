package org.gotovo.gotovomp.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.gotovo.gotovomp.data.local.database.Dao
import org.gotovo.gotovomp.data.local.settings.SettingsManager
import org.gotovo.gotovomp.data.network.ApiClient
import org.gotovo.gotovomp.data.network.model.RecipeCreateRequest
import org.gotovo.gotovomp.domain.RecipeRepo
import org.gotovo.gotovomp.util.NetworkMonitor
import org.gotovo.gotovomp.util.nullIfBlank

class RecipeRepoImpl(
    private val api: ApiClient,
    private val dao: Dao,
    private val networkMonitor: NetworkMonitor
): RecipeRepo {
    private var isOnline: Boolean = false
    init {
        CoroutineScope(Dispatchers.Default).launch {
            networkMonitor.isOnline.collect { isOnline = it }
        }
    }
    override suspend fun createRecipe(recipe: RecipeCreateRequest) {
        dao.insertCompleteRecipe(recipe)
        if (isOnline) {
            SettingsManager.token.nullIfBlank()?.let {
                api.addRecipe(recipe)
            }
        }

    }
}

