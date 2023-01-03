package com.udacity.project4.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.udacity.project4.authentication.State.*

class LoginViewModel : ViewModel() {

    val state = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AUTHENTICATED
        } else {
            UNAUTHENTICATED
        }
    }
}