package com.modolo.healthyplus.mealplanner

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MealsSharedViewModel: ViewModel() {
    val meals = MutableLiveData(mutableListOf<Meal>())

    fun addMeal(meal: Meal){
        meals.value!!.add(meal)
    }

    fun getMeals(): ArrayList<Meal>{
        return ArrayList(meals.value!!)
    }

    fun deleteMeal(category: String, mealId: Int) {
        if(category == "presets"){
            for(meal in meals.value!!){
                if (meal.id == mealId)
                {
                    meals.value!!.remove(meal)
                }
            }
        }
    }
}