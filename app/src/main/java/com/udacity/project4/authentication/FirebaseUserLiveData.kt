package com.udacity.project4.authentication

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseUserLiveData: LiveData<FirebaseUser?>() {

    private val fireBaseAuth = FirebaseAuth.getInstance()
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        value = firebaseAuth.currentUser
    }

    override fun onActive() {
        fireBaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onInactive() {
        fireBaseAuth.removeAuthStateListener(authStateListener)
    }
}