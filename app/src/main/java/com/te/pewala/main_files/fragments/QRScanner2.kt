package com.te.pewala.main_files.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.Barcode.QR_CODE
import com.google.android.gms.vision.barcode.BarcodeDetector
//import kotlinx.android.synthetic.main.fragment_scanner.*

class QRScanner2 : Fragment() {

    private val CAMERA_PERMISSION_REQUEST = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var surfaceView: SurfaceView
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_q_r_scanner2, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        surfaceView = view.findViewById(R.id.surfaceView)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
//            createCameraSource()
        } else {
            createCameraSource()
        }

        return view
    }


    private fun createCameraSource() {
        val barcodeDetector = BarcodeDetector.Builder(requireContext())
            .setBarcodeFormats(QR_CODE)
            .build()

        if (!barcodeDetector.isOperational) {
            Toast.makeText(requireContext(), "Could not set up the barcode detector.", Toast.LENGTH_SHORT).show()
//            Log.e("ScannerFragment", "Could not set up the barcode detector.")
            return
        }

        cameraSource = CameraSource.Builder(requireContext(), barcodeDetector)
            .setAutoFocusEnabled(true)
            .build()

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource.start(surfaceView.holder)
                    } else {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.CAMERA),
                            CAMERA_PERMISSION_REQUEST
                        )
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() > 0) {
                    val barcode = barcodes.valueAt(0)
                    handleBarcode(barcode)
                }
            }
        })
    }

    private fun handleBarcode(barcode: Barcode) {
        requireActivity().runOnUiThread {
            // Handle the scanned code as needed (e.g., navigate to a new fragment or activity)
            // For now, just log the result
//            Log.d("ScannerFragment", "Scanned code: ${barcode.displayValue}")
            loadData(barcode.displayValue.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createCameraSource()
            } else {
                Log.e("ScannerFragment", "Camera permission denied")
                // Handle the case where camera permission is denied
            }
        }
    }

    private fun loadData(walletId: String) {
        authViewModel.userdata.observe(this) {
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


