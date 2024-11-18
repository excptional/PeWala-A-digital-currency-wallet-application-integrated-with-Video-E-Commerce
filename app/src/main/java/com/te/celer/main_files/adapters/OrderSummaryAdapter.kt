package com.te.celer.main_files.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.te.celer.R
import com.te.celer.main_files.models.CartItems


class OrderSummaryAdapter(
    private val orderSummaryItems: ArrayList<CartItems>,
) :
    RecyclerView.Adapter<OrderSummaryAdapter.OrderSummaryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderSummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderSummaryViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderSummaryViewHolder, position: Int) {
        val currentItem = orderSummaryItems[position]
        holder.productName.text = currentItem.productName
        holder.brandName.text = currentItem.brandName
        holder.price.text = "₹${Integer.parseInt(currentItem.price!!) * Integer.parseInt(currentItem.quantity!!)}"
        holder.quantity.text = "Quantity : ${currentItem.quantity}"
        Glide.with(holder.itemView.context).load(currentItem.productImageUrl)
            .into(holder.productImage)

    }

    override fun getItemCount(): Int {
        return orderSummaryItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateOrderSummary(updateOrderSummaryItems: ArrayList<CartItems>) {
        orderSummaryItems.clear()
        orderSummaryItems.addAll(updateOrderSummaryItems)
        notifyDataSetChanged()
    }


    class OrderSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName_order_item)
        val brandName: TextView = itemView.findViewById(R.id.brandName_order_item)
        val quantity: TextView = itemView.findViewById(R.id.quantity_order_item)
        val productImage: ImageView = itemView.findViewById(R.id.productImg_order_item)
        val price: TextView = itemView.findViewById(R.id.productPrice_order_item)
    }
}