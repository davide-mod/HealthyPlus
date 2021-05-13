package com.modolo.healthyplus.mealplanner.food

import java.io.Serializable
/*semplice oggetto Food*/
data class Food(var name: String, var quantity: Float, var udm: String, var kcal: Float) :
    Serializable