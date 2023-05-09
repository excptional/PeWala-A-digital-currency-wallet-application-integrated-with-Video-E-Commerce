package com.example.trigeredgedigitalcurrencyproject.db

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import org.mindrot.jbcrypt.BCrypt
import java.security.SecureRandom
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

    private val transactionDetailsLiveData = MutableLiveData<ArrayList<DocumentSnapshot>>()
    val transactionDetails: LiveData<ArrayList<DocumentSnapshot>>
        get() = transactionDetailsLiveData

    private val redeemRequestDetailsLiveData = MutableLiveData<ArrayList<DocumentSnapshot>>()
    val redeemRequestDetails: LiveData<ArrayList<DocumentSnapshot>>
        get() = redeemRequestDetailsLiveData

    private val contactDetailsLiveData = MutableLiveData<ArrayList<DocumentSnapshot>>()
    val contactDetails: LiveData<ArrayList<DocumentSnapshot>>
        get() = contactDetailsLiveData

    private val payerDetailsLiveData = MutableLiveData<ArrayList<String>>()
    val payerDetails: LiveData<ArrayList<String>>
        get() = payerDetailsLiveData

    private val limitLivedata = MutableLiveData<Double>()
    val limitData: LiveData<Double>
        get() = limitLivedata

    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    fun fetchAccountDetails(uid: String) {
        firebaseDB.collection("Users").document(uid).get()
            .addOnSuccessListener {
                val list = ArrayList<String>()
                list.add(it.getString("Name").toString())
                list.add(it.getString("Phone").toString())
                list.add(it.getString("Card Id").toString())
                list.add(it.getString("Image Url").toString())
                list.add(it.getString("QR Code").toString())
                list.add(it.getString("Balance").toString())
                list.add(it.getString("PIN").toString())
                accDetailsLiveData.postValue(list)
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun fetchTransactionDetails(uid: String) {
        firebaseDB.collection("Transaction Records").document("Transaction Records").collection(uid)
            .orderBy(
                "TId", Query.Direction.DESCENDING
            )
            .get()
            .addOnSuccessListener { documents ->
                val list = arrayListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                dbResponseLiveData.postValue(Response.Success())
                transactionDetailsLiveData.postValue(list)
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun uploadImageToStorage(imageUri: Uri, user: FirebaseUser) {
        val ref =
            firebaseStorage.reference.child("images/${user.uid}/${imageUri.lastPathSegment}")
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

    private fun addAddMoneyRecords(amount: String, note: String, tId: String, uid: String) {
        val time =
            SimpleDateFormat("MMM dd, yyyy 'at' HH:mm aa", Locale.getDefault()).format(Date())
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
            "User Id" to "",
            "User Name" to "",
            "User Phone" to "",
            "Note" to note
        )

        firebaseDB.collection("Add Money Records").document(uid).collection(date)
            .document(tId)
            .set(data1)
        firebaseDB.collection("Transaction Records").document(uid).collection(date)
            .document(tId).set(data2)

    }

    @SuppressLint("SuspiciousIndentation")
    fun addMoney(amount: String, note: String, tId: String, uid: String) {
        val doc = firebaseDB.collection("Users").document(uid)
            doc.get().addOnSuccessListener {
            if (it.exists()) {
                val amountDouble = amount.toDouble()
                val balance = it.getString("Balance")!!.toDouble()
                val finalBalance = balance + amountDouble
                doc.update("Balance", finalBalance.toInt().toString())
                addAddMoneyRecords(amount, note, tId, uid)
                dbResponseLiveData.postValue(Response.Success())
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun getPayerDetails(id: String) {
        val ref = firebaseDB.collection("Users")
        ref.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val list = arrayListOf<String>()
                if (document.getString("Card Id") == id) {
                    list.add(document.getString("Name").toString())
                    list.add(document.getString("Phone").toString())
                    list.add(document.getString("Image Url").toString())
                    list.add(document.getString("Uid").toString())
                    payerDetailsLiveData.postValue(list)
                    break
                } else {
                    payerDetailsLiveData.postValue(list)
                }
            }
        }
    }

    fun changePIN(uid: String, PIN: String) {
        val salt = BCrypt.gensalt(10, SecureRandom())
        val hashedPIN = BCrypt.hashpw(PIN, salt)
        val doc = firebaseDB.collection("Users").document(uid)
        doc.get().addOnSuccessListener {
            if (it.exists()) {
                dbResponseLiveData.postValue(Response.Success())
                doc.update("PIN", hashedPIN)
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun payment(senderUid: String, receiverUid: String, amount: String, note: String) {

        val doc1 = firebaseDB.collection("Users").document(senderUid)
        val doc2 = firebaseDB.collection("Users").document(receiverUid)

        doc1.get().addOnSuccessListener {
            if (it.exists()) {
                val amountDouble = amount.toDouble()
                val balance = it.getString("Balance")!!.toDouble()
                val finalBalance = balance - amountDouble
                doc1.update("Balance", finalBalance.toInt().toString())
                dbResponseLiveData.postValue(Response.Success())
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

        doc2.get().addOnSuccessListener {
            if (it.exists()) {
                val amountDouble = amount.toDouble()
                val balance = it.getString("Balance")!!.toDouble()
                val finalBalance = balance + amountDouble
                doc2.update("Balance", finalBalance.toInt().toString())
                dbResponseLiveData.postValue(Response.Success())
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun addTransaction(
        amount: String,
        note: String,
        tId: String,
        senderUid: String,
        receiverUid: String,
        senderName: String,
        senderPhone: String,
        receiverName: String,
        receiverPhone: String,
        receiverImgUrl: String,
        time: String
    ) {
        val data1 = mapOf(
            "Amount" to amount,
            "TId" to tId,
            "Time" to time,
            "Operation" to "Send",
            "User Id" to receiverUid,
            "User Name" to receiverName,
            "User Phone" to receiverPhone,
            "Note" to note
        )

        val data2 = mapOf(
            "Amount" to amount,
            "TId" to tId,
            "Time" to time,
            "Operation" to "Receive",
            "User Id" to senderUid,
            "User Name" to senderName,
            "User Phone" to senderPhone,
            "Note" to note
        )

        firebaseDB.collection("Transaction Records").document("Transaction Records")
            .collection(senderUid).document(tId)
            .set(data1)
            .addOnSuccessListener {
                dbResponseLiveData.postValue(Response.Success())
                addContacts(receiverName, receiverPhone, receiverImgUrl, senderUid, receiverUid)
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
        firebaseDB.collection("Transaction Records").document("Transaction Records")
            .collection(receiverUid).document(tId)
            .set(data2)
            .addOnSuccessListener {
                dbResponseLiveData.postValue(Response.Success())
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    private fun addContacts(
        name: String,
        phone: String,
        imgUrl: String,
        selfUid: String,
        contactUid: String
    ) {
        val data = mapOf(
            "Name" to name,
            "Phone No" to phone,
            "Image Url" to imgUrl,
            "Uid" to contactUid
        )
        firebaseDB.collection("Contacts").document("Contacts").collection(selfUid)
            .document(contactUid).set(data)
    }

    fun fetchContacts(uid: String) {
        firebaseDB.collection("Contacts").document("Contacts").collection(uid).get()
            .addOnSuccessListener { documents ->
                val list = arrayListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                contactDetailsLiveData.postValue(list)
            }
    }

    fun sendRedeemRequest(uid: String, amount: String) {
        val time =
            SimpleDateFormat("MMM dd, yyyy 'at' HH:mm aa", Locale.getDefault()).format(Date())
        val timeInMillis = System.currentTimeMillis()
        val data = mapOf(
            "Amount" to amount,
            "Request send" to time,
            "Request approve" to "",
            "Status" to "Pending",
            "Order" to System.currentTimeMillis()
        )
        firebaseDB.collection("Redeem Request").document("Redeem Request").collection(uid)
            .document(timeInMillis.toString()).set(data)
            .addOnSuccessListener {
                redeem(uid, amount)
                dbResponseLiveData.postValue(Response.Success())
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    private fun redeem(uid: String, amount: String) {
        val doc = firebaseDB.collection("Users").document(uid)
        doc.get().addOnSuccessListener {
            if (it.exists()) {
                val updatedAmount = it.getString("Balance")!!.toDouble() - amount.toDouble()
                doc.update("Balance", updatedAmount.toInt().toString())
            }
        }
    }

    fun fetchRedeemRequest(uid: String) {
        firebaseDB.collection("Redeem Request").document("Redeem Request").collection(uid).orderBy("Order", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                val list = arrayListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                redeemRequestDetailsLiveData.postValue(list)
            }
    }

    private fun getErrorMassage(e: Exception): String {
        val colonIndex = e.toString().indexOf(":")
        return e.toString().substring(colonIndex + 2)
    }

}