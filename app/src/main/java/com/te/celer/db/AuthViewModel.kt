package com.te.celer.db

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(application: Application): AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)

    val userdata: LiveData<FirebaseUser?>
        get() = authRepository.userData

    val response: LiveData<Response<String>>
        get() = authRepository.response

    val alreadyRegistered: LiveData<Boolean>
        get() = authRepository.alreadyRegistered

    fun login(phone: String, password: String) {
        authRepository.login(phone, password)
    }

    fun logout() {
        authRepository.logOut()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun signUp(
        name: String,
        phone: String,
        aadharNo: String,
        password: String,
        userType: String
    ) {
        authRepository.signUp(name, phone, aadharNo, password, userType)
    }

    fun isRegistered(phone: String) {
        authRepository.isRegistered(phone)
    }

}