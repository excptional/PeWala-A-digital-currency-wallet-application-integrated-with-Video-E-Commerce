package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.main_files.adapters.ContactAdapter
import com.te.pewala.main_files.items.ContactItems
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentSnapshot

class Send : Fragment() {

    private lateinit var phoneEditText: TextInputEditText
    private lateinit var submitBtn: CardView
    private val validPhoneNumberPattern by lazy { "^(\\+\\d{1,3}[- ]?)?\\d{10}\$" }
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var whiteView: View
    private lateinit var loaderSend: LottieAnimationView
    private lateinit var phoneNum: String
    private lateinit var backBtn: ImageButton
    private var contactItemsArray = arrayListOf<ContactItems>()
    private lateinit var contactAdapter: ContactAdapter
    private var contactItems = arrayListOf<ContactItems>()
    private lateinit var recyclerview: RecyclerView
    private lateinit var contactLayout: LinearLayout
    private var iconUrl = "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/user2.png?alt=media&token=91a4d9d4-71cc-4d25-919b-eed55ff51842"

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_send, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        loadData()

        phoneEditText = view.findViewById(R.id.phoneEdiText_send)
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
                        fetchData(it, s)
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
                bundle.putString("walletId", "$phone@digital")
                whiteView.visibility = View.GONE
                loaderSend.visibility = View.GONE
                Navigation.findNavController(view).navigate(R.id.nav_final_pay, bundle)
            }
        }
        return view
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>, s: CharSequence) {
        contactLayout.visibility = View.GONE
        contactItemsArray = arrayListOf()
        for (i in list) {
            if (i.exists() and i.getString("Phone No")!!.contains(s)) {
                contactLayout.visibility = View.VISIBLE
                val contactData = ContactItems(
                    i.getString("Name"),
                    i.getString("Phone No"),
                    i.getString("Image Url")
                )
                contactItemsArray.add(contactData)
            }
        }
        contactLayout.visibility = View.VISIBLE
        contactAdapter.updateContact(contactItemsArray)
    }

    private fun loadData() {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                dbViewModel.fetchAccountDetails(it.uid)
                dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
                    if (list.exists()) {
                        phoneNum = list.getString("Phone").toString()
                    }
                }
                dbViewModel.fetchContacts(it.uid)
            }
        }
    }
}