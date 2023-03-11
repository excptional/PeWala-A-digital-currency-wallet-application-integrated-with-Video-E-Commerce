package com.example.trigeredgedigitalcurrencyproject

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.trigeredgedigitalcurrencyproject.auth.AuthenticationActivity
import com.example.trigeredgedigitalcurrencyproject.databinding.ActivityMainBinding
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        authViewModel.userdata.observe(this) {
            if(it == null) {
                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
            }
        }

        window.statusBarColor = Color.parseColor("#4FC4D9E6")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        binding.fab.setOnClickListener {

        }

        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            val navController = findNavController(R.id.nav_host_fragment)
            when (item.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.nav_home)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.account-> {
                    navController.navigate(R.id.nav_account)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.redeem_req -> {
                    navController.navigate(R.id.nav_redeem_request)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.history -> {
                    navController.navigate(R.id.nav_history)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }
}