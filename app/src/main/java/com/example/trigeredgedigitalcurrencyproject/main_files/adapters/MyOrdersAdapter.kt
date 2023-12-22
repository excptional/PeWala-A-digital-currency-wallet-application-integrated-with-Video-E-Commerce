package com.example.trigeredgedigitalcurrencyproject.main_files.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.main_files.items.MyOrdersItems
import java.text.SimpleDateFormat
import java.util.TimeZone

class MyOrdersAdapter(
    private val myOrdersItems: ArrayList<MyOrdersItems>
) :
    RecyclerView.Adapter<MyOrdersAdapter.MyOrdersViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_orders_items, parent, false)
        return MyOrdersViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: MyOrdersViewHolder, position: Int) {
        val currentItem = myOrdersItems[position]
        val date = java.util.Date(currentItem.time!!.toLong())
        val timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm aa")
        dateFormat.timeZone = timeZone

        holder.time.text = dateFormat.format(date)
        holder.productName.text = currentItem.productName
        holder.brandName.text = currentItem.brandName
        holder.quantity.text = "Quantity : " + currentItem.quantity
        Glide.with(holder.itemView.context).load(currentItem.productImageUrl)
            .into(holder.productImage)

        when (currentItem.status) {
            "Pending" -> {
                holder.deliveryDate.text = "Status : Pending"
            }
            "Rejected" -> {
                holder.deliveryDate.text = "Your order was cancelled by the seller"
//                holder.deliveryDate.setTextColor(Color.RED)
            }
            "Delivered" -> {
                holder.deliveryDate.text = "Your order delivered on\n" + currentItem.delivery_date
                holder.ratingLayout.visibility = View.VISIBLE
                if(currentItem.rating != "0") {
                    holder.ratingLayout.isClickable = false
                    holder.ratingBar.rating = currentItem.rating!!.toFloat()
                    holder.rateNowText.visibility = View.GONE
                }
            }
            else -> {
                holder.deliveryDate.text = "Your order will deliver on\n" + currentItem.delivery_date
            }
        }

        val bundle = Bundle()
        bundle.putString("productId", currentItem.productId)
        bundle.putString("productName", currentItem.productName)
        bundle.putString("brandName", currentItem.brandName)
        bundle.putString("buyerName", currentItem.buyerName)
        bundle.putString("address", currentItem.address)
        bundle.putString("quantity", currentItem.quantity)
        bundle.putString("orderTime", dateFormat.format(date))
        bundle.putString("productPrice", currentItem.price)
        bundle.putString("orderId", currentItem.orderId)
        bundle.putString("productImg", currentItem.productImageUrl)
        bundle.putString("buyerUid", currentItem.buyerUid)
        bundle.putString("sellerUid", currentItem.sellerUid)

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

        holder.item.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_order_details, bundle, navBuilder.build())
        }

        holder.ratingLayout.setOnClickListener {
            if(holder.rateNowText.isVisible)
            Navigation.findNavController(it).navigate(R.id.nav_feedback, bundle, navBuilder.build())
        }

    }

    override fun getItemCount(): Int {
        return myOrdersItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMyOrders(updateMyOrdersItems: ArrayList<MyOrdersItems>) {
        myOrdersItems.clear()
        myOrdersItems.addAll(updateMyOrdersItems)
        notifyDataSetChanged()
    }

    class MyOrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rateNowText: TextView = itemView.findViewById(R.id.rateNowText_order_buyer)
        val productName: TextView = itemView.findViewById(R.id.productName_order_buyer)
        val brandName: TextView = itemView.findViewById(R.id.brandName_order_buyer)
        val quantity: TextView = itemView.findViewById(R.id.quantity_order_buyer)
        val time: TextView = itemView.findViewById(R.id.time_order_buyer)
        val productImage: ImageView = itemView.findViewById(R.id.productImg_order_buyer)
        val item: CardView = itemView.findViewById(R.id.itemLayout_order_buyer)
        val deliveryDate: TextView = itemView.findViewById(R.id.delivery_date_order_buyer)
        val ratingLayout: LinearLayout = itemView.findViewById(R.id.rating_layout_order_buyer)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingbar_order_buyer)
    }
}