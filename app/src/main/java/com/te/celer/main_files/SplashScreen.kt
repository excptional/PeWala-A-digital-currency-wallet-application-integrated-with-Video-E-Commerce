package com.te.celer.main_files

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.te.celer.R
import com.te.celer.auth.AuthenticationActivity
import com.te.celer.db.AuthViewModel
import com.te.celer.db.DBViewModel
import com.te.celer.db.LocalStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val dbViewModel: DBViewModel by viewModels()
    private val localStorage = LocalStorage()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        loadData()

    }

    private fun loadData() {
        authViewModel.userdata.observe(this) { user ->
            if (user == null) {
                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
            } else {
                dbViewModel.fetchAccountDetails(user.uid)
                dbViewModel.accDetails.observe(this) { details ->
                    val retrieveData = localStorage.getData(this@SplashScreen,"user_data")
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
                        localStorage.saveData(this@SplashScreen,"user_data", userdata)
                    }
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                }
            }
        }
    }

}