package com.te.celer.main_files.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.te.celer.R

class CommunityAdapter(private val items: List<String>, private val VIEW_TYPE: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class CommunityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_community_item)
        val imageView: ImageView = itemView.findViewById(R.id.image_community_item)
    }

    inner class AddCommunityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_add_community_item)
        val imageView: ImageView = itemView.findViewById(R.id.image_add_community_item)
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_community, parent, false)
            CommunityViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_community, parent, false)
            AddCommunityViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is CommunityViewHolder) {
            holder.textView.text = item
        } else if (holder is AddCommunityViewHolder) {
            holder.textView.text = item
        }
    }

    override fun getItemCount(): Int = items.size
}
