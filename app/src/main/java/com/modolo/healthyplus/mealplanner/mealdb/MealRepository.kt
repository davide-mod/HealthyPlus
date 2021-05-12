package com.modolo.healthyplus.mealplanner.mealdb

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.modolo.healthyplus.DButil
import com.modolo.healthyplus.mealplanner.DButilMealPlanner
import com.modolo.healthyplus.mealplanner.food.Food
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MealRepository(app: Application) {

    var mealData = MutableLiveData<List<Meal>>()
    private val mealDAO = MealDatabase.getDatabase(app).mealDAO()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val data: List<Meal> = mealDAO.getAll()
            if (data.isNullOrEmpty()) {
                val mAuth = FirebaseAuth.getInstance()
                val mealsFromGoogle = DButilMealPlanner(mAuth, Firebase.firestore).getAll()
                val mealsTmp = mutableListOf<Meal>()
                mealsFromGoogle.get().addOnSuccessListener { doc ->
                    doc.forEach { me ->
                        Log.i("devdebug", "MealRepo: $me")
                        val mealTmp = Meal(
                            id = (me.data["id"] as Long).toInt(),
                            name = me.data["name"] as String,
                            foodList = me.data["foodList"] as String,
                            date = (me.data["date"] as String),
                            ispreset = me.data["ispreset"] as Boolean,
                            isdone = me.data["isdone"] as Boolean,
                        )
                        mealsTmp.add(mealTmp)
                        insertMeal(mealTmp)
                    }
                }
                mealData.postValue(mealDAO.getAll())
                //insertMeals(mealsTmp)
            } else {
                //se il database di pasti è già popolato recupero le entry
                mealData.postValue(data)
            }
        }
    }

    //recupero tutti gli ingredienti nel database
    fun getAll() {
        CoroutineScope(Dispatchers.IO).launch {
            mealData.postValue(mealDAO.getAll())
        }
    }

    private fun insertMeals(meals: MutableList<Meal>){
        CoroutineScope(Dispatchers.IO).launch {
            meals.forEach {
                mealDAO.insertMeal(it)
            }
        }
    }

    //inserisco un nuovo ingrediente e aggiorno la lista richiedendola
    fun insertMeal(meal: Meal) {
        CoroutineScope(Dispatchers.IO).launch {
            mealDAO.insertMeal(meal)
            mealData.postValue(mealDAO.getAll())
        }
    }

    //elimino un ingrediente e aggiorno la lista richiedendola
    fun deleteMeal(meal: Meal) {
        CoroutineScope(Dispatchers.IO).launch {
            mealDAO.deleteMeal(meal)
            mealData.postValue(mealDAO.getAll())
        }
    }

}