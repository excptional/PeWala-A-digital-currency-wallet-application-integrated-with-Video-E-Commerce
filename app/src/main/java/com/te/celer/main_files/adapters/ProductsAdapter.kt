package com.te.celer.main_files.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.te.celer.R
import com.te.celer.main_files.models.ProductsItems

class ProductsAdapter(
    private val productItems: ArrayList<ProductsItems>
) :
    RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = productItems[position]
        holder.productName.text = currentItem.productName
        holder.brandName.text = currentItem.brandName
        Glide.with(holder.itemView.context).load(currentItem.productImageUrl)
            .into(holder.productImage)
        holder.productPrice.text = currentItem.price + " INR"
        holder.productRatingBar.rating = currentItem.ratings!!.toFloat()

        val bundle = Bundle()
        bundle.putString("productId", currentItem.productId)
        bundle.putString("category", currentItem.category)
        bundle.putString("seller_uid", currentItem.sellerUID)


        holder.cardLayout.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_order_place, bundle)
        }

    }

    override fun getItemCount(): Int {
        return productItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateProducts(updateProductsItems: ArrayList<ProductsItems>) {
        productItems.clear()
        productItems.addAll(updateProductsItems)
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name_product)
        val brandName: TextView = itemView.findViewById(R.id.brand_name_product)
        val productImage: AppCompatImageView = itemView.findViewById(R.id.product_image_product)
        val productPrice: TextView = itemView.findViewById(R.id.product_price_product)
        val productRatingBar: RatingBar = itemView.findViewById(R.id.ratingbar_product)
        val cardLayout: CardView = itemView.findViewById(R.id.card_layout_product)
    }
}