package org.yaabelozerov.gotovomp.data.local.Database

import app.cash.sqldelight.db.SqlDriver
import org.yaabelozerov.RecipeDatabase

import org.yaabelozerov.gotovomp.RecipeQueries
import org.yaabelozerov.gotovomp.data.network.model.RecipeCreateRequest

class Dao(driver: SqlDriver) {
    val db = RecipeDatabase(driver)
    val queries: RecipeQueries = db.recipeQueries

    fun saveRecipe(recipe: RecipeCreateRequest, fav: Boolean) {
        recipe.run {
            queries.insertRecipe(
                null,
                name,
                "me", // TODO пиздец
                text,
                if (fav) 1 else 0
            )
        }
    }
    // TODO как добавлять теги, если я не знаю id рецепта
    // аналогично с ингредиентами
    // и как в целом делать базу данных, может есть варианты попроще
}