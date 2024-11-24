package com.te.celer.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.te.celer.R
import com.te.celer.db.AuthViewModel
import com.te.celer.db.DBViewModel
import com.te.celer.main_files.adapters.ContactAdapter
import com.te.celer.main_files.models.ContactItems
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentSnapshot
import com.te.celer.db.LocalStorage

class Send : Fragment() {

    private lateinit var phoneEditText: TextInputEditText
    private lateinit var submitBtn: CardView
    private val validPhoneNumberPattern by lazy { "^(\\+\\d{1,3}[- ]?)?\\d{10}\$" }
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var whiteView: View
    private lateinit var loaderSend: LottieAnimationView
    private lateinit var phoneNum: String
    private lateinit var backBtn: ImageView
    private var contactItemsArray = arrayListOf<ContactItems>()
    private lateinit var contactAdapter: ContactAdapter
    private var contactItems = arrayListOf<ContactItems>()
    private lateinit var recyclerview: RecyclerView
    private lateinit var contactLayout: LinearLayout
    private val localStorage = LocalStorage()

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility", "ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_send, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        loadData()

        phoneEditText = view.findViewById(R.id.phoneEditText_send)
        submitBtn = view.findViewById(R.id.submit_btn_send)
        whiteView = view.findViewById(R.id.whiteView_send)
        loaderSend = view.findViewById(R.id.loader_send)
        backBtn = view.findViewById(R.id.back_btn_send)
        recyclerview = view.findViewById(R.id.contact_recyclerview_send)
        contactLayout = view.findViewById(R.id.contacts_layout)

        contactAdapter = ContactAdapter(requireContext(), contactItems)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.setHasFixedSize(true)
        recyclerview.setItemViewCacheSize(20)
        recyclerview.adapter = contactAdapter

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dbViewModel.contactDetails.observe(viewLifecycleOwner) {
                    if (s != null) {
                        fetchData(it!!, s)
                    }
                }
            }
        })

        submitBtn.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loaderSend.visibility = View.VISIBLE
            val phone = phoneEditText.text.toString()
            if ((phone.length != 10) or !phone.matches(validPhoneNumberPattern.toRegex())) {
                whiteView.visibility = View.GONE
                loaderSend.visibility = View.GONE
                phoneEditText.error = "Enter 10 digits valid phone number"
                Toast.makeText(
                    requireContext(),
                    "Enter 10 digits valid phone number",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (phone == phoneNum) {
                whiteView.visibility = View.GONE
                loaderSend.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "You can't send money to your own number, try with different number",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val bundle = Bundle()
                bundle.putString("walletId", "$phone@smart")
                whiteView.visibility = View.GONE
                loaderSend.visibility = View.GONE
                Navigation.findNavController(view).navigate(R.id.nav_final_pay, bundle)
            }
        }

        phoneEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (phoneEditText.text!!.isEmpty()) {
                    phoneEditText.hint = "0000000000"
                }
            } else {
                phoneEditText.hint = null
            }
        }

        return view
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>, s: CharSequence) {
        contactLayout.visibility = View.GONE
        contactItemsArray = arrayListOf()
        if(list.isEmpty()) {
            contactLayout.visibility = View.GONE
        } else {
            contactLayout.visibility = View.VISIBLE
            for (i in list) {
                if (i.exists() and i.getString("phone")!!.contains(s)) {
                    contactLayout.visibility = View.VISIBLE
                    val contactData = ContactItems(
                        i.getString("name"),
                        i.getString("phone"),
                        i.getString("image_url")
                    )
                    contactItemsArray.add(contactData)
                }
            }
            contactLayout.visibility = View.VISIBLE
            contactAdapter.updateContact(contactItemsArray)
        }
    }

    private fun loadData() {
        val userdata = localStorage.getData(requireContext(), "user_data")
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                dbViewModel.fetchAccountDetails(it.uid)
                dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
                    if (list.exists()) {
                        phoneNum = list.getString("phone").toString()
                    }
                }
                dbViewModel.fetchContacts(it.uid)
            }
        }
    }
}