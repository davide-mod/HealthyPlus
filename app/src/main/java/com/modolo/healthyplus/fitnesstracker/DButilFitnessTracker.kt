package com.modolo.healthyplus.fitnesstracker

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.modolo.healthyplus.fitnesstracker.workoutdb.Workout

class DButilFitnessTracker(mAuth: FirebaseAuth, private val db: FirebaseFirestore) {
    private var user: FirebaseUser? = mAuth.currentUser

    fun addWorkout(workout: Workout) {
        db.collection("user").document(user?.uid.toString()).collection("workouts").document(workout.id.toString())
            .set(workout)
    }

    fun deleteWorkout(workout: Workout){
        db.collection("user").document(user?.uid.toString()).collection("workouts").document(workout.id.toString())
            .delete()
    }

    fun getAll(): CollectionReference {
        return db.collection("user").document(user?.uid.toString()).collection("workouts")
    }
}