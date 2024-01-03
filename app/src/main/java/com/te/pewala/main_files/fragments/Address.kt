package com.te.pewala.main_files.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.db.Response
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText

class Address : Fragment() {

    private lateinit var area: TextInputEditText
    private lateinit var city: TextInputEditText
    private lateinit var postalCode: TextInputEditText
    private lateinit var landmark: TextInputEditText
    private lateinit var state: TextInputEditText
    private lateinit var useCurrentLocationBtn: CardView
    private lateinit var saveBtn: CardView
    private lateinit var backBtn: ImageButton
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var whiteView: View
    private lateinit var loader: LottieAnimationView
    private var flag = false
    private lateinit var localityStr: String
    private lateinit var cityStr: String
    private lateinit var postalCodeStr: String
    private lateinit var stateStr: String
    private lateinit var landmarkStr: String
    private lateinit var uid: String

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1234
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_address, container, false)

        area = view.findViewById(R.id.areaName_address)
        city = view.findViewById(R.id.cityName_address)
        postalCode = view.findViewById(R.id.postal_address)
        landmark = view.findViewById(R.id.landmark_address)
        state = view.findViewById(R.id.state_address)
        useCurrentLocationBtn = view.findViewById(R.id.use_current_location_btn_address)
        saveBtn = view.findViewById(R.id.save_btn_address)
        backBtn = view.findViewById(R.id.back_btn_address)
        whiteView = view.findViewById(R.id.whiteView_address)
        loader = view.findViewById(R.id.loader_address)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        load()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        checkAndTurnOnGPS()

        createLocationRequest()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult
                for (location in locationResult.locations) {
                    updateUI(location)
                }
            }
        }

        useCurrentLocationBtn.setOnClickListener {

            flag = false
            whiteView.visibility = View.VISIBLE
            loader.visibility = View.VISIBLE

            super.onStart()
            if (checkLocationPermission()) {
                startLocationUpdates()
            } else {
                requestLocationPermission()
            }

        }

        backBtn.setOnClickListener {
//            Navigation.findNavController(view).popBackStack()
            requireActivity().onBackPressed()
        }

        saveBtn.setOnClickListener {
            saveAddress(view)
        }

        return view
    }

    private fun saveAddress(view: View) {
        whiteView.visibility = View.VISIBLE
        loader.visibility = View.VISIBLE
        localityStr = area.text.toString()
        cityStr = city.text.toString()
        postalCodeStr = postalCode.text.toString()
        stateStr = state.text.toString()
        landmarkStr = landmark.text.toString()

        var isAllRight = true

        if(localityStr.isEmpty()) {
            isAllRight = false
            area.error = "Enter your location"
        }

        if(cityStr.isEmpty()) {
            isAllRight = false
            city.error = "Enter your city"
        }

        if(postalCodeStr.length != 6) {
            isAllRight = false
            postalCode.error = "Enter valid Postal Code"
        }

        if(stateStr.isEmpty()) {
            isAllRight = false
            state.error = "Enter your state"
        }

        if(!isAllRight) {
            Toast.makeText(requireContext(), "Enter proper location", Toast.LENGTH_SHORT).show()
            whiteView.visibility = View.GONE
            loader.visibility = View.GONE
        } else {
            dbViewModel.saveAddress(localityStr, cityStr, postalCodeStr, stateStr, landmarkStr, uid)
            dbViewModel.dbResponse.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Success -> {
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Address saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()
//                        Navigation.findNavController(view).popBackStack()
//                        Navigation.findNavController(view).navigate(R.id.nav_final_order_place)
                        requireActivity().onBackPressed()
                    }

                    is Response.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            it.errorMassage,
                            Toast.LENGTH_SHORT
                        ).show()
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                    }
                }
            }
        }
    }

//    override fun onStart() {
//        super.onStart()
//        if (checkLocationPermission()) {
//            startLocationUpdates()
//        } else {
//            requestLocationPermission()
//        }
//    }
//
    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 1000
//            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun updateUI(location: Location) {
        getCurrentLocation(location.latitude, location.longitude)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentLocation(lat: Double, long: Double) {
        val geocoder = Geocoder(requireContext())
        val addressList: MutableList<Address>
        try {
            addressList = geocoder.getFromLocation(lat, long, 1)!!
            if (addressList.isNotEmpty()) {
                val address = addressList[0]
//                val sb = StringBuilder()
//                for (i in 0 until address.maxAddressLineIndex) {
//                    sb.append(address.getAddressLine(i)).append("\n")
//                }
//                if (address.premises != null)
//                    sb.append(address.premises).append(", ")

                if(!flag) {
                    area.setText(address.subLocality)
                    city.setText(address.locality)
                    postalCode.setText(address.postalCode)
                    state.setText(address.adminArea)
                    flag = true
                }

                whiteView.visibility = View.GONE
                loader.visibility = View.GONE

//                sb.append(address.subLocality).append(", ")
//                sb.append(address.locality).append(", ")
//                sb.append(address.adminArea).append(", ")
//                sb.append(address.countryName).append(", ")
//                sb.append(address.postalCode)

            }

        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAndTurnOnGPS() {
        val locationManager =
            requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun load() {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if(it != null) uid = it.uid
        }
    }
}
