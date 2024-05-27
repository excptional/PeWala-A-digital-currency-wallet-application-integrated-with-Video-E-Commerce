package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.te.pewala.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.te.pewala.db.DBViewModel
import com.te.pewala.db.Response

class UploadVideoTutorials : Fragment() {

    private lateinit var uploadVideoFromLocal: LinearLayout
    private lateinit var videoView: VideoView
    private lateinit var descriptionET: TextInputEditText
    private lateinit var uploadBtn: CardView
    private val VIDEO_TRIM_REQUEST_CODE = 101
    private val STORAGE_PERMISSION_REQUEST_CODE = 102
    private lateinit var productImage: ImageView
    private lateinit var productId: String
    private lateinit var productName: TextView
    private lateinit var brandName: TextView
    private lateinit var productDescription: TextView
    private val dbViewModel: DBViewModel by viewModels()
    private lateinit var outputUri: Uri
    private lateinit var whiteView: View
    private lateinit var loader: LottieAnimationView
    private lateinit var backBtn: ImageView
    @SuppressLint("MissingInflatedId", "CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload_video_tutorials, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        uploadVideoFromLocal = view.findViewById(R.id.btn_upload_video)
        videoView = view.findViewById(R.id.video_view_upload_video)
        descriptionET = view.findViewById(R.id.description_upload_video)
        uploadBtn = view.findViewById(R.id.submit_btn_upload_video)
        productImage = view.findViewById(R.id.productImg_upload_video)
        productName = view.findViewById(R.id.product_name_upload_video)
        brandName = view.findViewById(R.id.brand_name_upload_video)
        productDescription = view.findViewById(R.id.product_description_upload_video)
        whiteView = view.findViewById(R.id.whiteView_upload_video)
        loader = view.findViewById(R.id.loader_upload_video)
        backBtn = view.findViewById(R.id.back_btn_upload_video)

        productName.text = requireArguments().getString("productName")!!
        productDescription.text = requireArguments().getString("description")!!
        brandName.text = requireArguments().getString("brandName")!!
        productId = requireArguments().getString("productId")!!

        Glide.with(productImage).load(requireArguments().getString("productImageUrl")!!)

        uploadVideoFromLocal.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"
            startActivityForResult(
                Intent.createChooser(intent, "Select Video"),
                VIDEO_TRIM_REQUEST_CODE
            )
//            if (ActivityCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(READ_EXTERNAL_STORAGE),
//                    STORAGE_PERMISSION_REQUEST_CODE
//                )
//            } else {
////                val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
////                startActivityForResult(intent, VIDEO_TRIM_REQUEST_CODE)
//                val intent = Intent(Intent.ACTION_GET_CONTENT)
//                intent.type = "video/*"
//                startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO_TRIM_REQUEST_CODE)
//            }
        }

        uploadBtn.setOnClickListener {
            upload()
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun upload() {
        whiteView.visibility = View.VISIBLE
        loader.visibility = View.VISIBLE
        val description = descriptionET.text.toString()
        if (!videoView.isVisible) {
            Toast.makeText(requireContext(), "Upload a video first", Toast.LENGTH_SHORT).show()
            whiteView.visibility = View.GONE
            loader.visibility = View.GONE
        } else if (description.isEmpty()) {
            descriptionET.error = "Write description to proceed"
            whiteView.visibility = View.GONE
            loader.visibility = View.GONE
        } else {
            dbViewModel.uploadVideoTutorial(outputUri, description, productId)
            dbViewModel.dbResponse.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Success -> {
                        Toast.makeText(requireContext(), "Tutorial successfully uploaded", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    }

                    is Response.Failure -> {
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                        Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VIDEO_TRIM_REQUEST_CODE && resultCode == RESULT_OK) {
            val videoUri = data?.data
            videoUri?.let { uri ->
                GlobalScope.launch(Dispatchers.Main) {
                    outputUri = uri
                    uploadVideoFromLocal.visibility = View.GONE
                    videoView.visibility = View.VISIBLE
                    videoView.setVideoURI(uri)
                    videoView.setOnPreparedListener {
                        mp -> mp.start()
                        mp.isLooping = true
                        mp.setVolume(0f, 0f)
                    }
                    videoView.setOnCompletionListener { mp -> mp.start() }
                }
            }
        }
    }


//    private fun showVideo(trimVideo: Uri) {
//        uploadVideoFromLocal.visibility = View.GONE
//        videoView.visibility = View.VISIBLE
//        videoView.setVideoURI(trimVideo)
//        videoView.setOnPreparedListener { mp -> mp.start() }
//        videoView.setOnCompletionListener { mp -> mp.start() }
//    }

}