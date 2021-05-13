package com.modolo.healthyplus.mealplanner

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.modolo.healthyplus.mealplanner.mealdb.Meal
import com.modolo.healthyplus.mealplanner.mealdb.MealRepository

/*Viewmodel utilizzata dal modulo MealPlanner per far comunicare i vari fragment e per comunicare col
* database
* */
class MealsSharedViewModel(val app: Application) : AndroidViewModel(app) {
    /*Di seguito ci sono, gli oggetti per l'autenticazione e per comunicare col db locale, la
    * lista di pasti "in RAM" e l'oggetto pasto da modificare
    * */
    private val mAuth = FirebaseAuth.getInstance()
    private val mealsdb = MealRepository(app)
    var meals = MutableLiveData<List<Meal>>()
    private var mealToEdit: Meal? = null

    /*inizializzo prendendo dal DB locale tutti i pasti e salvandoli localmente*/
    init {
        mealsdb.getAll()
        meals = mealsdb.mealData
        Log.i("hp_MealSharedViewModel", "init: $meals")
    }
    /*funzione alla quale si passa il pasto da modificare*/
    fun setMealtoEdit(meal: Meal){
        mealToEdit = meal
    }
    /*Funzione con la quale si recupera il pasto da modificare*/
    fun getMealtoEdit(): Meal{
        return mealToEdit!!
    }
    /*inserimento pasto sia nel DB locale che su Firebase*/
    fun insertMeal(meal: Meal) {
        mealsdb.insertMeal(meal)
        DButilMealPlanner(mAuth, Firebase.firestore).addMeal(meal)
    }
    /*rimozione pasto sia dal DB locale che da Firebase*/
    fun deleteMeal(meal: Meal) {
        mealsdb.deleteMeal(meal)
        DButilMealPlanner(mAuth, Firebase.firestore).deleteMeal(meal)
    }
    /*calcolo ultimo ID presente e lo restituisco*/
    fun getLastId(): Int{
        var maxId = 0
        meals.value!!.forEach {
            maxId = if(it.id > maxId) it.id else maxId
        }
        return maxId
    }
    /*restituisce la lista dei pasti impostati come preset*/
    fun getPresets(): ArrayList<Meal>{
        val presets = ArrayList<Meal>()
        meals.value!!.forEach {
            if(it.ispreset)
                presets.add(it)
        }
        return presets
    }
}

