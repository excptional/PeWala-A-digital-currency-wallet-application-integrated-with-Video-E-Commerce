package com.example.trigeredgedigitalcurrencyproject.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(application: Application): AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)

    val userdata: LiveData<FirebaseUser?>
        get() = authRepository.userData
    val response: LiveData<Response<String>>
        get() = authRepository.response

    fun login(phone: String, password: String) {
        authRepository.login(phone, password)
    }

    fun signUp(
        name: String,
        phone: String,
        cardId: String,
        aadharNo: String,
        password: String
    ) {
        authRepository.signUp(name, phone, cardId, aadharNo, password)
    }

}