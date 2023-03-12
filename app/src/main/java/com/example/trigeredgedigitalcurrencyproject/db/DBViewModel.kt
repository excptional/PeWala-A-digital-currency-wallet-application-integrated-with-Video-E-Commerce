package com.example.trigeredgedigitalcurrencyproject.db

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser

class DBViewModel(application: Application): AndroidViewModel(application) {

    private val dbRepository: DBRepository = DBRepository(application)
    val dbResponse: LiveData<Response<String>>
        get() = dbRepository.dbResponse
    val accDetails: LiveData<ArrayList<String>>
        get() = dbRepository.accDetails

    fun fetchAccountDetails(user: FirebaseUser){
        dbRepository.fetchAccountDetails(user)
    }

    fun uploadImageToStorage(imageUri: Uri, user: FirebaseUser) {
        dbRepository.uploadImageToStorage(imageUri, user)
    }
}