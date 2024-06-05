package com.te.pewala.main_files.adapters

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
import com.te.pewala.R
import com.te.pewala.main_files.items.SellerProductsItems

class SellerProductsAdapter(
    private val sellerProductItems: ArrayList<SellerProductsItems>
) :
    RecyclerView.Adapter<SellerProductsAdapter.SellerProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
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
        holder.sellerProductRatingBar.rating = currentItem.ratings!!.toFloat()

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
            Navigation.findNavController(it).navigate(R.id.nav_product_dashboard, bundle)
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
        val sellerProductName: TextView = itemView.findViewById(R.id.product_name_product)
        val brandName: TextView = itemView.findViewById(R.id.brand_name_product)
        val sellerProductImage: ImageView = itemView.findViewById(R.id.product_image_product)
        val sellerProductPrice: TextView = itemView.findViewById(R.id.product_price_product)
        val sellerProductRatingBar: RatingBar = itemView.findViewById(R.id.ratingbar_product)
        val itemLayout: CardView = itemView.findViewById(R.id.card_layout_product)
    }
}