package com.modolo.healthyplus.mealplanner

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.modolo.healthyplus.DButil
import com.modolo.healthyplus.mealplanner.food.Food
import java.time.LocalDateTime

class MealsSharedViewModel: ViewModel() {
    val mAuth  = FirebaseAuth.getInstance()
    val meals = MutableLiveData(ArrayList<Meal>())
    var mealtoEdit = MutableLiveData(
        Meal(
            "", ArrayList(), LocalDateTime.now().toString(),
            ispreset = false,
            isdone = false,
            id = ""
        )
    )

    fun addMeal(meal: Meal) {
        meals.value!!.add(meal)
        DButil(mAuth, Firebase.firestore).addMeal(meal)
        Log.i("devdebug", "viewModel: added ${meal.name} with id ${meal.id}")
    }

    fun getMeals(): ArrayList<Meal> {
        return ArrayList(meals.value!!)
    }

    fun setMealToEdit(meal: Meal) {
        mealtoEdit = MutableLiveData(meal)
    }

    fun removeMeal(id: String) {
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
                "", ArrayList(), LocalDateTime.now().toString(),
                ispreset = false,
                isdone = false,
                id = ""
            )
        )
    }

    fun getEdit(): Meal {
        return mealtoEdit.value!!
    }
}

