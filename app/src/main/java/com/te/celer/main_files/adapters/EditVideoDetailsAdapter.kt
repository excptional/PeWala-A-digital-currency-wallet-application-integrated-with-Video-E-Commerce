package com.te.celer.main_files.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.te.celer.R
import com.te.celer.main_files.models.EditVideoDetailsItems

class EditVideoDetailsAdapter (
    private val videoDetailsItems: ArrayList<EditVideoDetailsItems>
) :
    RecyclerView.Adapter<EditVideoDetailsAdapter.EditVideoDetailsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditVideoDetailsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_details_edit, parent, false)
        return EditVideoDetailsViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EditVideoDetailsViewHolder, position: Int) {
        val currentItem = videoDetailsItems[position]
        holder.description.text = currentItem.description
        holder.videoView.setVideoPath(currentItem.videoUrl)
        holder.videoView.start()
        holder.videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.setVolume(0f, 0f)
            holder.loader.visibility = View.GONE

        }
        holder.videoView.start()
        holder.videoView.setOnErrorListener { _, _, _ ->
            holder.loader.visibility = View.GONE
            true
        }

        holder.videoBody.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("productId", currentItem.productId)
            bundle.putString("documentId", currentItem.documentId)
            bundle.putString("videoUrl", currentItem.videoUrl)
            bundle.putString("description", currentItem.description)
//            Navigation.findNavController(holder.itemView).navigate(R.id.nav_product_feed, bundle)
        }

    }

    override fun getItemCount(): Int {
        return videoDetailsItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateVideoDetails(updateVideoItems: ArrayList<EditVideoDetailsItems>) {
        videoDetailsItems.clear()
        videoDetailsItems.addAll(updateVideoItems)
        notifyDataSetChanged()
    }

    class EditVideoDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoView: VideoView = itemView.findViewById(R.id.video_view_edit)
        val description: TextView = itemView.findViewById(R.id.description_edit)
        val loader: LottieAnimationView = itemView.findViewById(R.id.loader_edit)
        val videoBody: RelativeLayout = itemView.findViewById(R.id.body_edit)
    }
}