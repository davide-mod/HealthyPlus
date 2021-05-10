package com.modolo.healthyplus.mealplanner

import com.modolo.healthyplus.mealplanner.food.Food
import java.time.LocalDateTime
import kotlin.collections.ArrayList
import java.io.Serializable

data class Meal(var name: String, var foodList: MutableList<Food>, var date: String, var ispreset: Boolean, var isdone: Boolean, var id: String): Serializable