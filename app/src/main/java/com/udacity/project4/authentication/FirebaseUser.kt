package com.udacity.project4.authentication

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseUserViewModel : LiveData<FirebaseUser?>() {

    private val firebaseInstance = FirebaseAuth.getInstance()
    private val authStatusListener = FirebaseAuth.AuthStateListener {
        value = it.currentUser
    }

    override fun onActive() {
        firebaseInstance.addAuthStateListener(authStatusListener)
    }

    override fun onInactive(){
        firebaseInstance.removeAuthStateListener(authStatusListener)
    }
}