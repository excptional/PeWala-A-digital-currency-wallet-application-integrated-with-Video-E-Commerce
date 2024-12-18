package com.te.celer.db

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import java.io.InputStream

class DBViewModel(application: Application) : AndroidViewModel(application) {

    private val dbRepository: DBRepository = DBRepository(application)
    val dbResponse: LiveData<Response<String>>
        get() = dbRepository.dbResponse

    val accDetails: LiveData<DocumentSnapshot>
        get() = dbRepository.accDetails

    val productDetails: MutableLiveData<DocumentSnapshot?>
        get() = dbRepository.productDetails

    val contactDetails: MutableLiveData<ArrayList<DocumentSnapshot>?>
        get() = dbRepository.contactDetails

    val feedVideos: LiveData<ArrayList<DocumentSnapshot>>
        get() = dbRepository.feedVideos

    val redeemRequestDetails: MutableLiveData<ArrayList<DocumentSnapshot>?>
        get() = dbRepository.redeemRequestDetails

    val transactionDetails: LiveData<ArrayList<DocumentSnapshot>>
        get() = dbRepository.transactionDetails

    val chats: LiveData<ArrayList<DocumentSnapshot>>
        get() = dbRepository.chats

    val isConversation: LiveData<Boolean>
        get() = dbRepository.isConversation

    val payerDetails: MutableLiveData<ArrayList<String>?>
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

    val pendingPaymentsData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.pendingPaymentsData

    val wishlistData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.wishlistData

    val isInWishlistData: LiveData<Boolean>
        get() = dbRepository.isInWishlistData

    val cartData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.cartData

    val selectedCartData: LiveData<ArrayList<DocumentSnapshot>>
        get() = dbRepository.selectedCartData

    val isInCartData: LiveData<Boolean>
        get() = dbRepository.isInCartData

    val addressData: MutableLiveData<DocumentSnapshot?>
        get() = dbRepository.addressData

    val conversations: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.conversation

    val videoTutorialsData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.videoTutorialsData

    fun fetchAccountDetails(uid: String) {
        dbRepository.fetchAccountDetails(uid)
    }

    fun fetchTransactionDetails(uid: String) {
        dbRepository.fetchTransactionDetails(uid)
    }

    fun updateTransactorDetails(
        uid: String
    ) {
        dbRepository.updateTransactorDetails(uid)
    }

    fun uploadImageToStorage(imageUri: Uri, uid: String) {
        dbRepository.uploadImageToStorage(imageUri, uid)
    }

    fun replaceImage(imageUrl: String, newImageStream: InputStream) {
        dbRepository.replaceImage(imageUrl, newImageStream)
    }

//    fun checkDailyAddAmountLimit(user: FirebaseUser) {
//        dbRepository.checkDailyAddAmountLimit(user)
//    }

    fun addMoney(amount: String, note: String, tId: String, uid: String) {
        dbRepository.addMoney(amount, note, tId, uid)
    }

    fun getPayerDetails(id: String) {
        dbRepository.getPayerDetails(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        senderImgUrl: String,
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
            senderImgUrl,
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
        stocks: String,
        unit: String,
        description: String,
        category: String,
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
            stocks,
            unit,
            description,
            category,
            keywords
        )
    }

    fun fetchProducts(category: String) {
        dbRepository.fetchProducts(category)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        sellerUID: String,
        orderId: String,
        time: String
    ) {
        dbRepository.addOrder(
            userName,
            userNumber,
            userAddress,
            paymentType,
            userUID,
            brandName,
            productName,
            productImageUrl,
            productId,
            productCategory,
            payableAmount,
            quantity,
            sellerUID,
            orderId,
            time
        )
    }

    fun completeOrder(
        sellerUid: String,
        buyerUid: String,
        orderId: String
    ) {
        dbRepository.completeOrder(sellerUid, buyerUid, orderId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadSellerDoc(pan: String, gstin: String, uri: Uri, uid: String) {
        dbRepository.uploadSellerDoc(pan, gstin, uri, uid)
    }

    fun fetchSellerProducts(sellerUid: String) {
        dbRepository.fetchSellerProducts(sellerUid)
    }

    fun fetchReceivedOrders(sellerUid: String) {
        dbRepository.fetchReceivedOrders(sellerUid)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    fun getSelectedCartItems(uid: String) {
        dbRepository.getSelectedCartItems(uid)
    }

    fun updateSelectOptionCart(productId: String, uid: String) {
        dbRepository.updateSelectOptionCart(productId, uid)
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
        lat: String,
        long: String,
        locality: String,
        city: String,
        postalNo: String,
        state: String,
        street: String,
        uid: String
    ) {
        dbRepository.saveAddress(lat ,long, locality, city, postalNo, state, street, uid)
    }

    fun getAddress(uid: String) {
        dbRepository.getAddress(uid)
    }

    fun payToAdmin(amount: String, senderUid: String) {
        dbRepository.payToAdmin(amount, senderUid)
    }

    fun addReview(
        buyerUID: String,
        sellerUID: String,
        productId: String,
        orderId: String,
        rating: Float,
        feedback: String
    ) {
        dbRepository.addReview(buyerUID, sellerUID, productId, orderId, rating, feedback)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createConversation(uid1: String, uid2: String, msg: String, time: String, cId: String) {
        dbRepository.createConversation(uid1, uid2, msg, time, cId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertMessage(uid1: String, uid2: String, msg: String, time: String, cId: String) {
        dbRepository.insertMessage(uid1, uid2, msg, time, cId)
    }

    fun isConversationPresent(uid1: String, uid2: String) {
        dbRepository.isConversationPresent(uid1, uid2)
    }

    fun fetchMessages(cId: String) {
        dbRepository.fetchMessages(cId)
    }

    fun readMessage(cId: String, msgId: String) {
        dbRepository.readMessage(cId, msgId)
    }

    fun getConversations(uid: String) {
        dbRepository.getConversations(uid)
    }

    fun uploadVideoTutorial(video: Uri, description: String, productId: String) {
        dbRepository.uploadVideoTutorial(video, description, productId)
    }

    fun getVideoTutorials(productId: String) {
        dbRepository.getVideoTutorials(productId)
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
        dbRepository.updateProductDetails(sellerUid, productId, category, productName, brandName, productImage, productPrice, stocks, description)
    }

    fun addSellerDuePayment(
        sellerUid: String,
        amount: String,
        orderId: String,
        time: String
    ) {
        dbRepository.addSellerDuePayment(sellerUid, amount, orderId, time)
    }

    fun fetchSellerDuePayment(
        sellerUid: String
    ) {
        dbRepository.fetchSellerDuePayment(sellerUid)
    }

}