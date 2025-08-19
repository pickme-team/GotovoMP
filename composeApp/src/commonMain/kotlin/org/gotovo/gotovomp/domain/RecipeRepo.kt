package org.gotovo.gotovomp.domain

import org.gotovo.gotovomp.data.network.model.RecipeCreateRequest

interface RecipeRepo {
// TODO Надо обсудить архитектуру. Тут есть какой-то задел под юзкейсы,
// и если мы будем его делать, то я хочу с тобой это обсудить, потому что щас не особо понимаю как.
// Либо можно сделать просто через репозитории.
    suspend fun createRecipe(recipe: RecipeCreateRequest)
}