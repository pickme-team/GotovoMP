package org.example.project.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatDTO(
    @SerialName("cat_name")
    val name: String
)
