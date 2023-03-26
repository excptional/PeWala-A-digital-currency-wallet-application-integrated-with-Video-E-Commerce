package com.example.trigeredgedigitalcurrencyproject.main_files

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.auth.AuthenticationActivity
import com.example.trigeredgedigitalcurrencyproject.databinding.ActivityMainBinding
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel


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
            navController.navigate(R.id.nav_qr_scanner)
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

}