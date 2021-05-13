package com.modolo.healthyplus.mealplanner.mealdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/*query per il database locale*/
@Dao
interface MealDAO {

    @Query("SELECT * FROM meals ORDER BY date")
    suspend fun getAll(): List<Meal>

    @Insert
    suspend fun insertMeal(meal: Meal)

    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Query("UPDATE meals SET name=:name, foodList=:foodList, date=:data, ispreset=:ispreset, isdone=:isdone WHERE id=:id")
    suspend fun updateMeal(id: Int,
                                  name: String,
                                  foodList: String,
                                  data: String,
                                  ispreset: Boolean,
                                  isdone: Boolean)

    @Query("DELETE FROM meals")
    suspend fun deleteAll()
}