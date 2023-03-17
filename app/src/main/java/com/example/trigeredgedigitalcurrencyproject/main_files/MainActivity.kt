package com.example.trigeredgedigitalcurrencyproject.main_files

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.auth.AuthenticationActivity
import com.example.trigeredgedigitalcurrencyproject.databinding.ActivityMainBinding
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        navController = findNavController(R.id.nav_host_fragment)

        authViewModel.userdata.observe(this) {
            if(it == null) {
                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
            }
        }

        window.statusBarColor = Color.parseColor("#4FC4D9E6")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding.qrScanner.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.setPrompt("")
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            intentIntegrator.setOrientationLocked(true)
            intentIntegrator.setCameraId(0)
            intentIntegrator.captureActivity = CaptureActivity::class.java
            intentIntegrator.setBeepEnabled(false)
            intentIntegrator.initiateScan()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if((destination.id == R.id.nav_home) or (destination.id == R.id.nav_account) or (destination.id == R.id.nav_history) or (destination.id == R.id.nav_redeem_request)) {
                binding.bottomNav.visibility = View.VISIBLE
                binding.bottomAppbar.visibility = View.VISIBLE
                binding.qrScanner.visibility = View.VISIBLE
            } else {
                binding.bottomNav.visibility = View.GONE
                binding.bottomAppbar.visibility = View.GONE
                binding.qrScanner.visibility = View.GONE
            }
        }

        binding.bottomNav.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> {
                    navController.popBackStack()
                    navController.navigate(R.id.nav_home)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.account -> {
                    navController.popBackStack()
                    navController.navigate(R.id.nav_account)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.redeem_req -> {
                    navController.popBackStack()
                    navController.navigate(R.id.nav_redeem_request)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.history -> {
                    navController.popBackStack()
                    navController.navigate(R.id.nav_history)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                val bundle = Bundle()
                bundle.putString("walletId", intentResult.contents)
                navController.navigate(R.id.nav_send, bundle)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}