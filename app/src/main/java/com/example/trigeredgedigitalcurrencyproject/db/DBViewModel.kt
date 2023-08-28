package com.example.trigeredgedigitalcurrencyproject.db

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot

class DBViewModel(application: Application) : AndroidViewModel(application) {

    private val dbRepository: DBRepository = DBRepository(application)
    val dbResponse: LiveData<Response<String>>
        get() = dbRepository.dbResponse
    val accDetails: LiveData<DocumentSnapshot>
        get() = dbRepository.accDetails
    val productDetails: LiveData<DocumentSnapshot>
        get() = dbRepository.productDetails
    val contactDetails: LiveData<ArrayList<DocumentSnapshot>>
        get() = dbRepository.contactDetails
    val redeemRequestDetails: LiveData<ArrayList<DocumentSnapshot>>
        get() = dbRepository.redeemRequestDetails
    val transactionDetails: LiveData<ArrayList<DocumentSnapshot>>
        get() = dbRepository.transactionDetails
    val payerDetails: LiveData<ArrayList<String>>
        get() = dbRepository.payerDetails
    val dailyAddLimit: LiveData<Double>
        get() = dbRepository.limitData
    val productsData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.productsData

    val sellerProductsData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.sellerProductsData

    val sellerReceivedOrdersData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.sellerReceivedOrdersData

    val myOrdersData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.myOrdersData

    val wishlistData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.wishlistData

    val isInWishlistData: LiveData<Boolean>
        get() = dbRepository.isInWishlistData

    val cartData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.cartData

    val isInCartData: LiveData<Boolean>
        get() = dbRepository.isInCartData

    val addressData: LiveData<DocumentSnapshot>
        get() = dbRepository.addressData

    fun fetchAccountDetails(uid: String) {
        dbRepository.fetchAccountDetails(uid)
    }

    fun fetchTransactionDetails(uid: String) {
        dbRepository.fetchTransactionDetails(uid)
    }

    fun uploadImageToStorage(imageUri: Uri, user: FirebaseUser) {
        dbRepository.uploadImageToStorage(imageUri, user)
    }

    fun checkDailyAddAmountLimit(user: FirebaseUser) {
        dbRepository.checkDailyAddAmountLimit(user)
    }

    fun addMoney(amount: String, note: String, tId: String, uid: String) {
        dbRepository.addMoney(amount, note, tId, uid)
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
        receiverImg: String,
        time: String
    ) {
        dbRepository.addTransaction(
            amount,
            note,
            tId,
            senderUid,
            receiverUid,
            senderName,
            senderPhone,
            receiverName,
            receiverPhone,
            receiverImg,
            time
        )
    }

    fun fetchContacts(uid: String) {
        dbRepository.fetchContacts(uid)
    }

    fun sendRedeemRequest(uid: String, amount: String) {
        dbRepository.sendRedeemRequest(uid, amount)
    }

    fun fetchRedeemRequest(uid: String) {
        dbRepository.fetchRedeemRequest(uid)
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
        dbRepository.addProduct(
            sellerUid,
            sellerName,
            sellerImgUrl,
            productName,
            brandName,
            productImage,
            productPrice,
            quantity,
            unit,
            description,
            productType,
            keywords
        )
    }

    fun fetchProducts(category: String) {
        dbRepository.fetchProducts(category)
    }

    fun addOrder(
        userName: String,
        userNumber: String,
        userAddress: String,
        orderType: String,
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
        dbRepository.addOrder(
            userName,
            userNumber,
            userAddress,
            orderType,
            userUID,
            brandName,
            productName,
            productImageUrl,
            productId,
            productCategory,
            payableAmount,
            quantity,
            sellerUID
        )
    }

    fun uploadSellerDoc(pan: String, gstin: String, uri: Uri, uid: String) {
        dbRepository.uploadSellerDoc(pan, gstin, uri, uid)
    }

    fun fetchSellerProducts(sellerUid: String) {
        dbRepository.fetchSellerProducts(sellerUid)
    }

    fun fetchReceivedOrders(sellerUid: String) {
        dbRepository.fetchReceivedOrders(sellerUid)
    }

    fun acceptOrders(sellerUid: String, buyerUid: String, orderId: String, date: String) {
        dbRepository.acceptOrders(sellerUid, buyerUid, orderId, date)
    }

    fun rejectOrders(sellerUid: String, buyerUid: String, orderId: String) {
        dbRepository.rejectOrders(sellerUid, buyerUid, orderId)
    }

    fun fetchMyOrders(buyerUid: String) {
        dbRepository.fetchMyOrders(buyerUid)
    }

    fun addToWishlist(category: String, productId: String, uid: String) {
        dbRepository.addToWishlist(category, productId, uid)
    }

    fun removeFromWishlist(productId: String, uid: String) {
        dbRepository.removeFromWishlist(productId, uid)
    }

    fun isInWishList(productId: String, uid: String) {
        dbRepository.isInWishList(productId, uid)
    }

    fun fetchWishlistItems(uid: String) {
        dbRepository.fetchWishlistItems(uid)
    }

    fun addToCart(category: String, productId: String, uid: String) {
        dbRepository.addToCart(category, productId, uid)
    }

    fun isInCart(productId: String, uid: String) {
        dbRepository.isInCart(productId, uid)
    }

    fun removeFromCart(productId: String, uid: String) {
        dbRepository.removeFromCart(productId, uid)
    }

    fun fetchCartItems(uid: String) {
        dbRepository.fetchCartItems(uid)
    }

    fun updateQuantityOfCart(productId: String, uid: String, quantity: String) {
        dbRepository.updateQuantityOfCart(productId, uid, quantity)
    }

    fun getProductDetails(category: String, productId: String) {
        dbRepository.getProductDetails(category, productId)
    }

    fun saveAddress(
        locality: String,
        city: String,
        postalNo: String,
        state: String,
        landmark: String,
        uid: String
    ) {
        dbRepository.saveAddress(locality, city, postalNo, state, landmark, uid)
    }

    fun getAddress(uid: String) {
        dbRepository.getAddress(uid)
    }

    fun payToAdmin(amount: String, senderUid: String) {
        dbRepository.payToAdmin(amount, senderUid)
    }

}