package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.te.pewala.R
import com.te.pewala.db.DBViewModel
import com.te.pewala.main_files.adapters.FeedAdapter
import com.te.pewala.main_files.adapters.ProductsAdapter
import com.te.pewala.main_files.items.FeedItems
import com.te.pewala.main_files.items.ProductsItems

class ProductsFeed : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var feedAdapter: FeedAdapter
    private var feedItemsArray = arrayListOf<FeedItems>()
    private var dbViewModel: DBViewModel? = null
    private lateinit var shimmerContainerFeed: ShimmerFrameLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var notFoundText: TextView
    private lateinit var sellerName: String
    private lateinit var sellerImageUrl: String
    private lateinit var sellerUid: String
    private lateinit var productId: String
    private lateinit var videoUrl: String
    private lateinit var productName: String
    private lateinit var brandName: String

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products_feed, container, false)

        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        viewPager = view.findViewById(R.id.viewPager_feed)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_feed)
        shimmerContainerFeed = view.findViewById(R.id.shimmer_view_feed)
        notFoundText = view.findViewById(R.id.notFound_feed)

        sellerUid = requireArguments().getString("sellerUid").toString()
        productId = requireArguments().getString("productId").toString()
        videoUrl = requireArguments().getString("videoUrl").toString()
        productName = requireArguments().getString("productName").toString()
        brandName = requireArguments().getString("brandName").toString()

        loadData()

        feedAdapter = FeedAdapter(requireContext(), feedItemsArray)
        viewPager.adapter = feedAdapter

        swipeRefreshLayout.setOnRefreshListener {
            shimmerContainerFeed.startShimmer()
            shimmerContainerFeed.visibility = View.VISIBLE
            viewPager.visibility = View.GONE
            dbViewModel!!.getVideoTutorials(productId)
            dbViewModel!!.videoTutorialsData.observe(viewLifecycleOwner) {
                fetchVideos(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        return view;
    }

    private fun fetchVideos(list: MutableList<DocumentSnapshot>) {
        feedItemsArray = arrayListOf()
        if (list.size == 0) {
            notFoundText.visibility = View.VISIBLE
            shimmerContainerFeed.visibility = View.GONE
            viewPager.visibility = View.GONE
        } else {
            for (document in list) {
                if (document.exists()) {
                    val data = FeedItems(
                        sellerName,
                        sellerImageUrl,
                        document.getString("video_url"),
                        productName,
                        brandName,
                        document.getString("description"),
                        productId,
                        sellerUid
                    )
                    feedItemsArray.add(data)
                }
            }
            feedAdapter.updateFeed(feedItemsArray)
            shimmerContainerFeed.clearAnimation()
            shimmerContainerFeed.visibility = View.GONE
            viewPager.visibility = View.VISIBLE
            notFoundText.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loadData() {
        dbViewModel!!.fetchAccountDetails(sellerUid)
        dbViewModel!!.accDetails.observe(viewLifecycleOwner) { doc ->
            if (doc.exists()) {
                sellerName = doc.getString("name").toString()
                sellerImageUrl = doc.getString("image_url").toString()
                dbViewModel!!.getVideoTutorials(productId)
                dbViewModel!!.videoTutorialsData.observe(viewLifecycleOwner) {
                    fetchVideos(it)
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

}