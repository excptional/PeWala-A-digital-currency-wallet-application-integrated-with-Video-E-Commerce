package com.example.trigeredgedigitalcurrencyproject.db

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(private val application: Application) {

    private val userLiveData = MutableLiveData<FirebaseUser?>()
    val userData: LiveData<FirebaseUser?>
        get() = userLiveData
    private val responseLiveData = MutableLiveData<Response<String>>()
    val response: LiveData<Response<String>>
        get() = responseLiveData

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun login(phone: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword("$phone@gmail.com", password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    responseLiveData.postValue(Response.Success())
                    userLiveData.postValue(firebaseAuth.currentUser)
                } else {
                    responseLiveData.postValue(Response.Failure(getErrorMassage(task.exception!!)))
                }
            }
    }

    fun signUp(
        name: String,
        phone: String,
        cardId: String,
        aadharNo: String,
        password: String
    ) {
        val data = mapOf(
            "Name" to name,
            "Password" to password,
            "Phone" to phone,
            "Card Id" to cardId,
            "Aadhar" to aadharNo,
            "Uid" to ""
        )
        firebaseAuth.createUserWithEmailAndPassword("$phone@gmail.com", password).addOnSuccessListener {
            val doc = firebaseDB.collection("Users").document(firebaseAuth.currentUser!!.uid)
            doc.set(data)
            doc.get().addOnSuccessListener {
                doc.update("Uid", firebaseAuth.currentUser!!.uid)
            }
            responseLiveData.postValue(Response.Success())
            userLiveData.postValue(firebaseAuth.currentUser)
        }
            .addOnFailureListener {
                responseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    private fun getErrorMassage(e: Exception): String {
        val colonIndex = e.toString().indexOf(":")
        return e.toString().substring(colonIndex + 2)
    }

    init {
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(firebaseAuth.currentUser)
        } else {
            userLiveData.postValue(null)
        }
    }

}