package com.modolo.healthyplus.mealplanner.mealdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.modolo.healthyplus.mealplanner.food.Food

/*oggetto Meal che è anche il tipo di entità nel database locale*/
@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var foodList: String,
    var date: String,
    var ispreset: Boolean,
    var isdone: Boolean
)