package com.example.mydish.model.service.webservice

/*** this object class use as data class structures of the api service */
//object RandomDish {

    /*** class hold list off recipes @data class Recipe */
    data class Recipes(
        val recipes: List<Recipe>
    )

    /*** class hold recipes data @data class Recipe */
    data class Recipe(
        val aggregateLikes: Int,
        val analyzedInstructions: List<AnalyzedInstruction>,
        val cheap: Boolean,
        val creditsText: String,
        val cuisines: List<Any>,
        val dairyFree: Boolean,
        val diets: List<String>,
        val dishTypes: List<String>,
        val extendedIngredients: List<ExtendedIngredient>,
        val gaps: String,
        val glutenFree: Boolean,
        val healthScore: Double,
        val id: Int,
        val image: String,
        val imageType: String,
        val instructions: String,
        val license: String,
        val lowFodmap: Boolean,
        val occasions: List<Any>,
        val originalId: Any,
        val pricePerServing: Double,
        val readyInMinutes: Int,
        val servings: Int,
        val sourceName: String,
        val sourceUrl: String,
        val spoonacularScore: Double,
        val spoonacularSourceUrl: String,
        val summary: String,
        val sustainable: Boolean,
        val title: String,
        val vegan: Boolean,
        val vegetarian: Boolean,
        val veryHealthy: Boolean,
        val veryPopular: Boolean,
        val weightWatcherSmartPoints: Int
    )

    /*** called from Recipe object class, wrapped as list */
    data class AnalyzedInstruction(
        val name: String,
        val steps: List<Step>
    )

    /*** called from Recipe object class, wrapped as list */
    data class ExtendedIngredient(
        val aisle: String,
        val amount: Double,
        val consistency: String,
        val id: Int,
        val image: String,
        val measures: Measures,
        val meta: List<String>,
        val metaInformation: List<String>,
        val name: String,
        val nameClean: String,
        val original: String,
        val originalName: String,
        val originalString: String,
        val unit: String
    )

    /*** called from AnalyzedInstruction object class, wrapped as list */
    data class Step(
        val equipment: List<Equipment>,
        val ingredients: List<Ingredient>,
        val length: Length,
        val number: Int,
        val step: String
    )

    /*** called from Step object class, wrapped as list */
    data class Equipment(
        val id: Int,
        val image: String,
        val localizedName: String,
        val name: String
    )

    /*** called from Step object class, wrapped as list */
    data class Ingredient(
        val id: Int,
        val image: String,
        val localizedName: String,
        val name: String
    )

    /*** called from Step object class */
    data class Length(
        val number: Int,
        val unit: String
    )

    /*** called from ExtendedIngredient object class */
    data class Measures(
        val metric: Metric,
        val us: Us
    )

    /*** called from Measures object class */
    data class Metric(
        val amount: Double,
        val unitLong: String,
        val unitShort: String
    )

    /*** called from Measures object class */
    data class Us(
        val amount: Double,
        val unitLong: String,
        val unitShort: String
    )
//}
