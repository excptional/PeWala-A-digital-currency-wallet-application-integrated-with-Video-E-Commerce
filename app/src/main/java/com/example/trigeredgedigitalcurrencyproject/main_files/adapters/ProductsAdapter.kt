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
import com.example.trigeredgedigitalcurrencyproject.main_files.items.ProductsItems
import de.hdodenhof.circleimageview.CircleImageView

class ProductsAdapter(
    private val ProductItems: ArrayList<ProductsItems>
) :
    RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_items, parent, false)
        return ProductViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = ProductItems[position]
        holder.productName.text = currentItem.productName
        holder.brandName.text = currentItem.brandName
        Glide.with(holder.itemView.context).load(currentItem.productImageUrl)
            .into(holder.productImage)
        holder.productPrice.text = "₹" + currentItem.price
        holder.productRatingText.text = currentItem.ratings
        holder.productRatingBar.rating = currentItem.ratings!!.toFloat()
        holder.sellerName.text = currentItem.sellerName
        Glide.with(holder.itemView.context).load(currentItem.sellerImageUrl).into(holder.sellerImg)

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

        holder.itemLayout.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_order_place, bundle)
        }

//        val bundle = Bundle()
//        bundle.putString("productName", currentItem.productName)
//        bundle.putString("productImage", currentItem.productImageUrl)
//        bundle.putString("productPrice", currentItem.productPrice)
//        bundle.putString("productRating", currentItem.productRating)
//        bundle.putString("sellerId", currentItem.sellerID)
//
//        holder.itemLayout.setOnClickListener {
////            Navigation.findNavController(it).navigate(R.id.nav_order_place, bundle)
//        }
    }

    override fun getItemCount(): Int {
        return ProductItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateProducts(updateProductsItems: ArrayList<ProductsItems>) {
        ProductItems.clear()
        ProductItems.addAll(updateProductsItems)
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName_product)
        val brandName: TextView = itemView.findViewById(R.id.brandName_product)
        val sellerName: TextView = itemView.findViewById(R.id.sellerName_product)
        val sellerImg: CircleImageView = itemView.findViewById(R.id.sellerImage_product)
        val productImage: ImageView = itemView.findViewById(R.id.productImage_product)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice_product)
        val productRatingBar: RatingBar = itemView.findViewById(R.id.ratingBar_product)
        val productRatingText: TextView = itemView.findViewById(R.id.ratingText_product)
        val itemLayout: CardView = itemView.findViewById(R.id.itemLayout_product)
    }
}