package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel

class QRScanner : Fragment() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_q_r_scanner, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                loadData(it.text.toString())
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun loadData(walletId: String) {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                dbViewModel.fetchAccountDetails(it.uid)
                dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
                    if (list.exists()) {
                        if(walletId == list.getString("Card Id")) {
                            Toast.makeText(requireContext(), "You can't pay to your own account", Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressed()
                        } else {
                            val bundle = Bundle()
                            bundle.putString("walletId", walletId)
                            Navigation.findNavController(requireView()).popBackStack()
                            Navigation.findNavController(requireView()).navigate(R.id.nav_final_pay, bundle)
                        }
                    }
                }
            }
        }
    }

}