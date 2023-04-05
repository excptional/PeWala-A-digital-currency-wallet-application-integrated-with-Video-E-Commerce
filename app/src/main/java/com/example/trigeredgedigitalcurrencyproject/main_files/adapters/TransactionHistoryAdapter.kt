package com.example.trigeredgedigitalcurrencyproject.main_files.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
import com.example.trigeredgedigitalcurrencyproject.main_files.items.TransactionHistoryItems

class TransactionHistoryAdapter(
    private val context: Context,
    private val TransactionHistoryItems: ArrayList<TransactionHistoryItems>
) :
    RecyclerView.Adapter<TransactionHistoryAdapter.TransactionHistoryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_history_items, parent, false)
        return TransactionHistoryViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TransactionHistoryViewHolder, position: Int) {
        val currentItem = TransactionHistoryItems[position]
        holder.time.text = currentItem.time
        holder.amount.text = "₹${currentItem.amount}"
        holder.name.text = currentItem.name
        when (currentItem.operation) {
            "Send" -> {
                holder.payType.text = "Send to +91 ${currentItem.phone}"
                holder.icon.setImageDrawable(getDrawable(context, R.drawable.send_icon))
            }
            "Receive" -> {
                holder.payType.text = "Receive from +91 ${currentItem.phone}"
                holder.icon.setImageDrawable(getDrawable(context, R.drawable.receive_money))

            }
            "Add" -> {
                holder.payType.text = "Added to +91 ${currentItem.phone}"
                holder.icon.setImageDrawable(getDrawable(context, R.drawable.receive_money))

            }
            else -> {
                holder.payType.text = "Withdraw from +91 ${currentItem.phone}"
                holder.icon.setImageDrawable(getDrawable(context, R.drawable.send_icon))
            }
        }
    }

    override fun getItemCount(): Int {
        return TransactionHistoryItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTransactionHistory(updateTransactionHistoryItems: ArrayList<TransactionHistoryItems>) {
        TransactionHistoryItems.clear()
        TransactionHistoryItems.addAll(updateTransactionHistoryItems)
        notifyDataSetChanged()
    }

    class TransactionHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_item)
        val payType: TextView = itemView.findViewById(R.id.pay_type_item)
        val icon: ImageView = itemView.findViewById(R.id.icon_item)
        val amount: TextView = itemView.findViewById(R.id.amount_item)
        val time: TextView = itemView.findViewById(R.id.time_item)
        val body: LinearLayout = itemView.findViewById(R.id.body_item)
    }
}