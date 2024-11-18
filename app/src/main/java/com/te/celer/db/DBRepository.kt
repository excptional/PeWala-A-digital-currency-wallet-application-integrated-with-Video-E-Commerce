package com.te.celer.db

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.io.InputStream
import java.security.SecureRandom
import java.util.Calendar
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import kotlin.collections.ArrayList
import kotlin.random.Random

class DBRepository(private val application: Application) {

    private val aesCrypt = AESCrypt()
    private val key = ByteArray(32)

    private val dbResponseLiveData = MutableLiveData<Response<String>>()
    val dbResponse: LiveData<Response<String>>
        get() = dbResponseLiveData

    private val accDetailsLiveData = MutableLiveData<DocumentSnapshot>()
    val accDetails: LiveData<DocumentSnapshot>
        get() = accDetailsLiveData

    private val productDetailsLiveData = MutableLiveData<DocumentSnapshot>()
    val productDetails: LiveData<DocumentSnapshot>
        get() = productDetailsLiveData

    private val transactionDetailsLiveData = MutableLiveData<ArrayList<DocumentSnapshot>>()
    val transactionDetails: LiveData<ArrayList<DocumentSnapshot>>
        get() = transactionDetailsLiveData

    private val chatsLiveData = MutableLiveData<ArrayList<DocumentSnapshot>>()
    val chats: LiveData<ArrayList<DocumentSnapshot>>
        get() = chatsLiveData

    private val isConversationLiveData = MutableLiveData<Boolean>()
    val isConversation: LiveData<Boolean>
        get() = isConversationLiveData

    private val conversationLiveData = MutableLiveData<MutableList<DocumentSnapshot>>()
    val conversation: LiveData<MutableList<DocumentSnapshot>>
        get() = conversationLiveData

    private val feedVideosLiveData = MutableLiveData<ArrayList<DocumentSnapshot>>()
    val feedVideos: LiveData<ArrayList<DocumentSnapshot>>
        get() = feedVideosLiveData

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

    private val productsLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val productsData: LiveData<MutableList<DocumentSnapshot>>
        get() = productsLivedata

    private val sellerProductsLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val sellerProductsData: LiveData<MutableList<DocumentSnapshot>>
        get() = sellerProductsLivedata

    private val sellerReceivedOrdersLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val sellerReceivedOrdersData: LiveData<MutableList<DocumentSnapshot>>
        get() = sellerReceivedOrdersLivedata

    private val myOrdersLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val myOrdersData: LiveData<MutableList<DocumentSnapshot>>
        get() = myOrdersLivedata

    private val pendingPaymentsLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val pendingPaymentsData: LiveData<MutableList<DocumentSnapshot>>
        get() = pendingPaymentsLivedata

    private val wishlistLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val wishlistData: LiveData<MutableList<DocumentSnapshot>>
        get() = wishlistLivedata

    private val isInWishlistLivedata = MutableLiveData<Boolean>()
    val isInWishlistData: LiveData<Boolean>
        get() = isInWishlistLivedata

    private val isInCartLivedata = MutableLiveData<Boolean>()
    val isInCartData: LiveData<Boolean>
        get() = isInCartLivedata

    private val selectedCartLivedata = MutableLiveData<ArrayList<DocumentSnapshot>>()
    val selectedCartData: LiveData<ArrayList<DocumentSnapshot>>
        get() = selectedCartLivedata

    private val cartLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val cartData: LiveData<MutableList<DocumentSnapshot>>
        get() = cartLivedata

    private val addressLivedata = MutableLiveData<DocumentSnapshot>()
    val addressData: LiveData<DocumentSnapshot>
        get() = addressLivedata

