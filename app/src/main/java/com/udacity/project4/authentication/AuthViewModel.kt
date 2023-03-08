package com.udacity.project4.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map


enum class AuthStatus{
    AUTHENTICATED, UNAUTHENTICATED
}

class AuthViewModel : ViewModel() {

  val authStatus by lazy {
      FirebaseUserViewModel().map { firebaseUser ->
          if (firebaseUser != null)
              AuthStatus.AUTHENTICATED
          else {
              AuthStatus.UNAUTHENTICATED
          }
      }
  }
}