package com.te.pewala.main_files.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.te.pewala.R
import com.te.pewala.db.AESCrypt
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.main_files.items.MessageItems
import java.text.SimpleDateFormat
import java.util.TimeZone
import javax.crypto.SecretKey

class ChatAdapter(
    val context: Context,
    viewModelStoreOwner: ViewModelStoreOwner,
    private val messages: ArrayList<MessageItems>,
    private val key: ByteArray
) :
    RecyclerView.Adapter<ViewHolder>() {

    val authViewModel = ViewModelProvider(viewModelStoreOwner)[AuthViewModel::class.java]
    val dbViewModel = ViewModelProvider(viewModelStoreOwner)[DBViewModel::class.java]
    val aesCrypt = AESCrypt()
    private var isStart = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(!isStart) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_top_description_chat, parent, false)
            return TopDescriptionViewHolder(view)

        } else {
            if(viewType == 1) {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_message_receiver, parent, false)
                return ReceiverViewHolder(view)
            } else {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_message_sender, parent, false)
                return SenderViewHolder(view)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMessage = messages[position]
        val decryptMessage = aesCrypt.decrypt(currentMessage.message!!, key)
        val time: Long = currentMessage.time!!.toLong()
        val date = java.util.Date(time)
        val timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val dateFormat = SimpleDateFormat("HH:mm MMM dd, yyyy")
        dateFormat.timeZone = timeZone
        isStart = true
        if(holder.javaClass == ReceiverViewHolder::class.java) {
            val viewHolder = holder as ReceiverViewHolder
            viewHolder.messageReceive.text = decryptMessage
            viewHolder.timeReceive.text = dateFormat.format(date)
        } else if(holder.javaClass == SenderViewHolder::class.java) {
            val viewHolder = holder as SenderViewHolder
            viewHolder.messageSend.text = decryptMessage
            viewHolder.statusSend.text = currentMessage.status
            viewHolder.timeSend.text = dateFormat.format(date)
        }

        if (currentMessage.isSender == false) {
            dbViewModel.readMessage(currentMessage.cId!!, currentMessage.time)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(messages[position].isSender == true) {
            2
        } else {
            1
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMessages(updateMessages: ArrayList<MessageItems>) {
        messages.clear()
        messages.addAll(updateMessages)
        notifyDataSetChanged()
    }

    class TopDescriptionViewHolder(itemView: View) : ViewHolder(itemView)

    class ReceiverViewHolder(itemView: View) : ViewHolder(itemView) {
        val messageReceive: TextView = itemView.findViewById(R.id.content_chat_receiver_item)
        val timeReceive: TextView = itemView.findViewById(R.id.time_chat_receiver_item)
        val bodyReceive: LinearLayout = itemView.findViewById(R.id.body_chat_receiver_item)
    }

    class SenderViewHolder(itemView: View) : ViewHolder(itemView) {
        val messageSend: TextView = itemView.findViewById(R.id.content_chat_sender_item)
        val timeSend: TextView = itemView.findViewById(R.id.time_chat_sender_item)
        val bodySend: LinearLayout = itemView.findViewById(R.id.body_chat_sender_item)
        val statusSend: TextView = itemView.findViewById(R.id.status_chat_sender_item)

    }

}