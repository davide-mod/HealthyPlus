package com.modolo.healthyplus.mealplanner.mealdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.modolo.healthyplus.mealplanner.food.Food

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String,
    var foodList: String,
    var date: String,
    var ispreset: Boolean,
    var isdone: Boolean
)