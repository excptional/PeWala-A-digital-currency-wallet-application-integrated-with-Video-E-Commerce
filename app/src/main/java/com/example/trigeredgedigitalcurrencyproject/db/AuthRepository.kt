package com.example.trigeredgedigitalcurrencyproject.db

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.type.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import io.github.g0dkar.qrcode.QRCode
import io.github.g0dkar.qrcode.render.Colors
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class AuthRepository(private val application: Application) {

    private val userLiveData = MutableLiveData<FirebaseUser?>()
    val userData: LiveData<FirebaseUser?>
        get() = userLiveData
    private val responseLiveData = MutableLiveData<Response<String>>()
    val response: LiveData<Response<String>>
        get() = responseLiveData

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

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
        aadharNo: String,
        password: String
    ) {
        val data = mapOf(
            "Name" to name,
            "Password" to password,
            "Phone" to phone,
            "Card Id" to "$phone@digital",
            "Aadhar" to aadharNo,
            "Uid" to "",
            "QR Code" to "",
            "Image Url" to "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/user2.png?alt=media&token=91a4d9d4-71cc-4d25-919b-eed55ff51842"
        )
        firebaseAuth.createUserWithEmailAndPassword("$phone@gmail.com", password)
            .addOnSuccessListener {
                val doc = firebaseDB.collection("Users").document(firebaseAuth.currentUser!!.uid)
                doc.set(data)
                doc.get().addOnSuccessListener {
                    doc.update("Uid", firebaseAuth.currentUser!!.uid)
                    qrGenerator("$phone@digital")
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
                            firebaseDB.collection("Users").document(firebaseAuth.currentUser!!.uid)
                        doc.get().addOnSuccessListener {
                            if (it.exists()) {
                                doc.update("QR Code", uri.toString())
                            }
                        }
                    }
            }
    }

}