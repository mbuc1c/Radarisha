package com.bucic.radarisha.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bucic.radarisha.R
import com.bucic.radarisha.ui.auth.AuthActivity
import com.bucic.radarisha.ui.radar.RadarActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashTheme)
        super.onCreate(savedInstanceState)

        getCurrentUser()

        lifecycleScope.launch {
            viewModel.isLoggedIn.collectLatest { isLoggedIn ->
                if (isLoggedIn) {
                    startActivity(Intent(this@SplashActivity, RadarActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun getCurrentUser() {
        viewModel.getCurrentUser()
    }
}