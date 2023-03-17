package com.example.trigeredgedigitalcurrencyproject.db

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DBRepository(private val application: Application) {

    private val dbResponseLiveData = MutableLiveData<Response<String>>()
    val dbResponse: LiveData<Response<String>>
        get() = dbResponseLiveData

    private val accDetailsLiveData = MutableLiveData<ArrayList<String>>()
    val accDetails: LiveData<ArrayList<String>>
        get() = accDetailsLiveData

    private val limitLivedata = MutableLiveData<Double>()
    val limitData: LiveData<Double>
        get() = limitLivedata

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    fun fetchAccountDetails(user: FirebaseUser) {
        firebaseDB.collection("Users").document(user.uid).get()
            .addOnSuccessListener {
                val list = ArrayList<String>()
                list.add(it.getString("Name").toString())
                list.add(it.getString("Phone").toString())
                list.add(it.getString("Card Id").toString())
                list.add(it.getString("Image Url").toString())
                list.add(it.getString("QR Code").toString())
                list.add(it.getString("Balance").toString())
                accDetailsLiveData.postValue(list)
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun uploadImageToStorage(imageUri: Uri, user: FirebaseUser) {
        val ref = firebaseStorage.reference.child("images/${user.uid}/${imageUri.lastPathSegment}")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener {
                        uploadImageUrlToDatabase(it, user)
                    }
                    .addOnFailureListener {
                        dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                    }
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    private fun uploadImageUrlToDatabase(uri: Uri, user: FirebaseUser) {
        val doc = firebaseDB.collection("Users").document(user.uid)
        doc.get().addOnSuccessListener {
            if (it.exists()) {
                dbResponseLiveData.postValue(Response.Success())
                doc.update("Image Url", uri.toString())
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun checkDailyAddAmountLimit(user: FirebaseUser) {
        val date = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(Date())
        var temp = 0
        firebaseDB.collection("Add Money Records").document(user.uid).collection(date).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    temp += Integer.parseInt(document.getString("Amount").toString())
                }
                val limit = 10000.00 - temp
                limitLivedata.postValue(limit)
            }
    }

    fun addAddMoneyRecords(amount: String, tId: String, user: FirebaseUser) {
        val time = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm aa", Locale.getDefault()).format(Date())
        val date = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(Date())
        val data1 = mapOf(
            "Amount" to amount,
            "TId" to tId,
            "Time" to time
        )
        val data2 = mapOf(
            "Amount" to amount,
            "TId" to tId,
            "Time" to time,
            "Operation" to "Add",
            "User Id" to ""
        )

        firebaseDB.collection("Add Money Records").document(user.uid).collection(date).document(tId).set(data1)
        firebaseDB.collection("Transaction Records").document(user.uid).collection(date).document(tId).set(data2)

    }


    private fun getErrorMassage(e: Exception): String {
        val colonIndex = e.toString().indexOf(":")
        return e.toString().substring(colonIndex + 2)
    }

}