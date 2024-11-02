package com.te.pewala.main_files.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.te.pewala.R
import com.te.pewala.main_files.models.PendingPaymentsItems
import com.te.pewala.main_files.models.RedeemItems
import java.text.SimpleDateFormat
import java.util.TimeZone

class PendingPaymentsAdapter(
    private val pendingPaymentsItems: ArrayList<PendingPaymentsItems>
): RecyclerView.Adapter<PendingPaymentsAdapter.PendingPaymentsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingPaymentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pending_payment, parent, false)
        return PendingPaymentsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pendingPaymentsItems.size
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: PendingPaymentsViewHolder, position: Int) {
        val currentItem = pendingPaymentsItems[position]
        val date = java.util.Date(currentItem.time!!.toLong())
        val timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm aa")
        dateFormat.timeZone = timeZone

        holder.time.text = "Time: "+ dateFormat.format(date)

        holder.amount.text = "₹${currentItem.amount}"

        holder.orderId.text = "Order Id: " + currentItem.orderId

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePendingPaymentsItems(updateRedeemItems: ArrayList<PendingPaymentsItems>) {
        pendingPaymentsItems.clear()
        pendingPaymentsItems.addAll(updateRedeemItems)
        notifyDataSetChanged()
    }

    class PendingPaymentsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val amount: TextView = itemView.findViewById(R.id.amount_pp_item)
        val time: TextView = itemView.findViewById(R.id.time_pp_item)
        val orderId: TextView = itemView.findViewById(R.id.order_id_pp_item)
    }

}