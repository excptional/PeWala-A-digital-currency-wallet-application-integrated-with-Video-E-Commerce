package com.te.celer.main_files.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.te.celer.R
import com.te.celer.db.AESCrypt
import com.te.celer.db.DBViewModel
import com.te.celer.main_files.adapters.ConversationAdapter
import com.te.celer.main_files.models.ConversationItems

class ChatList : Fragment() {

    private lateinit var backBtn: ImageView
    private lateinit var notFoundText: TextView
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var conversationAdapter: ConversationAdapter
    private var conversationItemsArray = arrayListOf<ConversationItems>()
    val aesCrypt = AESCrypt()
    val key = ByteArray(32)
    private lateinit var uid: String
    private val dbViewModel: DBViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        uid = requireArguments().getString("uid")!!

        notFoundText = view.findViewById(R.id.nothing_found_chat_list)
        shimmer = view.findViewById(R.id.shimmer_chat_list)
        recyclerView = view.findViewById(R.id.recyclerview_chat_list)
        backBtn = view.findViewById(R.id.back_btn_chat_list)

        shimmer.startShimmer()
        shimmer.animate()


        key.fill(1)
        conversationAdapter =
            ConversationAdapter(requireContext(), conversationItemsArray, key)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setItemViewCacheSize(20)
        recyclerView.adapter = conversationAdapter

        loadData()

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }


        return view

    }

    private fun fetchData(list: MutableList<DocumentSnapshot>) {
        if (list.isNotEmpty()) {
            conversationItemsArray = arrayListOf()

            for (item in list) {
                val conversationData = ConversationItems(
                    item.getString("name"),
                    item.getString("image_url"),
                    item.getString("last_message"),
                    uid,
                    item.getString("uid")
                )
                conversationItemsArray.add(conversationData)
            }
            conversationAdapter.updateConversations(conversationItemsArray)
        } else {
            notFoundText.visibility = View.VISIBLE
        }
        shimmer.visibility = View.GONE
    }

    private fun loadData() {
        if (!uid.isNullOrEmpty()) {
            dbViewModel.getConversations(uid)
            dbViewModel.conversations.observe(viewLifecycleOwner) { list ->
                fetchData(list)
            }
        }
    }

}