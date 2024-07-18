package com.bucic.radarisha.ui.auth.registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bucic.radarisha.R
import com.bucic.radarisha.databinding.FragmentRegistrationBinding
import dagger.hilt.android.AndroidEntryPoint

// TODO: add username already exists error message
@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private val viewModel: RegistrationViewModel by viewModels()
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            if (validateInput()) {
                val username = binding.etTextFieldUsername.text.toString()
                val password = binding.etTextFieldPassword.text.toString()
                viewModel.createUser(username, password)
                findNavController().navigateUp()
            } else displayErrorMessages()
        }
    }

    private fun displayErrorMessages() {
        with(binding) {
            if (!isUsernameLengthValid()) {
                textFieldUsername.error = getString(R.string.error_username_length)
            } else textFieldUsername.error = null
            if (!isPasswordLengthValid()) {
                textFieldPassword.error = getString(R.string.error_password_length)
            } else textFieldPassword.error = null
            if (!isPasswordRepeated()) {
                textFieldRepeatPassword.error = getString(R.string.error_password_repeat)
            } else textFieldRepeatPassword.error = null
        }
    }

    private fun validateInput(): Boolean = isUsernameLengthValid() && isPasswordLengthValid() && isPasswordRepeated()

    private fun isUsernameLengthValid(): Boolean = binding.etTextFieldUsername.text.toString().length >= 2

    private fun isPasswordLengthValid(): Boolean = binding.etTextFieldPassword.text.toString().length >= 8

    private fun isPasswordRepeated(): Boolean = binding.etTextFieldPassword.text.toString() == binding.etTextFieldRepeatPassword.text.toString()


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}