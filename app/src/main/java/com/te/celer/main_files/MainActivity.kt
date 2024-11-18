package com.te.celer.main_files

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.te.celer.R
import com.te.celer.auth.AuthenticationActivity
import com.te.celer.databinding.ActivityMainBinding
import com.te.celer.db.AuthViewModel
import com.te.celer.db.DBViewModel
import com.te.celer.db.LocalStorage
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val dbViewModel: DBViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor
    private lateinit var uid: String
    private lateinit var userName: String
    private val localStorage = LocalStorage()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#F7F9FD")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        navController = findNavController(R.id.nav_host_fragment)

        loadData()

//        binding.qrScanner.setOnClickListener {
////            Toast.makeText(this, "This feature is not implemented yet", Toast.LENGTH_SHORT).show()
//            navController.navigate(R.id.nav_qr_scanner)
////            navController.navigate(R.id.nav_qr_scanner)
//        }

        if (intent.getStringExtra("fragmentToLoad") == "Send") {
            navController.navigate(R.id.nav_history_)
        }

        if (intent.getStringExtra("orderPlaced") == "order") {
            navController.popBackStack(R.id.nav_home, false);
            navController.navigate(R.id.nav_orders)
        }


//        checkDeviceHasBiometric()
//        executor = ContextCompat.getMainExecutor(this)
//        biometricPrompt = BiometricPrompt(this, executor,
//            object : BiometricPrompt.AuthenticationCallback() {
//                override fun onAuthenticationError(
//                    errorCode: Int,
//                    errString: CharSequence
//                ) {
//                    super.onAuthenticationError(errorCode, errString)
//                    Toast.makeText(this@MainActivity, "Application is closing", Toast.LENGTH_SHORT)
//                        .show()
//                    Handler().postDelayed({
//                        finish()
//                        exitProcess(-1)
//                    }, 1000)
//                }
//
//                override fun onAuthenticationSucceeded(
//                    result: BiometricPrompt.AuthenticationResult
//                ) {
//                    super.onAuthenticationSucceeded(result)
//                    Toast.makeText(this@MainActivity, "Authentication succeed", Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//                override fun onAuthenticationFailed() {
//                    super.onAuthenticationFailed()
//                    Toast.makeText(
//                        this@MainActivity, "Authentication failed",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            })

//        promptInfo = BiometricPrompt.PromptInfo.Builder()
//            .setTitle("Unlock Trigredge")
//            .setSubtitle("Enter phone screen lock pattern, PIN, password or fingerprint")
//            .setDeviceCredentialAllowed(true)
//            .setConfirmationRequired(true)
//            .build()
//
//        biometricPrompt.authenticate(promptInfo)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if ((destination.id == R.id.nav_home) or (destination.id == R.id.nav_account) or (destination.id == R.id.nav_history) or (destination.id == R.id.nav_redeem_request)) {
                binding.bottomNav.visibility = View.VISIBLE
                binding.bottomAppbar.visibility = View.VISIBLE
//                binding.qrScanner.visibility = View.VISIBLE
            } else {
                binding.bottomNav.visibility = View.GONE
                binding.bottomAppbar.visibility = View.GONE
//                binding.qrScanner.visibility = View.GONE
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

//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                if (binding.bottomNav.isVisible) {
//                    val selectedItemId = binding.bottomNav.selectedItemId
//                    when (selectedItemId) {
//                        R.id.home -> {
//                            finish()
//                        }
//                        else -> {
//                            navController.navigate(R.id.nav_home)
//                            binding.bottomNav.selectedItemId = R.id.home
//                        }
//                    }
//                } else {
//                    // No action or perform default back press behavior (optional)
//                }
//            }
//        }
//
//        this.onBackPressedDispatcher.addCallback(this, callback)

        onBackButtonPressed {
            if (binding.bottomNav.isVisible) {
                false
            } else true

        }

    }

    @SuppressLint("SwitchIntDef")
    private fun checkDeviceHasBiometric() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Toast.makeText(this, "App can authenticate using biometrics.", Toast.LENGTH_SHORT)
                    .show()

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Toast.makeText(
                    this,
                    "No biometric features available on this device.",
                    Toast.LENGTH_SHORT
                ).show()

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {}
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                            )
                        }
                    }
                } else {
                    TODO("VERSION.SDK_INT < R")
                }
            }
        }
    }

//    private fun videoCallServices() {
//        val appID: Long = getString(R.string.ZEGOCLOUD_APP_ID).toLong()
//        val appSign: String = getString(R.string.ZEGOCLOUD_APP_SIGN)
//
//        val callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()
//
////        callInvitationConfig.incomingCallRingtone = "res/raw/incomingcall.mp3"
////        callInvitationConfig.outgoingCallRingtone = "res/raw/outgoingcall.mp3"
//
//        ZegoUIKitPrebuiltCallService.init(
//            application,
//            appID,
//            appSign,
//            uid,
//            userName,
//            callInvitationConfig
//        )
//    }

    private fun loadData() {
        authViewModel.userdata.observe(this) { user ->
            if (user == null) {
                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
            } else {
                uid = user.uid
                dbViewModel.fetchAccountDetails(user.uid)
                dbViewModel.accDetails.observe(this) { details ->
                    val retrieveData = localStorage.getData(this@MainActivity,"user_data")
                    if (retrieveData == null || retrieveData["image_url"] != details.getString("image_url")
                        || retrieveData["balance"] != details.getString("balance")
                    ) {
                        val userdata = mapOf(
                            "uid" to user.uid,
                            "name" to details.getString("name")!!,
                            "phone" to details.getString("phone")!!,
                            "card_id" to details.getString("card_id")!!,
                            "user_type" to details.getString("user_type")!!,
                            "image_url" to details.getString("image_url")!!,
                            "pin" to details.getString("pin")!!,
                            "qr_code" to details.getString("qr_code")!!,
                            "balance" to details.getString("balance")!!
                        )
                        localStorage.saveData(this@MainActivity,"user_data", userdata)
                    }
                    userName = details.getString("name")!!
//                    videoCallServices()
                }
            }
        }
    }

    private fun onBackButtonPressed(callback: (() -> Boolean)) {
        (this as? FragmentActivity)?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentDestination = navController.currentDestination

                if (!callback()) {
                    if (currentDestination?.id == R.id.nav_home) {
                        finish()
                    } else {
                        navController.navigate(R.id.nav_home)
                        binding.bottomNav.selectedItemId = R.id.home
                    }
                } else {
                    remove()
                    performBackPress()
                }

//                if (!callback()) {
//                    val selectedItemId = binding.bottomNav.selectedItemId
//                    when (selectedItemId) {
//                        R.id.home -> {
//                            finish()
//                        }
//                        else -> {
//                            navController.navigate(R.id.nav_home)
//                            binding.bottomNav.selectedItemId = R.id.home
//                        }
//                    }
//                } else {
//                    remove()
//                    performBackPress()
//                }
            }
        })
    }

    fun performBackPress() {
        (this as? FragmentActivity)?.onBackPressedDispatcher?.onBackPressed()
    }

//    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
//    override fun onBackPressed() {
//        if(binding.bottomNav.isVisible) {
//            val selectedItemId = binding.bottomNav.selectedItemId
//            when (selectedItemId) {
//                R.id.home -> {
//                    super.finish()
//                }
//                else -> {
//                    navController.navigate(R.id.nav_home)
//                    binding.bottomNav.selectedItemId = R.id.home
//                }
//            }
//        } else super.onBackPressed()
//    }

}