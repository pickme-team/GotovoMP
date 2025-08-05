package org.yaabelozerov.gotovomp.data.network.model

data class RecipeShort(
    val user: UserShort,
    val previewPhotos: List<String>,
    val likes: Int,
    val topComment: Comment,
    val title: String

)

data class Recipe( // TODO: Точно нам нужен этот класс? мб по-нормальному переписать или удалить его?
    val user: UserShort,
    val ingredients: List<Ingredient>,
    val previewPhotos: List<String>,
    val steps: List<CookingStep>,
    val likes: Int,
    val comments: List<Comment>,
)

data class Comment(
    val author: UserShort,
    val text: String
)

data class CookingStep(
    val name: String,
    val text: String,
    val photo: String?
)

data class UserShort(
    val id: Long,
    val username: String,
    val photoUrl: String
)