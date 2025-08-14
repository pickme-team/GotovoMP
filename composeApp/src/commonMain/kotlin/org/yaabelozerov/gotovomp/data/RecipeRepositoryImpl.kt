package org.yaabelozerov.gotovomp.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.yaabelozerov.gotovomp.data.local.database.Dao
import org.yaabelozerov.gotovomp.data.local.settings.SettingsManager
import org.yaabelozerov.gotovomp.data.network.ApiClient
import org.yaabelozerov.gotovomp.data.network.model.RecipeCreateRequest
import org.yaabelozerov.gotovomp.domain.RecipeRepo
import org.yaabelozerov.gotovomp.util.NetworkMonitor
import org.yaabelozerov.gotovomp.util.nullIfBlank

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

