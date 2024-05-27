package com.te.pewala.main_files.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.te.pewala.R
import com.te.pewala.main_files.items.FeedItems
import de.hdodenhof.circleimageview.CircleImageView

class FeedAdapter(
    private val context: Context,
    private val feedItems: ArrayList<FeedItems>
) :
    RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    private var duration: Int = 0
    private var handler: Handler? = null
    private var isVideoCompleted = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed, parent, false)
        return FeedViewHolder(view)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val currentItem = feedItems[position]
        holder.loader.visibility = View.VISIBLE
        holder.loader.animate()
        holder.name.text = currentItem.profileName
        holder.productName.text = currentItem.productName
        holder.brandName.text = currentItem.brandName
        holder.description.text = currentItem.description
        Glide.with(holder.itemView).load(currentItem.profileImgUrl).into(holder.img)
        holder.name.text = currentItem.profileName
        holder.feedView.setVideoPath(currentItem.videoUrl)
        holder.feedView.start()

        holder.feedView.setOnPreparedListener { mp ->
            holder.loader.visibility = View.GONE
            mp.setOnTimedTextListener { _, _ ->
                if (mp.currentPosition >= 30000) {
                    mp.pause() // Stop or pause the video after 30 seconds
                    isVideoCompleted = true
                }
            }
            mp.start()
            duration = mp.duration
            handler = Handler()
            holder.startProgressHandler(handler!!, duration)
        }

//        holder.feedView.setOnPreparedListener {
//            it.setOnInfoListener { mp, what, extra ->
//                when (what) {
//                    MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
//                        holder.loader.visibility = View.VISIBLE
//                        holder.progressbar.visibility = View.GONE
//                    }
//                    MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
//                        holder.loader.visibility = View.GONE
//                        holder.progressbar.visibility = View.VISIBLE
//                    }
//                    MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
//                        holder.loader.visibility = View.GONE
//                        holder.progressbar.visibility = View.VISIBLE
//                    }
//                }
//                true
//            }
//        }

        holder.feedView.setOnErrorListener { _, _, _ ->
            holder.loader.visibility = View.GONE
            true
        }

        holder.feedView.setOnCompletionListener {
            holder.playPauseBtn.setImageResource(android.R.drawable.ic_media_play)
            isVideoCompleted = true
        }

        holder.clickView.setOnClickListener{
            if (holder.feedView.isPlaying) {
                holder.feedView.pause()

//                holder.playPauseBtn.setImageResource(R.drawable.play_icon)
//                holder.playPauseBtn.visibility = View.VISIBLE
//                Handler().postDelayed({
//                    holder.playPauseBtn.animate()
//                        .alpha(0f)
//                        .setDuration(1000) // duration in milliseconds
//                        .withEndAction {
//                            holder.playPauseBtn.visibility = View.GONE
//                        }
//                        .start()
//                }, 2000)
            } else {
                if (isVideoCompleted) {
                    holder.feedView.seekTo(0)
                    holder.progressbar.progress = 0
                    isVideoCompleted = false
                }
                holder.feedView.start()
//                holder.playPauseBtn.setImageResource(R.drawable.pause_icon)
//                holder.playPauseBtn.visibility = View.GONE
//                Handler().postDelayed({
//                    holder.playPauseBtn.animate()
//                        .alpha(0f)
//                        .setDuration(1000) // duration in milliseconds
//                        .withEndAction {
//                            holder.playPauseBtn.visibility = View.INVISIBLE
//                        }
//                        .start()
//                }, 2000)
            }
        }

    }

    override fun getItemCount(): Int {
        return feedItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateFeed(updateFeedItems: ArrayList<FeedItems>) {
        feedItems.clear()
        feedItems.addAll(updateFeedItems)
        notifyDataSetChanged()
    }

    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.seller_name_feed_item)
        val productName: TextView = itemView.findViewById(R.id.seller_name_feed_item)
        val brandName: TextView = itemView.findViewById(R.id.seller_name_feed_item)
        val description: TextView = itemView.findViewById(R.id.description_feed_item)
        val img: CircleImageView = itemView.findViewById(R.id.seller_img_feed_item)
        val feedView: VideoView = itemView.findViewById(R.id.video_view_feed_item)
//        val orderBtn: CardView = itemView.findViewById(R.id.order_btn_feed_item)
        val progressbar: ProgressBar = itemView.findViewById(R.id.time_loader_feed_item)
        val loader: LottieAnimationView = itemView.findViewById(R.id.loader_feed_item)
        val playPauseBtn: ImageView = itemView.findViewById(R.id.play_pause_btn_feed_item)
        val clickView: RelativeLayout = itemView.findViewById(R.id.click_view_feed_item)

        fun startProgressHandler(handler: Handler, duration: Int) {
            handler.post(object : Runnable {
                override fun run() {
                    val currentPosition = feedView.currentPosition
                    progressbar.progress = currentPosition * 100 / duration
                    handler.postDelayed(this, 1000) // Update progress every second
                }
            })
        }
    }
}