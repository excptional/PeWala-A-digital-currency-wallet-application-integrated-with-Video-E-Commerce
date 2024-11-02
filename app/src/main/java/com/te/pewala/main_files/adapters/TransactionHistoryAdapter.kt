package com.te.pewala.main_files.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.te.pewala.R
import com.te.pewala.main_files.models.TransactionHistoryItems
import java.text.SimpleDateFormat
import java.util.TimeZone

class TransactionHistoryAdapter(
    private val context: Context,
    private val transactionHistoryItems: ArrayList<TransactionHistoryItems>
) :
    RecyclerView.Adapter<TransactionHistoryAdapter.TransactionHistoryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_history, parent, false)
        return TransactionHistoryViewHolder(view)
    }

//    val dbViewModel = ViewModelProvider(viewModelStoreOwner)[DBViewModel::class.java]

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: TransactionHistoryViewHolder, position: Int) {
        val currentItem = transactionHistoryItems[position]
        val time: Long = currentItem.time!!.toLong()
        val date = java.util.Date(time)
        val timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm aa")
        dateFormat.timeZone = timeZone


        holder.name.text = currentItem.name
        when (currentItem.operation) {
            "Debit" -> {
                holder.payType.text = "Send to +91 ${currentItem.phone}"
                holder.icon.setImageDrawable(getDrawable(context, R.drawable.send_icon))
            }

            "Credit" -> {
                holder.payType.text =
                    "Receive from +91 ${currentItem.phone}"
                holder.icon.setImageDrawable(getDrawable(context, R.drawable.receive_money))

            }

            "Add" -> {
                holder.payType.text = "Added to +91 ${currentItem.phone}"
                holder.icon.setImageDrawable(getDrawable(context, R.drawable.receive_money))

            }

            else -> {
                holder.payType.text =
                    "Withdraw from +91 ${currentItem.phone}"
                holder.icon.setImageDrawable(getDrawable(context, R.drawable.send_icon))
            }
        }
        holder.time.text = dateFormat.format(date)
        holder.amount.text = "₹${currentItem.amount}"
        holder.tId.text = currentItem.tId
        
    }

    override fun getItemCount(): Int {
        return transactionHistoryItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTransactionHistory(updateTransactionHistoryItems: ArrayList<TransactionHistoryItems>) {
        transactionHistoryItems.clear()
        transactionHistoryItems.addAll(updateTransactionHistoryItems)
        notifyDataSetChanged()
    }

    class TransactionHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_item)
        val payType: TextView = itemView.findViewById(R.id.pay_type_item)
        val icon: ImageView = itemView.findViewById(R.id.icon_item)
        val amount: TextView = itemView.findViewById(R.id.amount_item)
        val time: TextView = itemView.findViewById(R.id.time_item)
        val tId: TextView = itemView.findViewById(R.id.tId_item)
        val body: LinearLayout = itemView.findViewById(R.id.body_item)
    }
}