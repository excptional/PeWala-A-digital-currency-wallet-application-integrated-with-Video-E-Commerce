package com.example.trigeredgedigitalcurrencyproject.main_files.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
import com.example.trigeredgedigitalcurrencyproject.main_files.items.SellerReceivedOrdersItems
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
import java.security.acl.Owner
import java.text.SimpleDateFormat
import java.util.TimeZone

class SellerReceivedOrdersAdapter(
    private val OrdersItems: ArrayList<SellerReceivedOrdersItems>
) :
    RecyclerView.Adapter<SellerReceivedOrdersAdapter.SellerReceivedOrdersViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SellerReceivedOrdersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.received_order_items, parent, false)
        return SellerReceivedOrdersViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: SellerReceivedOrdersViewHolder, position: Int) {
        val currentItem = OrdersItems[position]
        val date = java.util.Date(currentItem.time!!.toLong())
        val timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm aa")
        dateFormat.timeZone = timeZone

        holder.time.text = dateFormat.format(date)
        holder.productName.text = currentItem.productName
        holder.brandName.text = currentItem.brandName
        holder.buyerName.text = "Ordered by " + currentItem.buyerName
        Glide.with(holder.itemView.context).load(currentItem.productImageUrl)
            .into(holder.productImage)
        holder.productPrice.text = "₹" + currentItem.price
        holder.address.text = currentItem.address

        if (currentItem.status == "Pending") {
            holder.btnLayout.visibility = View.VISIBLE
            holder.deliveryDate.visibility = View.GONE
        } else {
            holder.btnLayout.visibility = View.GONE
            holder.deliveryDate.visibility = View.VISIBLE
            holder.deliveryDate.text = "Deliver on " + currentItem.delivery_date
        }

        val bundle = Bundle()
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
        bundle.putString("status", currentItem.status)

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

        holder.item.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.nav_order_details, bundle, navBuilder.build())
        }

    }

    override fun getItemCount(): Int {
        return OrdersItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSellerReceivedOrders(updateSellerReceivedOrdersItems: ArrayList<SellerReceivedOrdersItems>) {
        OrdersItems.clear()
        OrdersItems.addAll(updateSellerReceivedOrdersItems)
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
        val item: CardView = itemView.findViewById(R.id.itemLayout_order_seller)
        val deliveryDate: TextView = itemView.findViewById(R.id.delivery_date_order_seller)
        val btnLayout: LinearLayout = itemView.findViewById(R.id.btn_layout_order_seller)
    }
}