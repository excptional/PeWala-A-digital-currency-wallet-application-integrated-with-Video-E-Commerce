package com.te.pewala.db

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
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

    private val accDetailsLiveData = MutableLiveData<DocumentSnapshot>()
    val accDetails: LiveData<DocumentSnapshot>
        get() = accDetailsLiveData

    private val productDetailsLiveData = MutableLiveData<DocumentSnapshot>()
    val productDetails: LiveData<DocumentSnapshot>
        get() = productDetailsLiveData

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

    private val wishlistLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val wishlistData: LiveData<MutableList<DocumentSnapshot>>
        get() = wishlistLivedata

    private val isInWishlistLivedata = MutableLiveData<Boolean>()
    val isInWishlistData: LiveData<Boolean>
        get() = isInWishlistLivedata

    private val isInCartLivedata = MutableLiveData<Boolean>()
    val isInCartData: LiveData<Boolean>
        get() = isInCartLivedata

    private val cartLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val cartData: LiveData<MutableList<DocumentSnapshot>>
        get() = cartLivedata

    private val addressLivedata = MutableLiveData<DocumentSnapshot>()
    val addressData: LiveData<DocumentSnapshot>
        get() = addressLivedata

    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    fun fetchAccountDetails(uid: String) {
        firebaseDB.collection("Users").document(uid).get()
            .addOnSuccessListener {
                accDetailsLiveData.postValue(it)
                dbResponseLiveData.postValue(Response.Success())
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
        val time = System.currentTimeMillis().toString()
//        val time =
//            SimpleDateFormat("MMM dd, yyyy 'at' HH:mm aa", Locale.getDefault()).format(Date())
//        val date = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(Date())
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

        firebaseDB.collection("Add Money Records").document("Add Money Records")
            .collection(uid).document(tId).set(data1)

        firebaseDB.collection("Transaction Records").document("Transaction Records")
            .collection(uid).document(tId).set(data2)

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
        senderImgUrl: String,
        receiverName: String,
        receiverPhone: String,
        receiverImgUrl: String,
        time: String
    ) {
        val data1 = mapOf(
            "Amount" to amount,
            "TId" to tId,
            "Time" to time,
            "Operation" to "Debit",
            "Operator Id" to receiverUid,
//            "Operator Name" to receiverName,
//            "Operator Phone" to receiverPhone,
            "Note" to note
        )

        val data2 = mapOf(
            "Amount" to amount,
            "TId" to tId,
            "Time" to time,
            "Operation" to "Credit",
            "Operator Id" to senderUid,
//            "Operator Name" to senderName,
//            "Operator Phone" to senderPhone,
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
                addContacts(senderName, senderPhone, senderImgUrl, receiverUid, senderUid)
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
//        val time =
//            SimpleDateFormat("MMM dd, yyyy 'at' HH:mm aa", Locale.getDefault()).format(Date())
        val timeInMillis = System.currentTimeMillis().toString()
        val data = mapOf(
            "Amount" to amount,
            "Request send" to timeInMillis,
            "Request approve" to "",
            "Status" to "Pending",
            "Order" to timeInMillis
        )
        firebaseDB.collection("Redeem Request").document("Redeem Request").collection(uid)
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
        val doc = firebaseDB.collection("Users").document(uid)
        doc.get().addOnSuccessListener {
            if (it.exists()) {
                val updatedAmount = it.getString("Balance")!!.toDouble() - amount.toDouble()
                doc.update("Balance", updatedAmount.toInt().toString())
            }
        }
    }

    fun fetchRedeemRequest(uid: String) {
        firebaseDB.collection("Redeem Request").document("Redeem Request").collection(uid)
            .orderBy("Order", Query.Direction.DESCENDING).get()
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
        quantity: String,
        unit: String,
        description: String,
        productType: String,
        keywords: String
    ) {
        val id = System.currentTimeMillis().toString()
        val doc =
            firebaseStorage.reference.child("food and accessories/${productImage.lastPathSegment}")
        doc.putFile(productImage).addOnSuccessListener {
            doc.downloadUrl.addOnSuccessListener {
                val data = hashMapOf(
                    "Product Name" to productName,
                    "Brand Name" to brandName,
                    "Product Image" to it.toString(),
                    "Product Price" to productPrice,
                    "Stocks" to quantity,
                    "Description" to description,
                    "Category" to productType,
                    "Tags" to keywords,
                    "Seller UID" to sellerUid,
                    "Seller Name" to sellerName,
                    "Seller Image" to sellerImgUrl,
                    "Raters" to "0",
                    "Ratings" to "0",
                    "Product ID" to id,
                    "Unit" to unit
                )
                firebaseDB.collection("Products").document("Products").collection(productType)
                    .document(id).set(data)
                firebaseDB.collection("Seller Products").document("Seller Products")
                    .collection(sellerUid)
                    .document(id).set(data)
            }
        }
    }

    fun fetchProducts(category: String) {
        firebaseDB.collection("Products").document("Products").collection(category).get()
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
        firebaseDB.collection("Products").document("Products").collection(category)
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
        firebaseDB.collection("Products").document("Products").collection(category)
            .document(productId).get().addOnSuccessListener { doc ->

                val data = mapOf(
                    "Category" to doc.get("Category").toString(),
                    "Product ID" to doc.get("Product ID").toString(),
                    "Product Name" to doc.get("Product Name").toString(),
                    "Product Image" to doc.get("Product Image").toString(),
                    "Brand Name" to doc.get("Brand Name").toString(),
                    "Seller Name" to doc.get("Seller Name").toString(),
                    "Seller Image" to doc.get("Seller Image").toString(),
                    "Ratings" to doc.get("Ratings").toString(),
                    "Product Price" to doc.get("Product Price").toString(),
                    "Tags" to "${doc.get("Product Name").toString()}, ${doc.get("Brand Name").toString()}, ${doc.get("Category").toString()},"
                    + "${doc.get("Product ID").toString()}, ${doc.get("Seller Name").toString()}"
                )

                firebaseDB.collection("Wishlist").document("Wishlist").collection(uid)
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
        firebaseDB.collection("Wishlist").document("Wishlist").collection(uid).get()
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
        val doc = firebaseDB.collection("Wishlist").document("Wishlist").collection(uid)
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
        firebaseDB.collection("Wishlist").document("Wishlist").collection(uid).get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                wishlistLivedata.postValue(list)
            }
    }

    fun addToCart(category: String, productId: String, uid: String) {
        firebaseDB.collection("Products").document("Products").collection(category)
            .document(productId).get().addOnSuccessListener { doc ->

                val data = mapOf(
                    "Category" to doc.get("Category").toString(),
                    "Product ID" to doc.get("Product ID").toString(),
                    "Product Name" to doc.get("Product Name").toString(),
                    "Product Image" to doc.get("Product Image").toString(),
                    "Brand Name" to doc.get("Brand Name").toString(),
                    "Seller Name" to doc.get("Seller Name").toString(),
                    "Seller Image" to doc.get("Seller Image").toString(),
                    "Seller UID" to doc.get("Seller UID").toString(),
                    "Description" to doc.get("Description").toString(),
                    "Quantity" to "1",
                    "Ratings" to doc.get("Ratings").toString(),
                    "Product Price" to doc.get("Product Price").toString()
                )

                firebaseDB.collection("Cart").document("Cart").collection(uid).document(productId)
                    .set(data).addOnSuccessListener {
                        firebaseDB.collection("Cart").document("Cart").collection(uid)
                        dbResponseLiveData.postValue(Response.Success())
                    }
                    .addOnFailureListener {
                        dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
                    }
            }
    }

    fun isInCart(productId: String, uid: String) {
        firebaseDB.collection("Cart").document("Cart").collection(uid).get()
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
        firebaseDB.collection("Cart").document("Cart").collection(uid).get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                cartLivedata.postValue(list)
            }
    }

    fun removeFromCart(productId: String, uid: String) {
        val doc = firebaseDB.collection("Cart").document("Cart").collection(uid)
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
        val doc = firebaseDB.collection("Cart").document("Cart").collection(uid).document(productId)
        doc.get().addOnSuccessListener {
            doc.update("Quantity", quantity)
        }
    }

    fun addOrder(
        userName: String,
        userNumber: String,
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
        sellerUID: String
    ) {
//        val time =
//            SimpleDateFormat("MMM dd, yyyy 'at' HH:mm aa", Locale.getDefault()).format(Date())
        val time = System.currentTimeMillis().toString()

        val data = mapOf(
            "Order ID" to time,
            "Buyer Name" to userName,
            "Buyer UID" to userUID,
            "Buyer Number" to userNumber,
            "Buyer Address" to userAddress,
            "Quantity" to quantity,
            "Brand Name" to brandName,
            "Product Name" to productName,
            "Payable Amount" to payableAmount,
            "Product Image Url" to productImageUrl,
            "Delivery Date" to "NA",
            "Seller UID" to sellerUID,
            "Product ID" to productId,
            "Order Time" to time,
            "Category" to productCategory,
            "Status" to "Pending",
            "Payment Type" to paymentType,
            "Product Rating" to "0"
        )

        firebaseDB.collection("Orders").document("order_$time").set(data).addOnSuccessListener {
            dbResponseLiveData.postValue(Response.Success())
        }
        firebaseDB.collection("My Orders").document("My Orders").collection(userUID)
            .document("order_$time").set(data).addOnSuccessListener {
                dbResponseLiveData.postValue(Response.Success())
            }
        firebaseDB.collection("Seller Orders").document("Seller Orders").collection(sellerUID)
            .document("order_$time").set(data).addOnSuccessListener {
                dbResponseLiveData.postValue(Response.Success())
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

        return null // Document with the target ID not found
    }

    fun uploadSellerDoc(pan: String, gstin: String, uri: Uri, uid: String) {
        val doc = firebaseDB.collection("Users").document(uid)
        doc.get().addOnSuccessListener {
            if (it.exists()) {
                val ref =
                    firebaseStorage.reference.child("images/${uid}/${uri.lastPathSegment}")
                ref.putFile(uri)
                    .addOnSuccessListener {
                        ref.downloadUrl
                            .addOnSuccessListener {
                                doc.update("PAN No", pan)
                                doc.update("GSTIN", gstin)
                                doc.update("Trade License", it.toString())
                                doc.update("Status", "Checking")
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
        firebaseDB.collection("Seller Products").document("Seller Products").collection(sellerUid)
            .get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                dbResponseLiveData.postValue(Response.Success())
                sellerProductsLivedata.postValue(list)
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun fetchReceivedOrders(sellerUid: String) {
        firebaseDB.collection("Seller Orders").document("Seller Orders").collection(sellerUid).get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                dbResponseLiveData.postValue(Response.Success())
                sellerReceivedOrdersLivedata.postValue(list)
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun fetchMyOrders(buyerUid: String) {
        firebaseDB.collection("My Orders").document("My Orders").collection(buyerUid).get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                dbResponseLiveData.postValue(Response.Success())
                myOrdersLivedata.postValue(list)
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun acceptOrders(sellerUid: String, buyerUid: String, orderId: String, date: String) {
        val doc1 = firebaseDB.collection("Orders").document("order_$orderId")
        val doc2 =
            firebaseDB.collection("Seller Orders").document("Seller Orders").collection(sellerUid)
                .document("order_$orderId")
        val doc3 = firebaseDB.collection("My Orders").document("My Orders").collection(buyerUid)
            .document("order_$orderId")

        doc1.get().addOnSuccessListener {
            if (it.exists()) {
                dbResponseLiveData.postValue(Response.Success())
                doc1.update("Delivery Date", date)
                doc1.update("Status", "Accepted")
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

        doc2.get().addOnSuccessListener {
            if (it.exists()) {
                dbResponseLiveData.postValue(Response.Success())
                doc2.update("Delivery Date", date)
                doc2.update("Status", "Accepted")
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

        doc3.get().addOnSuccessListener {
            if (it.exists()) {
                dbResponseLiveData.postValue(Response.Success())
                doc3.update("Delivery Date", date)
                doc3.update("Status", "Accepted")
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun rejectOrders(sellerUid: String, buyerUid: String, orderId: String) {
        val doc1 = firebaseDB.collection("Orders").document("order_$orderId")
        val doc2 =
            firebaseDB.collection("Seller Orders").document("Seller Orders").collection(sellerUid)
                .document("order_$orderId")
        val doc3 = firebaseDB.collection("My Orders").document("My Orders").collection(buyerUid)
            .document("order_$orderId")

        doc1.get().addOnSuccessListener {
            if (it.exists()) {
                dbResponseLiveData.postValue(Response.Success())
                doc1.update("Status", "Rejected")
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

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
                doc3.update("Status", "Rejected")
            }
        }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun saveAddress(
        locality: String,
        city: String,
        postalNo: String,
        state: String,
        landmark: String,
        uid: String
    ) {
        val data = mapOf(
            "Locality" to locality,
            "City" to city,
            "Postal Code" to postalNo,
            "State" to state,
            "Landmark" to landmark
        )

        firebaseDB.collection("Addresses").document("Addresses").collection(uid).document("address")
            .set(data).addOnSuccessListener {
                dbResponseLiveData.postValue(Response.Success())
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun getAddress(uid: String) {
        firebaseDB.collection("Addresses").document("Addresses").collection(uid).document("address")
            .get().addOnSuccessListener {
                if (it.exists()) {
                    addressLivedata.postValue(it)
                    dbResponseLiveData.postValue(Response.Success())
                } else dbResponseLiveData.postValue(Response.Failure("No address found"))
            }
    }

    fun payToAdmin(amount: String, senderUid: String) {
        val adminId = "Jufm91ImZUat1ZUrFpA8CY1HMlw1"

        val doc1 = firebaseDB.collection("Users").document(senderUid)
        val doc2 = firebaseDB.collection("Users").document(adminId)

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

    private fun getErrorMassage(e: Exception): String {
        val colonIndex = e.toString().indexOf(":")
        return e.toString().substring(colonIndex + 2)
    }

//    private fun generateUniqueId(): String {
//        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
//        val random = Random()
//        val idBuilder = StringBuilder()
//
//        repeat(8) {
//            val randomIndex = random.nextInt(characters.length)
//            idBuilder.append(characters[randomIndex])
//        }
//
//        return idBuilder.toString()
//    }

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
            "User UID" to buyerUID,
            "Product ID" to productId,
            "rating" to rating.toString(),
            "Feedback" to feedback,
            "Time" to timeInMillis,
        )

        firebaseDB.collection("Feedbacks").document(productId).collection(productId)
            .document(timeInMillis).set(data)
            .addOnSuccessListener {
                var category = ""
                val doc1 = firebaseDB.collection("Seller Products").document("Seller Products")
                    .collection(sellerUID).document(productId)

                doc1.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        category = document.getString("Category").toString()
                        val raters = document.getString("Raters")!!.toDouble()
                        val avgRating = document.getString("Ratings")!!.toDouble()
                        val newRaters = raters + 1;
                        val newRating = ((raters * avgRating) + rating) / newRaters
                        doc1.update("Raters", newRaters.toString())
                        doc1.update("Ratings", newRating.toString())
                        val doc2 = firebaseDB.collection("Products").document("Products")
                            .collection(category).document(productId)
                        doc2.get().addOnSuccessListener {
                            if (it.exists()) {
                                doc2.update("Raters", newRaters.toString())
                                doc2.update("Ratings", newRating.toString())
                            }
                        }

                    }
                }
                dbResponseLiveData.postValue(Response.Success())
            }
            .addOnFailureListener {
                dbResponseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }

        val doc3 = firebaseDB.collection("My Orders").document("My Orders").collection(buyerUID)
            .document("order_$orderId")
        doc3.get().addOnSuccessListener { document ->
            if (document.exists()) {
                doc3.update("Product Rating", rating.toString())
            }
        }

        val doc4 = firebaseDB.collection("Orders").document("order_$orderId")
        doc4.get().addOnSuccessListener { document ->
            if (document.exists()) {
                doc4.update("Product Rating", rating.toString())
            }
        }

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