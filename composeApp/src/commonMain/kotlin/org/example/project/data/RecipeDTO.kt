package org.example.project.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class RecipeDTO(
    val id: Long,
    val name: String,
    val authorId: Long,
    val text: String,
    val tags: List<String>,
    val ingredients: List<Ingredient>,
)

data class Ingredient(
    val id: Long,
    val name: String,
    val quantityType: Long,
    val quantity: Long,
    val category: String,
    val additionalParameters: String,
)
