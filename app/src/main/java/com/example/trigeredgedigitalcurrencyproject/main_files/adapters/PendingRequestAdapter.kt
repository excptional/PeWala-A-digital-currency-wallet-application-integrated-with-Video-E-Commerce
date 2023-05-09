package com.example.trigeredgedigitalcurrencyproject.main_files.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.main_files.items.RedeemItems

class PendingRequestAdapter(
    private val context: Context,
    private val redeemItems: ArrayList<RedeemItems>
) :
    RecyclerView.Adapter<PendingRequestAdapter.PendingRequestViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PendingRequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pending_redeem_request, parent, false)
        return PendingRequestViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PendingRequestViewHolder, position: Int) {
        val currentItem = redeemItems[position]
        holder.time.text = currentItem.time
        holder.amount.text = "₹${currentItem.amount}"
    }

    override fun getItemCount(): Int {
        return redeemItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRedeemItems(updateRedeemItems: ArrayList<RedeemItems>) {
        redeemItems.clear()
        redeemItems.addAll(updateRedeemItems)
        notifyDataSetChanged()
    }

    class PendingRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amount: TextView = itemView.findViewById(R.id.amount_pending)
        val time: TextView = itemView.findViewById(R.id.time_pending)
    }
}