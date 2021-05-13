package com.modolo.healthyplus.fitnesstracker

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.modolo.healthyplus.fitnesstracker.workoutdb.Workout

/*classe per comunicare col database online di Firebase/Firestore*/
class DButilFitnessTracker(mAuth: FirebaseAuth, private val db: FirebaseFirestore) {
    private var user: FirebaseUser? = mAuth.currentUser

    /*aggiungo un allenamento a /user/utente attuale/workouts con id = workout.id.toString()*/
    fun addWorkout(workout: Workout) {
        db.collection("user").document(user?.uid.toString()).collection("workouts").document(workout.id.toString())
            .set(workout)
    }
    /*rimuovo l'allenamento da /user/utente attuale/workouts con id = workout.id.toString()*/
    fun deleteWorkout(workout: Workout){
        db.collection("user").document(user?.uid.toString()).collection("workouts").document(workout.id.toString())
            .delete()
    }
    /*recupero dall'online tutti i "workouts" dell'utente*/
    fun getAll(): CollectionReference {
        return db.collection("user").document(user?.uid.toString()).collection("workouts")
    }
}