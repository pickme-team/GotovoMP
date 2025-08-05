package org.yaabelozerov.gotovomp.data.local.Database

import app.cash.sqldelight.db.SqlDriver
import org.yaabelozerov.RecipeDatabase

import org.yaabelozerov.gotovomp.RecipeQueries

class Dao(driver: SqlDriver) {
    val db = RecipeDatabase(driver)
    val queries: RecipeQueries = db.recipeQueries
}