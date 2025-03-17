package org.yaabelozerov.gotovomp.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDTO(
    val id: Long,
    val name: String,
    val author: UserDTO,
    val text: String,
    val tags: List<String>,
    val ingredients: List<Ingredient>,
)

@Serializable
data class Ingredient(
    val id: Long,
    val name: String,
    val quantityType: Long,
    val quantity: Long,
    val category: String,
    val additionalParameters: String,
)

fun Ingredient.toRequest() = IngredientCreateRequest(name, quantityType, quantity, category, additionalParameters)

@Serializable
data class RecipeCreateRequest(
    val name: String,
    val text: String,
    val tags: List<String>,
    val ingredients: List<IngredientCreateRequest>,
    val author: Map<String, String> = emptyMap(),
)

@Serializable
data class IngredientCreateRequest(
    val name: String,
    val quantityType: Long,
    val quantity: Long,
    val category: String,
    val additionalParameters: String,
)