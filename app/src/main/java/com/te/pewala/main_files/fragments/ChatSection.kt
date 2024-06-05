package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentSnapshot
import com.te.pewala.R
import com.te.pewala.db.AESCrypt
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.db.Response
import com.te.pewala.main_files.adapters.ChatAdapter
import com.te.pewala.main_files.items.MessageItems
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class ChatSection : Fragment() {

    private lateinit var chatAdapter: ChatAdapter
    private var chatItemsArray = arrayListOf<MessageItems>()
    private var dbViewModel: DBViewModel? = null
    private var authViewModel: AuthViewModel? = null
    private lateinit var backBtn: ImageButton
    private lateinit var receiverName: TextView
    private lateinit var msgET: TextInputEditText
    private lateinit var mic: ImageButton
    private lateinit var sendBtn: ImageButton
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var receiverUid: String
    private lateinit var uid: String
    private lateinit var cId: String
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var topLayout: LinearLayout
    private lateinit var userType: String
    private lateinit var mainLayout: RelativeLayout
    private lateinit var chatShimmer: ShimmerFrameLayout
    private val aesCrypt = AESCrypt()
    val key = ByteArray(32)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_section, container, false)

        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        backBtn = view.findViewById(R.id.back_btn_chat)
        receiverName = view.findViewById(R.id.receiver_name_chat)
        msgET = view.findViewById(R.id.msg_et_chat)
        mic = view.findViewById(R.id.mic_chat)
        sendBtn = view.findViewById(R.id.send_btn_chat)
        chatRecyclerView = view.findViewById(R.id.recycler_view_chat)
        refreshLayout = view.findViewById(R.id.swipeRefreshLayout_chat)
        backBtn = view.findViewById(R.id.back_btn_chat)
        topLayout = view.findViewById(R.id.top_layout_chat)
        mainLayout = view.findViewById(R.id.main_layout_chat)
        chatShimmer = view.findViewById(R.id.shimmer_chat)

        chatShimmer.startShimmer()

        receiverUid = requireArguments().getString("receiverUid")!!

        requireActivity().window.statusBarColor = Color.WHITE

        key.fill(1)
        chatAdapter = ChatAdapter(requireContext(), this, chatItemsArray, key)
        chatRecyclerView.layoutManager = LinearLayoutManager(view.context)
        chatRecyclerView.setHasFixedSize(true)
        chatRecyclerView.setItemViewCacheSize(20)
        chatRecyclerView.adapter = chatAdapter

        loadData()

        refreshLayout.setOnRefreshListener {
            chatShimmer.visibility = View.VISIBLE
            mainLayout.visibility = View.GONE
            dbViewModel!!.fetchMessages(cId)
            dbViewModel!!.chats.observe(viewLifecycleOwner) { msgList ->
                if (msgList.isNotEmpty()) {
                    fetchMessages(msgList)
                } else {
                    refreshLayout.isRefreshing = false
                }
            }
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        msgET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mic.visibility = if (s.isNullOrEmpty()) View.VISIBLE else View.GONE
                sendBtn.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })

        sendBtn.setOnClickListener {
            val msgString = msgET.text.toString()
            val timeString = System.currentTimeMillis().toString()
            sendMessage(msgString, timeString)
            msgET.text = null
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessage(msgString: String, timeString: String) {
        val msg = MessageItems(
            aesCrypt.encrypt(msgString, key),
            timeString,
            true,
            "Sending",
            cId
        )
        chatItemsArray.add(msg)
        chatAdapter.updateMessages(chatItemsArray)
        dbViewModel!!.isConversationPresent(uid, receiverUid)
        dbViewModel!!.isConversation.observe(viewLifecycleOwner)
        {
            when (it) {
                true -> {
                    dbViewModel!!.insertMessage(uid, receiverUid, msgString, timeString, cId)
                }

                false -> {
                    dbViewModel!!.createConversation(uid, receiverUid, msgString, timeString, cId)

                }

                else -> {}
            }

            if (chatAdapter.itemCount >= 1) {
                chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)

            }

            dbViewModel!!.fetchMessages(cId)
            dbViewModel!!.chats.observe(viewLifecycleOwner) { msgList ->
                if (msgList.isNotEmpty()) {
                    fetchMessages(msgList)
                } else {
                    chatRecyclerView.visibility = View.GONE
                }
            }
        }
    }

    private fun fetchMessages(list: ArrayList<DocumentSnapshot>) {
        chatItemsArray.clear()
        chatItemsArray = arrayListOf()
        val msg = MessageItems(
            "",
            System.currentTimeMillis().toString(),
            false,
            "",
            cId
        )
        chatItemsArray.add(msg)
        for (i in list) {
            if (i.exists()) {
                val msg = MessageItems(
                    i.getString("message"),
                    i.getString("time"),
                    i.getString("sender_uid").equals(uid),
                    i.getString("status"),
                    cId
                )
                chatItemsArray.add(msg)
            }
        }
        chatAdapter.updateMessages(chatItemsArray)
        chatRecyclerView.visibility = View.VISIBLE
        refreshLayout.isRefreshing = false
        topLayout.visibility = View.GONE
        chatShimmer.visibility = View.GONE
        mainLayout.visibility = View.VISIBLE
        chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
    }

    private fun loadData() {
        authViewModel!!.userdata.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                uid = user.uid
                dbViewModel!!.fetchAccountDetails(user.uid)
                dbViewModel!!.accDetails.observe(viewLifecycleOwner) { accDetail ->
                    userType = accDetail.getString("user_type")!!
                    if (userType == "Buyer") {
                        cId = "$uid:$receiverUid"
                    } else {
                        cId = "$receiverUid:$uid"
                    }
                    dbViewModel!!.fetchMessages(cId)
                    dbViewModel!!.chats.observe(viewLifecycleOwner) { msgList ->
                        if (msgList.isNotEmpty()) {
                            fetchMessages(msgList)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "There are no chats found",
                                Toast.LENGTH_SHORT
                            ).show()
                            mainLayout.visibility = View.VISIBLE
                            chatShimmer.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

}