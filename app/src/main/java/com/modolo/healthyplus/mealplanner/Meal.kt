package com.modolo.healthyplus.mealplanner

import com.modolo.healthyplus.mealplanner.food.Food
import java.time.LocalDateTime
import kotlin.collections.ArrayList
import java.io.Serializable

data class Meal(var name: String, var foodList: MutableList<Food>, var date: LocalDateTime, var ispreset: Boolean, var isdone: Boolean, var id: Int): Serializable