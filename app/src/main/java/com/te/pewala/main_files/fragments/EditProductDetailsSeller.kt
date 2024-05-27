package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.te.pewala.R
import com.te.pewala.db.DBViewModel
import com.te.pewala.main_files.adapters.EditVideoDetailsAdapter
import com.te.pewala.main_files.items.EditVideoDetailsItems

class EditProductDetailsSeller : Fragment() {

    private lateinit var backBtn: ImageView
    private lateinit var productCard: CardView
    private lateinit var productName: TextView
    private lateinit var brandName: TextView
    private lateinit var productImage: ImageView
    private lateinit var productDescription: TextView
    private lateinit var videoAdapter: EditVideoDetailsAdapter
    private var videoItemsArray = arrayListOf<EditVideoDetailsItems>()
    private lateinit var editProductRecyclerView: RecyclerView
    private lateinit var productId: String
    private val dbViewModel: DBViewModel by viewModels()
    private lateinit var uploadProduct: RelativeLayout

    @SuppressLint("MissingInflatedId", "CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_product_details_seller, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        backBtn = view.findViewById(R.id.back_btn_edit_product)
        productCard = view.findViewById(R.id.product_card_edit_product)
        productName = view.findViewById(R.id.product_name_edit_product)
        productImage = view.findViewById(R.id.product_img_edit_product)
        brandName = view.findViewById(R.id.brand_name_edit_product)
        productDescription = view.findViewById(R.id.product_description_edit_product)
        editProductRecyclerView = view.findViewById(R.id.recyclerview_edit_product)
        uploadProduct = view.findViewById(R.id.btn_upload_edit_product)

        videoAdapter = EditVideoDetailsAdapter(videoItemsArray)
        editProductRecyclerView.layoutManager = GridLayoutManager(view.context, 2)
        editProductRecyclerView.setHasFixedSize(true)
        editProductRecyclerView.setItemViewCacheSize(20)
        editProductRecyclerView.adapter = videoAdapter

        productName.text = requireArguments().getString("productName")!!
        productDescription.text = requireArguments().getString("description")!!
        brandName.text = requireArguments().getString("brandName")!!
        productId = requireArguments().getString("productId")!!
        Glide.with(productImage).load(requireArguments().getString("productImageUrl")!!)

        dbViewModel.getVideoTutorials(productId)
        dbViewModel.videoTutorialsData.observe(viewLifecycleOwner) {
            fetchVideos(it)
        }

        val bundle = Bundle()
        bundle.putString("productId", productId)
        bundle.putString("description", productDescription.text.toString())
        bundle.putString("productName", productName.text.toString())
        bundle.putString("brandName", brandName.text.toString())
        bundle.putString("productImageUrl", requireArguments().getString("productImageUrl"))

        uploadProduct.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_upload_video_tutorials, bundle)
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun fetchVideos(list: MutableList<DocumentSnapshot>) {
        videoItemsArray = arrayListOf()
        if (list.size == 0) {
        } else {
            for (document in list) {
                if (document.exists()) {
                    val data = EditVideoDetailsItems(
                        document.getString("Video Url"),
                        document.getString("Description"),
                        document.id,
                        productId
                    )
                    videoItemsArray.add(data)
                }
            }
            videoAdapter.updateVideoDetails(videoItemsArray)
            editProductRecyclerView.visibility = View.VISIBLE
        }
    }

}