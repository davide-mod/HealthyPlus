package com.modolo.healthyplus.mealplanner

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.modolo.healthyplus.DButil
import com.modolo.healthyplus.mealplanner.food.Food
import com.modolo.healthyplus.mealplanner.mealdb.Meal
import com.modolo.healthyplus.mealplanner.mealdb.MealRepository
import java.time.LocalDateTime

class MealsSharedViewModel(val app: Application) : AndroidViewModel(app) {
    private val mAuth = FirebaseAuth.getInstance()
    private val mealsdb = MealRepository(app)
    var meals = MutableLiveData<List<Meal>>()
    var mealToEdit: Meal? = null
    init {
        mealsdb.getAll()
        meals = mealsdb.mealData
        Log.i("devdebug", "ViewModel init: $meals")
    }

    fun setMealtoEdit(meal: Meal){
        mealToEdit = meal
    }
    fun getMealtoEdit(): Meal{
        return mealToEdit!!
    }

    fun insertMeal(meal: Meal) {
        mealsdb.insertMeal(meal)
        DButil(mAuth, Firebase.firestore).addMeal(meal)
    }

    fun deleteMeal(meal: Meal) {
        mealsdb.deleteMeal(meal)
        DButil(mAuth, Firebase.firestore).deleteMeal(meal)
    }


    fun getPresets(): ArrayList<Meal>{
        val presets = ArrayList<Meal>()
        meals.value!!.forEach {
            if(it.ispreset)
                presets.add(it)
        }
        return presets
    }
    /*val mAuth  = FirebaseAuth.getInstance()
    var meals = MutableLiveData(ArrayList<Meal>())
    var mealtoEdit = MutableLiveData(
        Meal(
            "", ArrayList(), LocalDateTime.now().toString(),
            ispreset = false,
            isdone = false,
            id = ""
        )
    )

    fun addMeal(meal: Meal) {
        meal.id=(meals.value!!.size+1).toString()
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

    fun setMeals(tmpmeals: ArrayList<Meal>){
        meals.value = tmpmeals
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
    }*/
}

