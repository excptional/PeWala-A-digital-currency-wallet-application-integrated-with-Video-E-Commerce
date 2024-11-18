package com.te.celer.main_files.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.te.celer.R
import com.te.celer.db.LocalStorage
import com.te.celer.main_files.dialog.AddCommunityDialog
import de.hdodenhof.circleimageview.CircleImageView

class CreatePost : Fragment() {

    private lateinit var profileImage: CircleImageView
    private lateinit var username: TextView
    private lateinit var inputText: TextInputEditText
    private lateinit var addImage: ImageView
    private lateinit var addTag: LinearLayout
    private lateinit var dialog: AddCommunityDialog
    private val localStorage = LocalStorage()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        profileImage = view.findViewById(R.id.user_icon_create_post)
        username = view.findViewById(R.id.username_create_post)
        inputText = view.findViewById(R.id.input_text_create_post)
        addImage = view.findViewById(R.id.add_image_create_post)
        addTag = view.findViewById(R.id.add_tags_create_post)

        dialog = AddCommunityDialog(requireContext())

        val userdata = localStorage.getData(requireContext(), "user_data")
        Glide.with(view).load(userdata!!["image_url"]!!).into(profileImage)
        username.text = userdata["name"]

        val list = listOf("ONDC", "Shopping", "Style", "Fashion", "Trends", "Winter Style")

        addTag.setOnClickListener {
            dialog.showDialog(list)
        }

        return view
    }

}