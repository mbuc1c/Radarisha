package com.bucic.radarisha.ui.radar

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bucic.radarisha.R
import com.bucic.radarisha.databinding.ActivityRadarBinding
import com.bucic.radarisha.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RadarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRadarBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRadarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.findNavController()

        binding.topAppBar.setupWithNavController(navController)

        setupMenu()
    }
    // TODO: fix color change for logout button
    private fun setupMenu() {
        with(binding) {
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.logOut -> logOut()
                    else -> false
                }
            }
        }
    }

    private fun logOut(): Boolean {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }
}