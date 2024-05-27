package com.te.pewala.main_files.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.te.pewala.R
import com.te.pewala.db.AESCrypt
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.main_files.items.ConversationItems
import de.hdodenhof.circleimageview.CircleImageView

class ConversationAdapter(
    val context: Context,
    private val messages: ArrayList<ConversationItems>,
    private val key: ByteArray
) :
    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    val aesCrypt = AESCrypt()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_chat, parent, false)
        return ConversationViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val chat = messages[position]
        holder.name.text = chat.name
        val decryptMessage = aesCrypt.decrypt(chat.lastMsg!!, key)
        holder.lastMsg.text = decryptMessage
        Glide.with(holder.dp).load(chat.imgUrl)
        val bundle = Bundle()
        bundle.putString("receiverUid", chat.uid2)
        holder.body.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_chat, bundle)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateConversations(updateMessages: ArrayList<ConversationItems>) {
        messages.clear()
        messages.addAll(updateMessages)
        notifyDataSetChanged()
    }

    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dp: CircleImageView = itemView.findViewById(R.id.dp_chat_item)
        val name: TextView = itemView.findViewById(R.id.name_chat_item)
        val lastMsg: TextView = itemView.findViewById(R.id.lastMsg_chat_item)
        val body: LinearLayout = itemView.findViewById(R.id.body_chat_item)
    }

}