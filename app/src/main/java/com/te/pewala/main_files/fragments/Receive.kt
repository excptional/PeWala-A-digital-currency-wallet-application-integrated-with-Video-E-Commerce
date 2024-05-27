package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import java.io.*
import java.util.*


class Receive : Fragment() {

    private lateinit var dbViewModel: DBViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var qrImage: ImageView
    private lateinit var cardId: TextView
    private lateinit var whiteView: View
    private lateinit var loaderReceive: LottieAnimationView
    private lateinit var mainLayout: RelativeLayout
    private lateinit var scanBtn: CardView
    private lateinit var backBtn: ImageButton
    private lateinit var shareBtn: CardView
    private lateinit var downloadBtn: CardView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_receive, container, false)

        requireActivity().window.statusBarColor = Color.parseColor("#F7F9FD")

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        qrImage = view.findViewById(R.id.qr_image)
        cardId = view.findViewById(R.id.card_id_receive)
        mainLayout = view.findViewById(R.id.main_layout_receive)
        whiteView = view.findViewById(R.id.whiteView_receive)
        loaderReceive = view.findViewById(R.id.loader_receive)
        backBtn = view.findViewById(R.id.back_btn_receive)
        shareBtn = view.findViewById(R.id.share_btn_receive)
        downloadBtn = view.findViewById(R.id.download_btn_receive)
        scanBtn = view.findViewById(R.id.scan_btn_receive)

        loadData(view)

//        val backBuilder = NavOptions.Builder()
//        backBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.slide_out)

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        scanBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_qr_scanner2)
        }

        shareBtn.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loaderReceive.visibility = View.VISIBLE
            shareQRImage(qrImage)
        }

        downloadBtn.setOnClickListener {
            saveQRImage(qrImage.drawable.toBitmap())
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun loadData(view: View) {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if(it != null) {
                dbViewModel.fetchAccountDetails(it.uid)
                dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
                    if(list.exists()) {
                        Glide.with(view).load(list.getString("QR Code")).into(qrImage)
                        cardId.text = "Your card id : ${list.getString("Card Id")}"
                        mainLayout.visibility = View.VISIBLE
                        whiteView.visibility = View.GONE
                        loaderReceive.visibility = View.GONE
                    } else {
                        whiteView.visibility = View.GONE
                        loaderReceive.visibility = View.GONE
                        mainLayout.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun shareQRImage(imageView: ImageView) {
        val intent = Intent(Intent.ACTION_SEND).setType("image/*")
        val rand = Random()
        val randNo = rand.nextInt(100000)
        val bitmap = imageView.drawable.toBitmap()
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, bitmap, "IMG:$randNo", null)
        val uri = Uri.parse(path)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        whiteView.visibility = View.GONE
        loaderReceive.visibility = View.GONE
        startActivity(intent)
    }

    private fun saveQRImage(bitmap: Bitmap?) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireContext().contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(requireContext(), "Saved to Gallery" , Toast.LENGTH_SHORT).show()
        }
    }

}