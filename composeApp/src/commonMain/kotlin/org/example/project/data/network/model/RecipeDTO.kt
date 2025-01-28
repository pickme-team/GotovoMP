package org.example.project.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDTO(
    val id: Long,
    val name: String,
    val authorId: Long,
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

@Serializable
data class RecipeCreateRequest(
    val name: String,
    val text: String,
    val tags: List<String>,
    val ingredients: List<Ingredient>,
)
