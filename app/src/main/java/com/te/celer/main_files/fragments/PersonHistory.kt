package com.te.celer.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.te.celer.R
import com.te.celer.db.AuthViewModel
import com.te.celer.db.DBViewModel
import com.te.celer.main_files.adapters.TransactionHistoryAdapter
import com.te.celer.main_files.models.TransactionHistoryItems
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.te.celer.db.LocalStorage
import de.hdodenhof.circleimageview.CircleImageView

class PersonHistory : Fragment() {

    private lateinit var transactionHistoryAdapter: TransactionHistoryAdapter
    private var transactionHistoryItems = arrayListOf<TransactionHistoryItems>()
    private lateinit var personalHistoryShimmer: ShimmerFrameLayout
    private lateinit var recyclerview: RecyclerView
    private lateinit var nothingFoundText: TextView
    private lateinit var mainLayout: LinearLayout
    private lateinit var name: TextView
    private lateinit var phone: TextView
    private lateinit var walletId: TextView
    private lateinit var profileImage: CircleImageView
    private lateinit var payAgain: CardView
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var payerUid: String
    private lateinit var backBtn: ImageView
    private lateinit var payerPhone: String
    private val localStorage = LocalStorage()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_person_history, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        payerUid = requireArguments().getString("payerUid").toString()

        personalHistoryShimmer = view.findViewById(R.id.personal_history_shimmer)
        recyclerview = view.findViewById(R.id.recyclerView_personal_history)
        mainLayout = view.findViewById(R.id.main_layout_person_history)
        nothingFoundText = view.findViewById(R.id.nothingFound_person_history)
        name = view.findViewById(R.id.name_person_history)
        phone = view.findViewById(R.id.phone_person_history)
        walletId = view.findViewById(R.id.walletId_person_history)
        profileImage = view.findViewById(R.id.profileImage_person_history)
        payAgain = view.findViewById(R.id.pay_again_person_history)
        backBtn = view.findViewById(R.id.back_btn_person_history)

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        payAgain.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("walletId", "$payerPhone@digital")
            Navigation.findNavController(requireView()).navigate(R.id.nav_final_pay, bundle)
        }

        personalHistoryShimmer.startShimmer()
        personalHistoryShimmer.visibility = View.VISIBLE
        mainLayout.visibility = View.GONE

        transactionHistoryAdapter =
            TransactionHistoryAdapter(requireContext(), transactionHistoryItems)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.setHasFixedSize(true)
        recyclerview.setItemViewCacheSize(20)
        recyclerview.adapter = transactionHistoryAdapter

        loadData()

        return view
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>) {
        transactionHistoryItems = arrayListOf()
        for (i in list) {
            if (i.exists() and i.getString("operator_id").equals(payerUid)) {
                val transactionData = TransactionHistoryItems(
                    i.getString("amount"),
                    i.getString("operation"),
                    i.getString("tid"),
                    i.getString("time"),
                    i.getString("operator_name"),
                    i.getString("operator_phone")
                )
                transactionHistoryItems.add(transactionData)
            }
        }
        transactionHistoryAdapter.updateTransactionHistory(transactionHistoryItems)
        personalHistoryShimmer.clearAnimation()
        personalHistoryShimmer.visibility = View.GONE
        mainLayout.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        val userdata = localStorage.getData(requireContext(), "user_data")
        authViewModel.userdata.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                dbViewModel.fetchAccountDetails(payerUid)
                dbViewModel.accDetails.observe(viewLifecycleOwner) {
                    name.text = it.getString("name")
                    payerPhone = it.getString("phone")!!
                    phone.text = "Ph : ${it.getString("phone")}"
                    walletId.text = "Wallet Id : ${it.getString("card_id")}"
                    Glide.with(requireView()).load(it.getString("image_url")).into(profileImage)
                    dbViewModel.fetchTransactionDetails(user.uid)
                    dbViewModel.transactionDetails.observe(viewLifecycleOwner) { list ->
                        if (list.isNotEmpty()) {
                            nothingFoundText.visibility = View.GONE
                            fetchData(list)
                            personalHistoryShimmer.visibility = View.GONE
                            mainLayout.visibility = View.VISIBLE
                            nothingFoundText.visibility = View.GONE
                        } else {
                            personalHistoryShimmer.visibility = View.GONE
                            mainLayout.visibility = View.GONE
                            nothingFoundText.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

}