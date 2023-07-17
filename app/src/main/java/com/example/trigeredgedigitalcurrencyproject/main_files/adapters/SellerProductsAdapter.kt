package com.example.trigeredgedigitalcurrencyproject.main_files.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.main_files.items.SellerProductsItems

class SellerProductsAdapter(
    private val sellerProductItems: ArrayList<SellerProductsItems>
) :
    RecyclerView.Adapter<SellerProductsAdapter.SellerProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.seller_products_items, parent, false)
        return SellerProductViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SellerProductViewHolder, position: Int) {
        val currentItem = sellerProductItems[position]
        holder.sellerProductName.text = currentItem.productName
        holder.brandName.text = currentItem.brandName
        Glide.with(holder.itemView.context).load(currentItem.productImageUrl)
            .into(holder.sellerProductImage)
        holder.sellerProductPrice.text = "₹" + currentItem.price
        holder.sellerProductRatingText.text = currentItem.ratings
        holder.sellerProductRatingBar.rating = currentItem.ratings!!.toFloat()
        holder.stocks.text = "Stocks : " + currentItem.quantity

        val bundle = Bundle()
        bundle.putString("brandName", currentItem.brandName)
        bundle.putString("productName", currentItem.productName)
        bundle.putString("productImageUrl", currentItem.productImageUrl)
        bundle.putString("productPrice", currentItem.price)
        bundle.putString("productRating", currentItem.ratings)
        bundle.putString("sellerName", currentItem.sellerName)
        bundle.putString("sellerImageUrl", currentItem.sellerImageUrl)
        bundle.putString("rating", currentItem.ratings)
        bundle.putString("quantity", currentItem.quantity)
        bundle.putString("description", currentItem.description)
        bundle.putString("productId", currentItem.productId)
        bundle.putString("category", currentItem.category)
        bundle.putString("sellerUid", currentItem.sellerUID)

        holder.itemLayout.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {
        return sellerProductItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSellerProducts(updateSellerProductsItems: ArrayList<SellerProductsItems>) {
        sellerProductItems.clear()
        sellerProductItems.addAll(updateSellerProductsItems)
        notifyDataSetChanged()
    }

    class SellerProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sellerProductName: TextView = itemView.findViewById(R.id.productName_seller_product)
        val brandName: TextView = itemView.findViewById(R.id.brandName_seller_product)
        val sellerProductImage: ImageView = itemView.findViewById(R.id.productImage_seller_product)
        val sellerProductPrice: TextView = itemView.findViewById(R.id.productPrice_seller_product)
        val sellerProductRatingBar: RatingBar = itemView.findViewById(R.id.ratingBar_seller_product)
        val sellerProductRatingText: TextView = itemView.findViewById(R.id.ratingText_seller_product)
        val stocks: TextView = itemView.findViewById(R.id.stocks_seller_product)
        val itemLayout: CardView = itemView.findViewById(R.id.itemLayout_seller_product)
    }
}