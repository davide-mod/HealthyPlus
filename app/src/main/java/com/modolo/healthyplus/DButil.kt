package com.modolo.healthyplus

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class DButil(mAuth: FirebaseAuth, private val db: FirebaseFirestore) {
    private var user: FirebaseUser? = mAuth.currentUser

    /*aggiungo il nuovo utente al mio database di utenti in modo da poter salvare ulteriori informazioni,
    come pasti o schede dai moduli*/
    fun addUser(surname: String, dateofbirth: String) {
        val newUserInfo = hashMapOf(
            "email" to "${user?.email}",
            "name" to "${user?.displayName}",
            "surname" to surname,
            "dateofbirth" to dateofbirth,
            "mealplanner" to true,
            //"nuovomodulo" to true,
            "fitnesstraker" to true
            /*ho aggiunto i campi dei moduli per una futura implementazione in cui si
            possano impostare i moduli visibili da remoto*/
        )

        db.collection("user").document(user?.uid.toString()).set(newUserInfo).addOnSuccessListener {
            Log.i("hp_DButil", "Utente aggiunto correttamente")
        }.addOnFailureListener {
            Log.i("hp_DButil", "Utente non aggiunto\n$it")
        }

    }
}