package com.te.pewala.main_files.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.VideoView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.te.pewala.R
import com.te.pewala.main_files.items.VideoTutorialsItems
class VideoTutorialsAdapter(
    private val context: Context,
    private val videoTutorialsItems: ArrayList<VideoTutorialsItems>
) :
    RecyclerView.Adapter<VideoTutorialsAdapter.VideoTutorialsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoTutorialsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_place_view, parent, false)
        return VideoTutorialsViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VideoTutorialsViewHolder, position: Int) {
        val currentItem = videoTutorialsItems[position]
        
        holder.videoBody.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("productId", currentItem.productId)
            bundle.putString("sellerUid", currentItem.sellerUid)
            bundle.putString("videoUrl", currentItem.videoUrl)
            bundle.putString("productName", currentItem.productName)
            bundle.putString("brandName", currentItem.brandName)
            Navigation.findNavController(holder.itemView).navigate(R.id.nav_product_feed, bundle)
        }

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
    }

    override fun getItemCount(): Int {
        return videoTutorialsItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateVideoTutorials(updateVideoTutorialsItems: ArrayList<VideoTutorialsItems>) {
        videoTutorialsItems.clear()
        videoTutorialsItems.addAll(updateVideoTutorialsItems)
        notifyDataSetChanged()
    }

    class VideoTutorialsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoView: VideoView = itemView.findViewById(R.id.videoView_order_item)
        val videoBody: RelativeLayout = itemView.findViewById(R.id.body_order_item)
        val loader: LottieAnimationView = itemView.findViewById(R.id.loader_order_item)
    }
}