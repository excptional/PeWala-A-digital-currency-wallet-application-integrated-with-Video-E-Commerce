package com.example.trigeredgedigitalcurrencyproject.main_files.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.main_files.items.ContactItems
import de.hdodenhof.circleimageview.CircleImageView

class ContactAdapter(
    private val context: Context,
    private val ContactItems: ArrayList<ContactItems>
) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_items, parent, false)
        return ContactViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentItem = ContactItems[position]
        holder.name.text = currentItem.name
        holder.phone.text = currentItem.phone
        Glide.with(context).load(currentItem.imgUrl).into(holder.icon)
        holder.contactBody.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("walletId", "${currentItem.phone}@digital")
            Navigation.findNavController(holder.itemView).navigate(R.id.nav_final_pay, bundle)
        }
    }

    override fun getItemCount(): Int {
        return ContactItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateContact(updateContactItems: ArrayList<ContactItems>) {
        ContactItems.clear()
        ContactItems.addAll(updateContactItems)
        notifyDataSetChanged()
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_contact_item)
        val phone: TextView = itemView.findViewById(R.id.phone_contact_item)
        val icon: CircleImageView = itemView.findViewById(R.id.icon_contact_item)
        val contactBody: LinearLayout = itemView.findViewById(R.id.contact_body_item)
    }
}