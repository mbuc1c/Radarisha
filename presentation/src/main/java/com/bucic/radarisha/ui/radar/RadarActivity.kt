package com.bucic.radarisha.ui.radar

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bucic.domain.util.Result
import com.bucic.radarisha.R
import com.bucic.radarisha.databinding.ActivityRadarBinding
import com.bucic.radarisha.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RadarActivity : AppCompatActivity() {

    private val viewModel: RadarViewModel by viewModels()
    private lateinit var binding: ActivityRadarBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRadarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.findNavController()

        setupMenu()

        getCurrentUser()

        lifecycleScope.launch {
            viewModel.currentUser.collectLatest { result ->
                if (result is Result.Success) {
                    if (result.data.stayLoggedIn == false) {
                        viewModel.removeCurrentUser()
                    }
                }
            }
        }
    }

    private fun getCurrentUser() = viewModel.getCurrentUser()

    private fun setupMenu() {
        with(binding) {
            topAppBar.setupWithNavController(navController)
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.logOut -> logOut()
                    else -> false
                }
            }
        }
    }

    private fun logOut(): Boolean {
        viewModel.removeCurrentUser()
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }
}