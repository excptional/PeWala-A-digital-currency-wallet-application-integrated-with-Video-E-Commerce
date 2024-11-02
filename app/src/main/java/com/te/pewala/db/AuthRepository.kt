package com.te.pewala.db

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class AuthRepository(private val application: Application) {

    private val aesCrypt = AESCrypt()
    private val key = ByteArray(32)

    private val userLiveData = MutableLiveData<FirebaseUser?>()
    val userData: LiveData<FirebaseUser?>
        get() = userLiveData
    private val responseLiveData = MutableLiveData<Response<String>>()
    val response: LiveData<Response<String>>
        get() = responseLiveData

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    fun logOut() {
        firebaseAuth.signOut()
        userLiveData.postValue(null)
    }

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun signUp(
        name: String,
        phone: String,
        aadharNo: String,
        password: String,
        userType: String
    ) {
        key.fill(1)
        val hashedPassword = aesCrypt.encrypt(password, key)
        val hashedAadhar = aesCrypt.encrypt(aadharNo, key)
        val data = mutableMapOf(
            "name" to name,
            "password" to hashedPassword,
            "phone" to phone,
            "card_id" to "$phone@smart",
            "aadhar" to hashedAadhar,
            "uid" to "",
            "qr_code" to "",
            "pin" to "",
            "balance" to "0",
            "user_type" to userType,
            "status" to "",
            "pan" to "",
            "gstin" to "",
            "trade_license" to "",
            "image_url" to "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/user2.png?alt=media&token=91a4d9d4-71cc-4d25-919b-eed55ff51842"
        ).apply {
            if (userType == "Seller") {
                put("status", "Not Verified")
            }
        }

        firebaseAuth.createUserWithEmailAndPassword("$phone@gmail.com", password)
            .addOnSuccessListener {
                val doc = firebaseDB.collection("users").document(firebaseAuth.currentUser!!.uid)
                doc.set(data)
                doc.get().addOnSuccessListener {
                    doc.update("uid", firebaseAuth.currentUser!!.uid)
                    qrGenerator("$phone@smart")
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

    private fun qrGenerator(cardId: String) {
        val size = 512
        val bits = QRCodeWriter().encode(cardId, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(
                        x,
                        y,
                        if (bits[x, y]) android.graphics.Color.parseColor("#495963") else android.graphics.Color.WHITE
                    )
                }
            }
        }
        val tempFile = File.createTempFile("temp", ".png")
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        val fileOutPut = FileOutputStream(tempFile)
        fileOutPut.write(bitmapData)
        fileOutPut.flush()
        fileOutPut.close()
        val imgUri = Uri.fromFile(tempFile)
        val ref = firebaseStorage.reference.child("qr_images/${firebaseAuth.currentUser!!.uid}/")
        ref.putFile(imgUri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { uri ->
                        val doc =
                            firebaseDB.collection("users").document(firebaseAuth.currentUser!!.uid)
                        doc.get().addOnSuccessListener {
                            if (it.exists()) {
                                doc.update("qr_code", uri.toString())
                            }
                        }
                    }
            }
    }

    init {
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(firebaseAuth.currentUser)
        } else {
            userLiveData.postValue(null)
        }
    }

}