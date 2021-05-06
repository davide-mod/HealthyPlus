package com.modolo.healthyplus.mealplanner

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MealsSharedViewModel: ViewModel() {
    val meals = MutableLiveData(mutableListOf<Meal>())

    fun addMeal(meal: Meal){
        Log.i("devdebug", "addMeal: ${meals.value!!}")
        meals.value!!.add(meal)
    }

    fun getMeals(): ArrayList<Meal>{
        Log.i("devdebug", "getMeals: ${meals.value!!}")
        return ArrayList(meals.value!!)
    }

    fun removeMeal(meal: Meal){
        for (element in meals.value!!)
        {
            if(element.id == meal.id){
                meals.value!!.remove(element)
                break
            }
        }
    }
    fun updateMeal(meal: Meal){
        for (element in meals.value!!)
        {
            if(element.id == meal.id){
                Log.i("devdebug","element before edit: $element")
                element.name = meal.name
                element.foodList = meal.foodList
                Log.i("devdebug","element after edit: $element")
                break
            }
        }
    }

    fun getLastId(): Int{
        var id = 0
        for(meal in meals.value!!)
            id = if(meal.id > id) meal.id else id
        return id
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