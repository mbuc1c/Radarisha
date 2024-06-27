package com.bucic.radarisha.ui.auth.login

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.util.Result
import com.bucic.radarisha.R
import com.bucic.radarisha.databinding.FragmentLoginBinding
import com.bucic.radarisha.ui.radar.RadarActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// TODO: which way of navigation from one activity to other is more suitable?
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnNavigateToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }

        binding.btnLogin.setOnClickListener {
//            resetResultStateFlow()
            viewModel.getUserByUsernameAndPassword(
                binding.etTextFieldUsername.text.toString(),
                binding.etTextFieldPassword.text.toString()
            )
//            findNavController().navigate(R.id.action_loginFragment_to_radarActivity)
        }

        lifecycleScope.launch {
            viewModel.userResult.collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        binding.textFieldUsername.error = null
                        binding.textFieldPassword.error = null
                        Log.d("customTag", result.data.toString())
                        rememberUser(result.data)
//                        findNavController().navigate(R.id.action_loginFragment_to_radarActivity)
                        navigateToRadarActivity()
                    }
                    is Result.Error -> {
                        binding.textFieldUsername.error = " "
                        binding.textFieldPassword.error = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    private fun navigateToRadarActivity() {
        val intent = Intent(requireContext(), RadarActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun resetResultStateFlow() {
        viewModel.resetResultStateFlow()
        Log.d("customTag", "${viewModel.userResult.value}")
    }

    private fun rememberUser(user: UserEntity) {
        if (binding.cbRememberMe.isChecked) {
            viewModel.saveCurrentUser(user)
        } else viewModel.removeCurrentUser()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}