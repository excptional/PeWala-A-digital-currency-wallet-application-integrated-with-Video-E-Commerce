package com.example.trigeredgedigitalcurrencyproject.main_files.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.main_files.items.SellerReceivedOrdersItems
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

class SellerReceivedOrdersAdapter(
    private val ProductItems: ArrayList<SellerReceivedOrdersItems>
) :
    RecyclerView.Adapter<SellerReceivedOrdersAdapter. SellerReceivedOrdersViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerReceivedOrdersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.received_order_items, parent, false)
        return SellerReceivedOrdersViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SellerReceivedOrdersViewHolder, position: Int) {
        val currentItem = ProductItems[position]
        holder.productName.text = currentItem.productName
        holder.brandName.text = currentItem.brandName
        holder.buyerName.text = "Ordered by " + currentItem.buyerName
        Glide.with(holder.itemView.context).load(currentItem.productImageUrl)
            .into(holder.productImage)
        holder.productPrice.text = "₹" + currentItem.price
        holder.address.text = currentItem.address
        holder.time.text = currentItem.time

        if(currentItem.delivery_date == "NA") {
            holder.btnLayout.visibility = View.VISIBLE
            holder.deliveryDate.visibility = View.GONE
        } else {
            holder.btnLayout.visibility = View.GONE
            holder.deliveryDate.visibility = View.VISIBLE
            holder.deliveryDate.text = "Deliver on " + currentItem.delivery_date
        }

        holder.acceptBtn.setOnClickListener {

        }

        holder.rejectBtn.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {
        return ProductItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSellerReceivedOrders(updateSellerReceivedOrdersItems: ArrayList<SellerReceivedOrdersItems>) {
        ProductItems.clear()
        ProductItems.addAll(updateSellerReceivedOrdersItems)
        notifyDataSetChanged()
    }

    class SellerReceivedOrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName_order_seller)
        val brandName: TextView = itemView.findViewById(R.id.brandName_order_seller)
        val buyerName: TextView = itemView.findViewById(R.id.buyer_name_order_seller)
        val address: TextView = itemView.findViewById(R.id.address_order_seller)
        val time: TextView = itemView.findViewById(R.id.time_order_seller)
        val productImage: ImageView = itemView.findViewById(R.id.productImg_order_seller)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice_order_seller)
        val acceptBtn: CardView = itemView.findViewById(R.id.accept_btn_order_seller)
        val rejectBtn: CardView = itemView.findViewById(R.id.reject_order_seller)
        val deliveryDate: TextView = itemView.findViewById(R.id.delivery_date_order_seller)
        val btnLayout: LinearLayout = itemView.findViewById(R.id.btn_layout_order_seller)
    }
}