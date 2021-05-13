package com.modolo.healthyplus.mealplanner

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.modolo.healthyplus.mealplanner.mealdb.Meal

/*classe per comunicare col database online di Firebase/Firestore*/
class DButilMealPlanner(mAuth: FirebaseAuth, private val db: FirebaseFirestore) {
    private var user: FirebaseUser? = mAuth.currentUser

    /*aggiungo un pasto a /user/utente attuale/meals con id = newMeal.id.toString()*/
    fun addMeal(newMeal: Meal) {
        db.collection("user").document(user?.uid.toString()).collection("meals").document(newMeal.id.toString())
            .set(newMeal)
    }
    /*rimuovo il pasto con id = newMeal.id.toString() da /user/utente attuale/meals*/
    fun deleteMeal(meal: Meal){
        db.collection("user").document(user?.uid.toString()).collection("meals").document(meal.id.toString())
            .delete()
    }
    /*recupero dall'online tutti i "meals" dell'utente*/
    fun getAll(): CollectionReference {
        return db.collection("user").document(user?.uid.toString()).collection("meals")
    }
}