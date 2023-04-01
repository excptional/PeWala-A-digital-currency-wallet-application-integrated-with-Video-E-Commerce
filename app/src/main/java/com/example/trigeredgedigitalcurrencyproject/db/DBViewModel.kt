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
    val payerDetails: LiveData<ArrayList<String>>
        get() = dbRepository.payerDetails
    val dailyAddLimit: LiveData<Double>
        get() = dbRepository.limitData

    fun fetchAccountDetails(user: FirebaseUser){
        dbRepository.fetchAccountDetails(user)
    }

    fun uploadImageToStorage(imageUri: Uri, user: FirebaseUser) {
        dbRepository.uploadImageToStorage(imageUri, user)
    }

    fun checkDailyAddAmountLimit(user: FirebaseUser) {
        dbRepository.checkDailyAddAmountLimit(user)
    }

    fun addAddMoneyRecords(amount: String, note: String, tId: String, user: FirebaseUser) {
        dbRepository.addAddMoneyRecords(amount, note, tId, user)
    }

    fun getPayerDetails(id: String) {
        dbRepository.getPayerDetails(id)
    }

    fun changePIN(uid: String, PIN: String) {
        dbRepository.changePIN(uid, PIN)
    }

    fun payment(senderUid: String, receiverUid: String, amount: String, note: String) {
        dbRepository.payment(senderUid, receiverUid, amount, note)
    }
}