    private val videoTutorialsLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val videoTutorialsData: LiveData<MutableList<DocumentSnapshot>>
        get() = videoTutorialsLivedata

    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    fun fetchAccountDetails(uid: String) {
        firebaseDB.collection("users").document(uid).get()
            .addOnSuccessListener {
                accDetailsLiveData.postValue(it)
                dbResponseLiveData.postValue(Response.Success())
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
        senderImgUrl: String,
        receiverName: String,
        receiverPhone: String,
        receiverImgUrl: String,
        time: String
    ) {
        val data1 = mapOf(
            "amount" to amount,
            "tid" to tId,
            "time" to time,
            "operation" to "Debit",
            "operator_id" to receiverUid,
            "operator_name" to receiverName,
            "operator_phone" to receiverPhone,
            "note" to note
        )

        //Personal Transaction disabled

//        val data2 = mapOf(
//            "amount" to amount,
//            "tid" to tId,
//            "time" to time,
//            "operation" to "Credit",
//            "operator_id" to senderUid,
//            "operator_name" to senderName,
//            "operator_phone" to senderPhone,
//            "note" to note
//        )

        firebaseDB.collection("transaction_records").document("transaction_records")
            .collection(senderUid).document(tId)
            .set(data1)
            .addOnSuccessListener {
                dbResponseLiveData.postValue(Response.Success())
                addContacts(receiverName, receiverPhone, receiverImgUrl, senderUid, receiverUid)
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

        //Personal Transaction disabled
//        firebaseDB.collection("transaction_records").document("transaction_records")
//            .collection(receiverUid).document(tId)
//            .set(data2)
//            .addOnSuccessListener {
//                dbResponseLiveData.postValue(Response.Success())
//                addContacts(senderName, senderPhone, senderImgUrl, receiverUid, senderUid)
//            }
//            .addOnFailureListener {
//                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
//            }
    }

    fun fetchTransactionDetails(uid: String) {
        firebaseDB.collection("transaction_records").document("transaction_records").collection(uid)
            .orderBy(
                "time", Query.Direction.DESCENDING
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

    fun updateTransactorDetails(
        uid: String
    ) {
        val doc = firebaseDB.collection("transaction_records").document("transaction_records")
            .collection(uid)
        doc.orderBy(
            "tid", Query.Direction.DESCENDING
        )
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.exists() and document.getString("operator_name").isNullOrEmpty()) {
                        firebaseDB.collection("users").document(document.getString("operator_id")!!)
                            .get().addOnSuccessListener { details ->
                                doc.document(document.getString("tid")!!)
                                    .update("operator_name", details.getString("name"))
                                doc.document(document.getString("tid")!!)
                                    .update("operator_phone", details.getString("phone"))
                            }
                    } else break
                }
            }
    }

    fun uploadImageToStorage(imageUri: Uri, uid: String) {
        val ref =
            firebaseStorage.reference.child("profile_images/${uid}/${imageUri.lastPathSegment}")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener {
                        uploadImageUrlToDatabase(it, uid)
                    }
                    .addOnFailureListener {
                        dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                    }
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    private fun uploadImageUrlToDatabase(uri: Uri, uid: String) {
        val doc = firebaseDB.collection("users").document(uid)
        doc.get().addOnSuccessListener {
            if (it.exists()) {
                doc.update("image_url", uri.toString())
                dbResponseLiveData.postValue(Response.Success())
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun replaceImage(imageUrl: String, newImageStream: InputStream) {
        val existingImageRef = firebaseStorage.reference.child(imageUrl)
        val refName = existingImageRef.name
        existingImageRef.delete().addOnSuccessListener {
            firebaseStorage.reference.child(refName).putStream(newImageStream)
                .addOnSuccessListener {
                    dbResponseLiveData.postValue(Response.Success())
                }
                .addOnFailureListener {
                    dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

    }

//    fun checkDailyAddAmountLimit(user: FirebaseUser) {
//        val date = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(Date())
//        var temp = 0
//        firebaseDB.collection("Add Money Records").document(user.uid).collection(date).get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    temp += Integer.parseInt(document.getString("Amount").toString())
//                }
//                val limit = 10000.00 - temp
//                limitLivedata.postValue(limit)
//            }
//    }

    private fun addAddMoneyRecords(amount: String, note: String, tId: String, uid: String) {
        val time = System.currentTimeMillis().toString()

        firebaseDB.collection("users").document(uid).get().addOnSuccessListener {
            val data2 = mapOf(
                "amount" to amount,
                "tid" to tId,
                "time" to time,
                "operation" to "Add",
                "operator_id" to uid,
                "operator_name" to it.getString("name"),
                "operator_phone" to it.getString("phone"),
                "note" to note
            )

            firebaseDB.collection("transaction_records").document("transaction_records")
                .collection(uid).document(tId).set(data2)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun addMoney(amount: String, note: String, tId: String, uid: String) {
        val doc = firebaseDB.collection("users").document(uid)
        doc.get().addOnSuccessListener {
            if (it.exists()) {
                val amountDouble = amount.toDouble()
                val balance = it.getString("balance")!!.toDouble()
                val finalBalance = balance + amountDouble
                doc.update("balance", finalBalance.toInt().toString())
                addAddMoneyRecords(amount, note, tId, uid)
                dbResponseLiveData.postValue(Response.Success())
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun getPayerDetails(id: String) {
        val ref = firebaseDB.collection("users")
        ref.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val list = arrayListOf<String>()
                if (document.getString("card_id") == id) {
                    list.add(document.getString("name").toString())
                    list.add(document.getString("phone").toString())
                    list.add(document.getString("image_url").toString())
                    list.add(document.getString("uid").toString())
                    payerDetailsLiveData.postValue(list)
                    break
                } else {
                    payerDetailsLiveData.postValue(list)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun changePIN(uid: String, PIN: String) {
        key.fill(1)
        val hashedPIN = aesCrypt.encrypt(PIN, key)
        val doc = firebaseDB.collection("users").document(uid)
        doc.get().addOnSuccessListener {
            if (it.exists()) {
                dbResponseLiveData.postValue(Response.Success())
                doc.update("pin", hashedPIN)
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun payment(senderUid: String, receiverUid: String, amount: String, note: String) {

        val doc1 = firebaseDB.collection("users").document(senderUid)
        val doc2 = firebaseDB.collection("users").document(receiverUid)

        doc1.get().addOnSuccessListener {
            if (it.exists()) {
                val amountDouble = amount.toDouble()
                val balance = it.getString("balance")!!.toDouble()
                val finalBalance = balance - amountDouble
                doc1.update("balance", finalBalance.toInt().toString())
                dbResponseLiveData.postValue(Response.Success())
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

        doc2.get().addOnSuccessListener {
            if (it.exists()) {
                val amountDouble = amount.toDouble()
                val balance = it.getString("balance")!!.toDouble()
                val finalBalance = balance + amountDouble
                doc2.update("balance", finalBalance.toInt().toString())
                dbResponseLiveData.postValue(Response.Success())
            }
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
            "name" to name,
            "phone" to phone,
            "image_url" to imgUrl,
            "uid" to contactUid
        )
        firebaseDB.collection("contacts").document("contacts").collection(selfUid)
            .document(contactUid).set(data)
    }

    fun fetchContacts(uid: String) {
        firebaseDB.collection("contacts").document("contacts").collection(uid).get()
            .addOnSuccessListener { documents ->
                val list = arrayListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                contactDetailsLiveData.postValue(list)
            }
    }

    fun sendRedeemRequest(uid: String, amount: String) {
//        val time =
//            SimpleDateFormat("MMM dd, yyyy 'at' HH:mm aa", Locale.getDefault()).format(Date())
        val timeInMillis = System.currentTimeMillis().toString()
        val data = mapOf(
            "amount" to amount,
            "request_send_time" to timeInMillis,
            "request_approve_time" to "",
            "status" to "Pending",
            "order_time" to timeInMillis
        )
        firebaseDB.collection("redeem_request").document("redeem_request").collection(uid)
            .document(timeInMillis).set(data)
            .addOnSuccessListener {
                redeem(uid, amount)
                dbResponseLiveData.postValue(Response.Success())
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    private fun redeem(uid: String, amount: String) {
        val doc = firebaseDB.collection("users").document(uid)
        doc.get().addOnSuccessListener {
            if (it.exists()) {
                val updatedAmount = it.getString("balance")!!.toDouble() - amount.toDouble()
                doc.update("balance", updatedAmount.toInt().toString())
            }
        }
    }

    fun fetchRedeemRequest(uid: String) {
        firebaseDB.collection("redeem_request").document("redeem_request").collection(uid)
            .orderBy("order_time", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                val list = arrayListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                redeemRequestDetailsLiveData.postValue(list)
            }
    }

    fun addProduct(
        sellerUid: String,
        sellerName: String,
        sellerImgUrl: String,
        productName: String,
        brandName: String,
        productImage: Uri,
        productPrice: String,
        stocks: String,
        unit: String,
        description: String,
        category: String,
        keywords: String
    ) {
        val id = System.currentTimeMillis().toString()
        val doc =
            firebaseStorage.reference.child("products/PID$id/images/${productImage.lastPathSegment}")
        doc.putFile(productImage).addOnSuccessListener {
            doc.downloadUrl.addOnSuccessListener {
                val data = hashMapOf(
                    "product_name" to productName,
                    "brand_name" to brandName,
                    "product_image_url" to it.toString(),
                    "product_price" to productPrice,
                    "stocks" to stocks,
                    "description" to description,
                    "category" to category,
                    "tags" to keywords,
                    "seller_uid" to sellerUid,
                    "seller_name" to sellerName,
                    "seller_image_url" to sellerImgUrl,
                    "raters" to "0",
                    "ratings" to "0",
                    "product_id" to "PID$id",
                    "unit" to unit
                )
                firebaseDB.collection("products").document("products").collection(category)
                    .document("PID$id").set(data)
                firebaseDB.collection("seller_products").document("seller_products")
                    .collection(sellerUid)
                    .document("PID$id").set(data)
            }
        }
    }

    fun fetchProducts(category: String) {
        firebaseDB.collection("products").document("products").collection(category).get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                dbResponseLiveData.postValue(Response.Success())
                productsLivedata.postValue(list)
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun getProductDetails(category: String, productId: String) {
        firebaseDB.collection("products").document("products").collection(category)
            .document(productId).get()
            .addOnSuccessListener {
                productDetailsLiveData.postValue(it)
                dbResponseLiveData.postValue(Response.Success())
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun addToWishlist(category: String, productId: String, uid: String) {
        firebaseDB.collection("products").document("products").collection(category)
            .document(productId).get().addOnSuccessListener { doc ->

                val data = mapOf(
                    "category" to doc.get("category").toString(),
                    "product_id" to doc.get("product_id").toString(),
                    "product_name" to doc.get("product_name").toString(),
                    "product_image_url" to doc.get("product_image_url").toString(),
                    "brand_name" to doc.get("brand_name").toString(),
                    "seller_name" to doc.get("seller_name").toString(),
                    "seller_image_url" to doc.get("seller_image_url").toString(),
                    "ratings" to doc.get("ratings").toString(),
                    "product_price" to doc.get("product_price").toString(),
                    "tags" to "${doc.get("product_name").toString()}, ${
                        doc.get("brand_name").toString()
                    }, ${doc.get("category").toString()},"
                            + "${doc.get("product_id").toString()}, ${
                        doc.get("seller_name").toString()
                    }"
                )

                firebaseDB.collection("wishlist").document("wishlist").collection(uid)
                    .document(productId)
                    .set(data).addOnSuccessListener {
                        dbResponseLiveData.postValue(Response.Success())
                    }
                    .addOnFailureListener {
                        dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                    }
            }
    }

    fun isInWishList(productId: String, uid: String) {
        firebaseDB.collection("wishlist").document("wishlist").collection(uid).get()
            .addOnSuccessListener {
                val documents = it.toList()
                val foundDocument = binarySearchDocuments(documents, productId)
                if (foundDocument != null) {
                    isInWishlistLivedata.postValue(true)
                } else {
                    isInWishlistLivedata.postValue(false)

                }
            }
    }

    fun removeFromWishlist(productId: String, uid: String) {
        val doc = firebaseDB.collection("wishlist").document("wishlist").collection(uid)
            .document(productId)
        doc.get().addOnSuccessListener {
            doc.delete()
            dbResponseLiveData.postValue(Response.Success())
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(it.message.toString()))
            }
    }

    fun fetchWishlistItems(uid: String) {
        firebaseDB.collection("wishlist").document("wishlist").collection(uid)
            .addSnapshotListener { snapshots, e ->
                if (e == null) {
                    val list = mutableListOf<DocumentSnapshot>()
                    for (snapshot in snapshots!!) {
                        list.add(snapshot)
                    }
                    dbResponseLiveData.postValue(Response.Success())
                    wishlistLivedata.postValue(list)
                } else {
                    dbResponseLiveData.postValue(Response.Failure(getErrorMassage(e)))
                }
            }
    }

    fun addToCart(category: String, productId: String, uid: String) {
        firebaseDB.collection("products").document("products").collection(category)
            .document(productId).get().addOnSuccessListener { doc ->

                val data = mapOf(
                    "category" to doc.get("category").toString(),
                    "product_id" to doc.get("product_id").toString(),
                    "product_name" to doc.get("product_name").toString(),
                    "product_image_url" to doc.get("product_image_url").toString(),
                    "brand_name" to doc.get("brand_name").toString(),
                    "seller_name" to doc.get("seller_name").toString(),
                    "seller_image_url" to doc.get("seller_image_url").toString(),
                    "seller_uid" to doc.get("seller_uid").toString(),
                    "description" to doc.get("description").toString(),
                    "ratings" to doc.get("ratings").toString(),
                    "product_price" to doc.get("product_price").toString(),
                    "quantity" to "1",
                    "selected" to true
                )

                firebaseDB.collection("cart").document("cart").collection(uid).document(productId)
                    .set(data).addOnSuccessListener {
                        firebaseDB.collection("cart").document("cart").collection(uid)
                        dbResponseLiveData.postValue(Response.Success())
                    }
                    .addOnFailureListener {
                        dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                    }
            }
    }

    fun isInCart(productId: String, uid: String) {
        firebaseDB.collection("cart").document("cart").collection(uid).get()
            .addOnSuccessListener {
                val documents = it.toList()
                val foundDocument = binarySearchDocuments(documents, productId)
                if (foundDocument != null) {
                    isInCartLivedata.postValue(true)
                } else {
                    isInCartLivedata.postValue(false)
                }
            }
    }

    fun fetchCartItems(uid: String) {
        firebaseDB.collection("cart").document("cart").collection(uid)
            .addSnapshotListener { snapshots, e ->
                if (snapshots != null) {
                    val list = mutableListOf<DocumentSnapshot>()
                    for (snapshot in snapshots) {
                        list.add(snapshot)
                    }
                    dbResponseLiveData.postValue(Response.Success())
                    cartLivedata.postValue(list)
                } else {
                    dbResponseLiveData.postValue(Response.Failure(getErrorMassage(e!!)))
                }
            }
    }

    fun updateSelectOptionCart(productId: String, uid: String) {
        val doc = firebaseDB.collection("cart").document("cart").collection(uid).document(productId)
        doc.get().addOnSuccessListener {
            if (it.get("selected") == true) {
                doc.update("selected", false)
            } else {
                doc.update("selected", true)
            }
        }
    }

    fun getSelectedCartItems(uid: String) {
        firebaseDB.collection("cart").document("cart").collection(uid)
            .whereEqualTo("selected", true)
            .addSnapshotListener { snapshots, e ->
                if (snapshots != null) {
                    val list = arrayListOf<DocumentSnapshot>()
                    for (snapshot in snapshots) {
                        list.add(snapshot)
                    }
                    dbResponseLiveData.postValue(Response.Success())
                    selectedCartLivedata.postValue(list)
                } else {
                    dbResponseLiveData.postValue(Response.Failure(getErrorMassage(e!!)))
                }
            }
    }

    fun removeFromCart(productId: String, uid: String) {
        val doc = firebaseDB.collection("cart").document("cart").collection(uid)
            .document(productId)
        doc.get().addOnSuccessListener {
            doc.delete()
            dbResponseLiveData.postValue(Response.Success())
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(it.message.toString()))
            }
    }

    fun updateQuantityOfCart(productId: String, uid: String, quantity: String) {
        val doc = firebaseDB.collection("cart").document("cart").collection(uid).document(productId)
        doc.get().addOnSuccessListener {
            doc.update("quantity", quantity)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addOrder(
        userName: String,
        userPhone: String,
        userAddress: String,
        paymentType: String,
        userUID: String,
        brandName: String,
        productName: String,
        productImageUrl: String,
        productId: String,
        productCategory: String,
        payableAmount: String,
        quantity: String,
        sellerUID: String,
        orderId: String,
        time: String
    ) {

        val randomNumber = Random.nextInt(100000, 1000000)
        key.fill(1)
        val encryptedCode = aesCrypt.encrypt(randomNumber.toString(), key)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 10)

        val data = mapOf(
            "order_id" to orderId,
            "buyer_name" to userName,
            "buyer_uid" to userUID,
            "buyer_phone" to userPhone,
            "buyer_address" to userAddress,
            "quantity" to quantity,
            "brand_name" to brandName,
            "product_name" to productName,
            "payable_amount" to payableAmount,
            "product_image_url" to productImageUrl,
            "delivery_date" to calendar.timeInMillis.toString(),
            "seller_uid" to sellerUID,
            "product_id" to productId,
            "order_time" to time,
            "category" to productCategory,
            "status" to "Processing",
            "payment_type" to paymentType,
            "product_rating" to "0",
            "confirmation_code" to encryptedCode
        )

        firebaseDB.collection("my_orders").document("my_orders").collection(userUID)
            .document(orderId).set(data).addOnSuccessListener {
                dbResponseLiveData.postValue(Response.Success())
            }
        firebaseDB.collection("seller_orders").document("seller_orders").collection(sellerUID)
            .document(orderId).set(data).addOnSuccessListener {
                dbResponseLiveData.postValue(Response.Success())
            }
    }

    fun completeOrder(
        sellerUid: String,
        buyerUid: String,
        orderId: String
    ) {
        val doc1 =
            firebaseDB.collection("seller_orders").document("seller_orders").collection(sellerUid)
                .document(orderId)
        val doc2 = firebaseDB.collection("my_orders").document("my_orders").collection(buyerUid)
            .document(orderId)

        val doc3 = firebaseDB.collection("seller_payments").document("seller_payments")
            .collection(sellerUid)
            .document(orderId)

        doc1.get().addOnSuccessListener {
            if (it.exists()) {

                dbResponseLiveData.postValue(Response.Success())
                doc1.update("delivery_data", System.currentTimeMillis())
                doc1.update("status", "Delivered")
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

        doc2.get().addOnSuccessListener {
            if (it.exists()) {
                dbResponseLiveData.postValue(Response.Success())
                doc2.update("status", "Delivered")
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

        doc3.get().addOnSuccessListener {
            if (it.exists()) {
                dbResponseLiveData.postValue(Response.Success())
                doc3.update("order_status", "Delivered")
                doc3.update("time", System.currentTimeMillis().toString())
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadSellerDoc(pan: String, gstin: String, uri: Uri, uid: String) {
        key.fill(1)
        val hashedPAN = aesCrypt.encrypt(pan, key)
        val hashedGSTIN = aesCrypt.encrypt(gstin, key)
        val doc = firebaseDB.collection("users").document(uid)
        doc.get().addOnSuccessListener {
            if (it.exists()) {
                val ref =
                    firebaseStorage.reference.child("seller_document/${uid}/${uri.lastPathSegment}")
                ref.putFile(uri)
                    .addOnSuccessListener {
                        ref.downloadUrl
                            .addOnSuccessListener {
                                doc.update("pan", hashedPAN)
                                doc.update("gstin", hashedGSTIN)
                                doc.update("trade_license", it.toString())
                                doc.update("status", "Checking")
                                dbResponseLiveData.postValue(Response.Success())
                            }
                            .addOnFailureListener {
                                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                            }
                    }
                    .addOnFailureListener {
                        dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                    }
            }
        }
    }

    fun fetchSellerProducts(sellerUid: String) {
        firebaseDB.collection("seller_products").document("seller_products").collection(sellerUid)
            .addSnapshotListener { snapshots, e ->
                if (e == null) {
                    val list = mutableListOf<DocumentSnapshot>()
                    for (snapshot in snapshots!!) {
                        list.add(snapshot)
                    }
                    dbResponseLiveData.postValue(Response.Success())
                    sellerProductsLivedata.postValue(list)
                } else {
                    dbResponseLiveData.postValue(Response.Failure(getErrorMassage(e)))
                }
            }
    }

    fun fetchReceivedOrders(sellerUid: String) {
        firebaseDB.collection("seller_orders").document("seller_orders").collection(sellerUid)
            .orderBy(
                "order_time", Query.Direction.DESCENDING
            ).addSnapshotListener { snapshots, e ->
                if (e == null) {
                    val list = mutableListOf<DocumentSnapshot>()
                    for (snapshot in snapshots!!) {
                        list.add(snapshot)
                    }
                    dbResponseLiveData.postValue(Response.Success())
                    sellerReceivedOrdersLivedata.postValue(list)
                } else {
                    dbResponseLiveData.postValue(Response.Failure(getErrorMassage(e)))
                }
            }
    }

    fun fetchMyOrders(buyerUid: String) {
        firebaseDB.collection("my_orders").document("my_orders").collection(buyerUid)
            .orderBy(
                "order_time", Query.Direction.DESCENDING
            ).addSnapshotListener { snapshots, e ->
                if (e == null) {
                    val list = mutableListOf<DocumentSnapshot>()
                    for (snapshot in snapshots!!) {
                        list.add(snapshot)
                    }
                    dbResponseLiveData.postValue(Response.Success())
                    myOrdersLivedata.postValue(list)
                } else {
                    dbResponseLiveData.postValue(Response.Failure(getErrorMassage(e)))
                }
            }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun acceptOrders(sellerUid: String, buyerUid: String, orderId: String, date: String) {
        val doc2 =
            firebaseDB.collection("seller_orders").document("seller_orders")
                .collection(sellerUid)
                .document(orderId)
        val doc3 = firebaseDB.collection("my_orders").document("my_orders").collection(buyerUid)
            .document(orderId)

        val randomNumber = Random.nextInt(100000, 1000000)
        val encryptedCode = AESCrypt().encrypt(randomNumber.toString(), key)

        doc2.get().addOnSuccessListener {
            if (it.exists()) {
                dbResponseLiveData.postValue(Response.Success())
                doc2.update("delivery_date", date)
                doc2.update("status", "Accepted")
                doc2.update("confirmation_code", encryptedCode)
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

        doc3.get().addOnSuccessListener {
            if (it.exists()) {
                dbResponseLiveData.postValue(Response.Success())
                doc3.update("delivery_date", date)
                doc3.update("status", "Accepted")
                doc3.update("confirmation_code", encryptedCode)
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun rejectOrders(sellerUid: String, buyerUid: String, orderId: String) {
        val doc2 =
            firebaseDB.collection("seller_orders").document("seller_orders")
                .collection(sellerUid)
                .document(orderId)
        val doc3 = firebaseDB.collection("my_orders").document("my_orders").collection(buyerUid)
            .document(orderId)

        doc2.get().addOnSuccessListener {
            if (it.exists()) {
                dbResponseLiveData.postValue(Response.Success())
                doc2.delete()
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

        doc3.get().addOnSuccessListener {
            if (it.exists()) {
                dbResponseLiveData.postValue(Response.Success())
                doc3.update("status", "Rejected")
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun saveAddress(
        lat: String,
        long: String,
        locality: String,
        city: String,
        postalNo: String,
        state: String,
        street: String,
        uid: String
    ) {
        val data = mapOf(
            "latitude" to lat,
            "longitude" to long,
            "locality" to locality,
            "city" to city,
            "postal_code" to postalNo,
            "state" to state,
            "street" to street
        )

        firebaseDB.collection("addresses").document("addresses").collection(uid)
            .document("address")
            .set(data).addOnSuccessListener {
                dbResponseLiveData.postValue(Response.Success())
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun getAddress(uid: String) {
        firebaseDB.collection("addresses").document("addresses").collection(uid)
            .document("address")
            .get().addOnSuccessListener {
                if (it.exists()) {
                    addressLivedata.postValue(it)
                    dbResponseLiveData.postValue(Response.Success())
                } else dbResponseLiveData.postValue(Response.Failure("No address found"))
            }
    }

    fun payToAdmin(amount: String, senderUid: String) {
        val adminId = "Jufm91ImZUat1ZUrFpA8CY1HMlw1"

        val doc1 = firebaseDB.collection("users").document(senderUid)
        val doc2 = firebaseDB.collection("users").document(adminId)

        doc1.get().addOnSuccessListener {
            if (it.exists()) {
                val amountDouble = amount.toDouble()
                val balance = it.getString("balance")!!.toDouble()
                val finalBalance = balance - amountDouble
                doc1.update("balance", finalBalance.toInt().toString())
                dbResponseLiveData.postValue(Response.Success())
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

        doc2.get().addOnSuccessListener {
            if (it.exists()) {
                val amountDouble = amount.toDouble()
                val balance = it.getString("balance")!!.toDouble()
                val finalBalance = balance + amountDouble
                doc2.update("balance", finalBalance.toInt().toString())
                dbResponseLiveData.postValue(Response.Success())
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    private fun getErrorMassage(e: Exception): String {
        val colonIndex = e.toString().indexOf(":")
        return e.toString().substring(colonIndex + 2)
    }

    @SuppressLint("SuspiciousIndentation")
    fun addReview(
        buyerUID: String,
        sellerUID: String,
        productId: String,
        orderId: String,
        rating: Float,
        feedback: String
    ) {
        val timeInMillis = System.currentTimeMillis().toString()
        val data = mapOf(
            "user_uid" to buyerUID,
            "product_id" to productId,
            "rating" to rating.toString(),
            "feedback" to feedback,
            "time" to timeInMillis,
        )

        firebaseDB.collection("feedbacks").document(productId).collection(productId)
            .document(timeInMillis).set(data)
            .addOnSuccessListener {
                var category = ""
                val doc1 = firebaseDB.collection("seller_products").document("seller_products")
                    .collection(sellerUID).document(productId)

                doc1.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        category = document.getString("category").toString()
                        val raters = document.getString("raters")!!.toInt()
                        val avgRating = document.getString("ratings")!!.toDouble()
                        val newRaters = raters + 1;
                        val newRating = ((raters * avgRating) + rating) / newRaters
                        doc1.update("raters", newRaters.toString())
                        doc1.update("ratings", newRating.toString().substring(0, 3))
                        val doc2 = firebaseDB.collection("products").document("products")
                            .collection(category).document(productId)
                        doc2.get().addOnSuccessListener {
                            if (it.exists()) {
                                doc2.update("raters", newRaters.toString())
                                doc2.update("ratings", newRating.toString().substring(0, 3))
                            }
                        }

                    }
                }
                dbResponseLiveData.postValue(Response.Success())
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

        val doc3 = firebaseDB.collection("my_orders").document("my_orders").collection(buyerUID)
            .document(orderId)
        doc3.get().addOnSuccessListener { document ->
            if (document.exists()) {
                doc3.update("product_rating", rating.toString())
            }
        }

        val doc4 =
            firebaseDB.collection("seller_orders").document("seller_orders")
                .collection(sellerUID)
                .document(orderId)
        doc4.get().addOnSuccessListener { document ->
            if (document.exists()) {
                doc4.update("product_rating", rating.toString())
            }
        }
    }

//    fun getVideoUrls() {
//        firebaseDB.collection("product_tutorials").get()
//            .addOnSuccessListener { documents ->
//                val list = arrayListOf<DocumentSnapshot>()
//                for (document in documents) {
//                    list.add(document)
//                }
//                feedVideosLiveData.postValue(list)
//                dbResponseLiveData.postValue(Response.Success())
//            }
//            .addOnFailureListener {
//                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
//            }
//    }

    @SuppressLint("SuspiciousIndentation")
    fun isConversationPresent(uid1: String, uid2: String) {
        firebaseDB.collection("conversations").document(uid1).collection("people")
            .document(uid2)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    isConversationLiveData.postValue(true)
                } else {
                    firebaseDB.collection("conversations").document(uid2).collection("people")
                        .document(uid1)
                        .get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                isConversationLiveData.postValue(true)
                            } else {
                                isConversationLiveData.postValue(false)
                            }
                        }
                        .addOnFailureListener {
                            isConversationLiveData.postValue(false)
                        }
                }
            }
            .addOnFailureListener {
                isConversationLiveData.postValue(false)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createConversation(uid1: String, uid2: String, msg: String, time: String, cId: String) {

        firebaseDB.collection("users").document(uid2).get().addOnSuccessListener { doc1 ->
            val data = mapOf(
                "name" to doc1.getString("name"),
                "image_url" to doc1.getString("image_url"),
                "uid" to doc1.getString("uid"),
                "last_message" to ""
            )
            firebaseDB.collection("conversations").document(uid1).collection("people")
                .document(uid2).set(data)
        }

        firebaseDB.collection("users").document(uid1).get().addOnSuccessListener { doc2 ->
            val data = mapOf(
                "name" to doc2.getString("name"),
                "image_url" to doc2.getString("image_url"),
                "uid" to doc2.getString("uid"),
                "last_message" to ""
            )
            firebaseDB.collection("conversations").document(uid2).collection("people")
                .document(uid1).set(data)
        }

        insertMessage(uid1, uid2, msg, time, cId)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertMessage(uid1: String, uid2: String, msg: String, time: String, cId: String) {

        key.fill(1)
        val encryptMessage = aesCrypt.encrypt(msg, key)

        val data = mapOf(
            "message" to encryptMessage,
            "time" to time,
            "sender_uid" to uid1,
            "receiver_uid" to uid2,
            "status" to "Send"
        )

        firebaseDB.collection("messages").document("messages").collection(cId).document(time)
            .set(data)
            .addOnSuccessListener {
                dbResponseLiveData.postValue(Response.Success())

                val doc1 =
                    firebaseDB.collection("conversations").document(uid1).collection("people")
                        .document(uid2)
                doc1.get().addOnSuccessListener {
                    doc1.update("last_message", encryptMessage)
                }

                val doc2 =
                    firebaseDB.collection("conversations").document(uid2).collection("people")
                        .document(uid1)
                doc2.get().addOnSuccessListener {
                    doc2.update("last_message", encryptMessage)
                }

            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun fetchMessages(cId: String) {
        firebaseDB.collection("messages").document("messages").collection(cId)
            .addSnapshotListener { snapshots, e ->
                if(e == null) {
                    val list = arrayListOf<DocumentSnapshot>()
                    for (snapshot in snapshots!!) {
                        list.add(snapshot)
                    }
                    chatsLiveData.postValue(list)
                    dbResponseLiveData.postValue(Response.Success())
                } else {
                    dbResponseLiveData.postValue(Response.Failure(getErrorMassage(e)))
                }

            }
    }

    fun readMessage(cId: String, msgId: String) {
        val doc =
            firebaseDB.collection("messages").document("messages").collection(cId)
                .document(msgId)
        doc.get().addOnSuccessListener {
            doc.update("status", "Read")
        }
    }

    fun getConversations(uid: String) {

        firebaseDB.collection("conversations").document(uid).collection("people").get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                conversationLiveData.postValue(list)
                dbResponseLiveData.postValue(Response.Success())
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun uploadVideoTutorial(video: Uri, description: String, productId: String) {
        val time = System.currentTimeMillis().toString()

        val ref =
            firebaseStorage.reference.child("products/$productId/videos/VID$time")
        ref.putFile(video)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { uri ->
                        val data = mapOf(
                            "video_url" to uri.toString(),
                            "description" to description,
                            "product_id" to productId,
                            "video_id" to "VID$time"
                        )

                        firebaseDB.collection("video_tutorials").document("video_tutorials")
                            .collection(productId).document("VID$time").set(data)
                            .addOnSuccessListener {
                                dbResponseLiveData.postValue(Response.Success())
                            }
                            .addOnFailureListener {
                                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                            }
                    }
                    .addOnFailureListener {
                        dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                    }
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun getVideoTutorials(productId: String) {
        firebaseDB.collection("video_tutorials").document("video_tutorials")
            .collection(productId)
            .get().addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                videoTutorialsLivedata.postValue(list)
            }
    }

    fun updateProductDetails(
        sellerUid: String,
        productId: String,
        category: String,
        productName: String,
        brandName: String,
        productImage: Uri?,
        productPrice: String,
        stocks: String,
        description: String,
    ) {
        val doc1 = firebaseDB.collection("products").document("products").collection(category)
            .document(productId)
        val doc2 = firebaseDB.collection("seller_products").document("seller_products")
            .collection(sellerUid)
            .document(productId)

        if (productImage != null) {
            val ref =
                firebaseStorage.reference.child("products/$sellerUid/images/${productImage.lastPathSegment}")
            ref.putFile(productImage)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        doc1.get().addOnSuccessListener {
                            if (it.exists()) {
                                doc1.update("product_name", productName)
                                doc1.update("brand_name", brandName)
                                doc1.update("product_price", productPrice)
                                doc1.update("stocks", stocks)
                                doc1.update("description", description)
                                doc1.update("product_image_url", uri.toString())
                                dbResponseLiveData.postValue(Response.Success())
                            } else {
                                dbResponseLiveData.postValue(Response.Failure("Product is not exist anymore"))
                            }
                        }
                            .addOnFailureListener {
                                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                            }

                        doc2.get().addOnSuccessListener {
                            if (it.exists()) {
                                doc2.update("product_name", productName)
                                doc2.update("brand_name", brandName)
                                doc2.update("product_price", productPrice)
                                doc2.update("stocks", stocks)
                                doc2.update("description", description)
                                doc2.update("product_image_url", uri.toString())
                                dbResponseLiveData.postValue(Response.Success())
                            } else {
                                dbResponseLiveData.postValue(Response.Failure("Product is not exist anymore"))
                            }
                        }
                            .addOnFailureListener {
                                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                            }
                    }
                        .addOnFailureListener {
                            dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                        }
                }
                .addOnFailureListener {
                    dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                }
        } else {
            doc1.get().addOnSuccessListener {
                if (it.exists()) {
                    doc1.update("product_name", productName)
                    doc1.update("brand_name", brandName)
                    doc1.update("product_price", productPrice)
                    doc1.update("stocks", stocks)
                    doc1.update("description", description)
                    dbResponseLiveData.postValue(Response.Success())
                } else {
                    dbResponseLiveData.postValue(Response.Failure("Product is not exist anymore"))
                }
            }
                .addOnFailureListener {
                    dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                }

            doc2.get().addOnSuccessListener {
                if (it.exists()) {
                    doc2.update("product_name", productName)
                    doc2.update("brand_name", brandName)
                    doc2.update("product_price", productPrice)
                    doc2.update("stocks", stocks)
                    doc2.update("description", description)
                    dbResponseLiveData.postValue(Response.Success())
                } else {
                    dbResponseLiveData.postValue(Response.Failure("Product is not exist anymore"))
                }
            }
                .addOnFailureListener {
                    dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                }
        }

    }

    fun addSellerDuePayment(
        sellerUid: String,
        amount: String,
        orderId: String,
        time: String
    ) {

        val data = mapOf(
            "amount" to amount,
            "order_id" to orderId,
            "order_status" to "Processing",
            "payment_status" to "Pending",
            "time" to time
        )

        firebaseDB.collection("seller_payments").document("seller_payments")
            .collection(sellerUid)
            .document(orderId).set(data)

    }

    fun fetchSellerDuePayment(
        sellerUid: String
    ) {

        firebaseDB.collection("seller_payments").document("seller_payments")
            .collection(sellerUid)
            .orderBy("time", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                pendingPaymentsLivedata.postValue(list)
                dbResponseLiveData.postValue(Response.Success())
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

    }

    private fun binarySearchDocuments(
        documents: List<DocumentSnapshot>,
        targetId: String
    ): DocumentSnapshot? {
        var low = 0
        var high = documents.size - 1

        while (low <= high) {
            val mid = (low + high) / 2
            val midItem = documents[mid]

            val comparison = midItem.id.compareTo(targetId)

            when {
                comparison == 0 -> return midItem // Found the document with the target ID
                comparison < 0 -> low = mid + 1 // The target is in the right half
                else -> high = mid - 1 // The target is in the left half
            }
        }

        return null
    }

    private fun binarySearchList(list: List<String>, target: String): Boolean {
        var low = 0
        var high = list.size - 1

        while (low <= high) {
            val mid = (low + high) / 2
            val midValue = list[mid]

            when {
                midValue == target -> return true
                midValue < target -> low = mid + 1
                else -> high = mid - 1
            }
        }

        return false
    }

    private fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        val secureRandom = SecureRandom()
        keyGenerator.init(256, secureRandom)
        return keyGenerator.generateKey()
    }


//    fun addAccount(
//        rfID: String,
//        password: String
//    ) {
//        val salt = BCrypt.gensalt(10, SecureRandom())
//        val hashedPassword = BCrypt.hashpw(password, salt)
//        val data = mapOf(
//            "Name" to "",
//            "Password" to hashedPassword,
//            "Phone" to "8621028791",
//            "Card Id" to "8621028791@digital",
//            "Aadhar" to "",
//            "Uid" to rfID,
//            "QR Code" to "",
//            "PIN" to "1234",
//            "Balance" to "10000",
//            "User" to "Buyer",
//            "Status" to "",
//            "PAN No" to "",
//            "GSTIN" to "",
//            "Trade License" to "",
//            "Image Url" to "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/user2.png?alt=media&token=91a4d9d4-71cc-4d25-919b-eed55ff51842"
//        )
//
//        firebaseDB.collection("Users").document(rfID).set(data)
//    }

}