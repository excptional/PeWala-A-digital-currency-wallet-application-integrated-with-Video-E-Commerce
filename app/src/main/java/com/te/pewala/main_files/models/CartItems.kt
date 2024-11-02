package com.te.pewala.main_files.models

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class CartItems(
    val productName: String? = null,
    val brandName: String? = null,
    val productImageUrl: String? = null,
    val category: String? = null,
    val price: String? = null,
    val quantity: String? = null,
    val description: String? = null,
    val sellerName: String? = null,
    val sellerImageUrl: String? = null,
    val sellerUID: String? = null,
    val buyerUID: String? = null,
    val ratings: String? = null,
    val productId: String? = null,
    var isChecked: Boolean = false // boolean should be non-null
) : Parcelable {
    @SuppressLint("NewApi")
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
        parcel.readBoolean(),
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(productName)
        dest.writeString(brandName)
        dest.writeString(productImageUrl)
        dest.writeString(category)
        dest.writeString(price)
        dest.writeString(quantity)
        dest.writeString(description)
        dest.writeString(sellerName)
        dest.writeString(sellerImageUrl)
        dest.writeString(sellerUID)
        dest.writeString(buyerUID)
        dest.writeString(ratings)
        dest.writeString(productId)
        dest.writeValue(isChecked)
    }

    companion object CREATOR : Parcelable.Creator<CartItems> {
        override fun createFromParcel(parcel: Parcel): CartItems {
            return CartItems(parcel)
        }

        override fun newArray(size: Int): Array<CartItems?> {
            return arrayOfNulls(size)
        }
    }
}

