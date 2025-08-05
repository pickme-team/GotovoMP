package org.yaabelozerov.gotovomp.data.local.Database

import app.cash.sqldelight.db.SqlDriver
import org.yaabelozerov.RecipeDatabase
import org.yaabelozerov.gotovomp.data.local.settings.SettingsManager

import org.yaabelozerov.gotovomp.data.network.model.Ingredient
import org.yaabelozerov.gotovomp.data.network.model.IngredientCreateRequest
import org.yaabelozerov.gotovomp.data.network.model.RecipeCreateRequest
import org.yaabelozerov.gotovomp.data.network.model.RecipeDTO
import org.yaabelozerov.gotovomp.data.network.model.UserDTO
import org.yaabelozerov.gotovomp.domain.DomainResult
import org.yaabelozerov.gotovomp.domain.runAndCatch

class Dao(driver: SqlDriver) {
    val db = RecipeDatabase(driver)
    private val userQueries = db.userQueries
    private val recipeQueries = db.recipeQueries
    private val ingredientQueries = db.ingredientQueries
    private val tagQueries = db.tagQueries
    private val recipeTagQueries = db.recipeTagQueries
    private val recipeIngredientQueries = db.recipeIngredientQueries

    suspend fun insertCompleteRecipe(
        request: RecipeCreateRequest,
    ): Result<Long> = runCatching {
        val recipeId = generateRecipeId(request)

        db.transaction {
            // 1. Ensure author exists
            userQueries.insertUser(
                username = SettingsManager.username
            )

            // 2. Insert the recipe
            recipeQueries.insertRecipe(
                id = recipeId,
                name = request.name,
                authorUsername = SettingsManager.username,
                text = request.text,
                isFav = 0
            )

            // 3. Handle ingredients (check for existing ones)
            request.ingredients.forEach { ingredientRequest ->
                val existingIngredient = ingredientQueries.selectIngredientByNameAndCategory(
                    name = ingredientRequest.name,
                    category = ingredientRequest.category
                ).executeAsOneOrNull()

                val ingredientId = existingIngredient?.id ?: generateIngredientId(ingredientRequest)

                if (existingIngredient == null) {
                    ingredientQueries.insertIngredient(
                        id = ingredientId,
                        name = ingredientRequest.name,
                        quantityType = ingredientRequest.quantityType,
                        quantity = ingredientRequest.quantity,
                        category = ingredientRequest.category,
                        additionalParameters = ingredientRequest.additionalParameters
                    )
                }

                recipeIngredientQueries.insertRecipeIngredient(
                    recipeId = recipeId,
                    ingredientId = ingredientId
                )
            }

            // 4. Handle tags (check for existing ones)
            request.tags.forEach { tagText ->
                val existingTag = tagQueries.selectTagByText(tagText)
                    .executeAsOneOrNull()

                val tagId = existingTag?.id ?: generateTagId(tagText)

                if (existingTag == null) {
                    tagQueries.insertTag(
                        id = tagId,
                        tag = tagText
                    )
                }

                recipeTagQueries.insertRecipeTag(
                    recipeId = recipeId,
                    tagId = tagId
                )
            }
        }

        recipeId
    }

    // Helper functions for ID generation (implement according to your needs)
    private fun generateRecipeId(request: RecipeCreateRequest): Long {
        // Implement your ID generation logic
        // Could be UUID.random().leastSignificantBits or database auto-increment
        return request.text.hashCode().toLong() // Simple example using timestamp
    }

    private fun generateIngredientId(ingredient: IngredientCreateRequest): Long {
        return (ingredient.name.hashCode() +
                ingredient.quantityType.hashCode() +
                ingredient.quantity.hashCode()).toLong()
    }


    private fun generateTagId(tagText: String): Long {
        // Implement your ID generation logic
        return tagText.hashCode().toLong()
    }




    // TODO как добавлять теги, если я не знаю id рецепта
    // аналогично с ингредиентами
    // и как в целом делать базу данных, может есть варианты попроще
}