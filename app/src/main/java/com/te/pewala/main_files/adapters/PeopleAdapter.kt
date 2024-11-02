package com.te.pewala.main_files.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.te.pewala.R
import com.te.pewala.main_files.models.PeopleItems
import de.hdodenhof.circleimageview.CircleImageView

class PeopleAdapter(
    private val context: Context,
    private val peopleItems: ArrayList<PeopleItems>
) :
    RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PeopleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_people, parent, false)
        return PeopleViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val currentItem = peopleItems[position]
        holder.name.text = currentItem.name
        Glide.with(context).load(currentItem.icon).into(holder.icon)
        holder.peopleBody.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("payerUid", currentItem.uid)
            Navigation.findNavController(holder.itemView).navigate(R.id.nav_person_history, bundle)
        }
    }

    override fun getItemCount(): Int {
        return peopleItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePeople(updatePeopleItems: ArrayList<PeopleItems>) {
        peopleItems.clear()
        peopleItems.addAll(updatePeopleItems)
        notifyDataSetChanged()
    }

    class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_people_item)
        val icon: CircleImageView = itemView.findViewById(R.id.icon_people_item)
        val peopleBody: LinearLayout = itemView.findViewById(R.id.people_body_item)
    }
}