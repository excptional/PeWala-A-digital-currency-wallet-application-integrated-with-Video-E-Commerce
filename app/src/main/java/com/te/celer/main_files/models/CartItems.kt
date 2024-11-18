package com.te.celer.main_files.models

import android.os.Parcel
import android.os.Parcelable

//data class CartItems(
//    val productName: String? = null,
//    val brandName: String? = null,
//    val productImageUrl: String? = null,
//    val category: String? = null,
//    val price: String? = null,
//    val quantity: String? = null,
//    val description: String? = null,
//    val sellerName: String? = null,
//    val sellerImageUrl: String? = null,
//    val sellerUID: String? = null,
//    val buyerUID: String? = null,
//    val ratings: String? = null,
//    val productId: String? = null,
//    var isChecked: Boolean = false // boolean should be non-null
//) : Parcelable {
//    @SuppressLint("NewApi")
//    constructor(parcel: Parcel) : this(
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readBoolean(),
//    )
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    override fun writeToParcel(dest: Parcel, flags: Int) {
//        dest.writeString(productName)
//        dest.writeString(brandName)
//        dest.writeString(productImageUrl)
//        dest.writeString(category)
//        dest.writeString(price)
//        dest.writeString(quantity)
//        dest.writeString(description)
//        dest.writeString(sellerName)
//        dest.writeString(sellerImageUrl)
//        dest.writeString(sellerUID)
//        dest.writeString(buyerUID)
//        dest.writeString(ratings)
//        dest.writeString(productId)
//        dest.writeValue(isChecked)
//    }
//
//    companion object CREATOR : Parcelable.Creator<CartItems> {
//        override fun createFromParcel(parcel: Parcel): CartItems {
//            return CartItems(parcel)
//        }
//
//        override fun newArray(size: Int): Array<CartItems?> {
//            return arrayOfNulls(size)
//        }
//    }
//}

data class CartItems(
    val productName: String?,
    val brandName: String?,
    val productImageUrl: String?,
    val category: String?,
    val price: String?,
    val quantity: String?,
    val description: String?,
    val sellerName: String?,
    val sellerImageUrl: String?,
    val sellerUID: String?,
    val buyerUID: String?,
    val ratings: String?,
    val productId: String?,
    val isChecked: Boolean
) : Parcelable {

    // Constructor for unparcelling
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte() // readBoolean not available before API 29
    )

    // Method to write each property to the Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productName)
        parcel.writeString(brandName)
        parcel.writeString(productImageUrl)
        parcel.writeString(category)
        parcel.writeString(price)
        parcel.writeString(quantity)
        parcel.writeString(description)
        parcel.writeString(sellerName)
        parcel.writeString(sellerImageUrl)
        parcel.writeString(sellerUID)
        parcel.writeString(buyerUID)
        parcel.writeString(ratings)
        parcel.writeString(productId)
        parcel.writeByte(if (isChecked) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CartItems> {
        override fun createFromParcel(parcel: Parcel): CartItems = CartItems(parcel)
        override fun newArray(size: Int): Array<CartItems?> = arrayOfNulls(size)
    }
}


