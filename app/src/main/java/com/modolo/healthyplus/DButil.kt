package com.modolo.healthyplus

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class DButil(private val mAuth: FirebaseAuth, private val db: FirebaseFirestore) {
    private var user: FirebaseUser? = mAuth.currentUser

    fun addUser(){
        //email + psw + name + surname + date of birth

    }
}