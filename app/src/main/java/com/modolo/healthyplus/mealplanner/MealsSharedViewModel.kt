package com.modolo.healthyplus.mealplanner

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.modolo.healthyplus.mealplanner.food.Food
import java.time.LocalDateTime

class MealsSharedViewModel : ViewModel() {
    val meals = MutableLiveData(ArrayList<Meal>())
    var mealtoEdit = MutableLiveData(
        Meal(
            "", ArrayList(), LocalDateTime.now(),
            ispreset = false,
            isdone = false,
            id = 0
        )
    )

    fun addMeal(meal: Meal) {
        meals.value!!.add(meal)
        Log.i("devdebug", "viewModel: added ${meal.name} with id ${meal.id}")
    }

    fun getMeals(): ArrayList<Meal> {
        return ArrayList(meals.value!!)
    }

    fun setMealToEdit(meal: Meal) {
        mealtoEdit = MutableLiveData(meal)
    }

    fun removeMeal(id: Int) {
        Log.i("devdebug", "viewModel: mealList size ${meals.value!!.size}")
        val iter: MutableIterator<Meal> = meals.value!!.iterator()
        while (iter.hasNext()) {
            val meal = iter.next()
            if (meal.id == id) iter.remove()
        }
    }

    fun updateMeal(mealEdited: Meal) {
        Log.i(
            "devdebug",
            "viewModel: mealList before edit ${meals.value!!} with ${mealEdited.name}"
        )
        meals.value!!.forEachIndexed { index, meal ->
            if (meal.id == mealEdited.id) {
                meals.value!![index] = mealEdited
            }
        }
        Log.i("devdebug", "viewModel: mealList after edit ${meals.value!!}")

    }

    fun resetEdit() {
        mealtoEdit = MutableLiveData(
            Meal(
                "", ArrayList(), LocalDateTime.now(),
                ispreset = false,
                isdone = false,
                id = 0
            )
        )
    }

    fun getEdit(): Meal {
        return mealtoEdit.value!!
    }

    fun getNewId(): Int {
        var id = 1
        for (meal in meals.value!!)
            id = if (meal.id > id) meal.id else id
        return id + 1
    }
}

