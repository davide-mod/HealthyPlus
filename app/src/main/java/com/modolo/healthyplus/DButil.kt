package com.modolo.healthyplus

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class DButil(private val mAuth: FirebaseAuth, private val db: FirebaseFirestore) {
    private var user: FirebaseUser? = mAuth.currentUser

    fun addUser(surname: String, dateofbirth: String){
        val newUserInfo = hashMapOf(
            "email" to "${user?.email}",
            "name" to "${user?.displayName}",
            "surname" to surname,
            "dateofbirth" to dateofbirth,
            "mealplanner" to true,
            "fitnesstraker" to true
        )

        db.collection("user").document(user?.uid.toString()).set(newUserInfo).addOnSuccessListener {
            Log.i("devdebug", "Update done correctly")
        }.addOnFailureListener {
            Log.i("devdebug", "Update not done correctly")
        }

    }
}