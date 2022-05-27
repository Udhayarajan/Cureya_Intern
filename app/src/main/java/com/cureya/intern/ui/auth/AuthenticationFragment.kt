package com.cureya.intern.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cureya.intern.firebase.FirebaseViewModel
import com.cureya.intern.databinding.FragmentAuthenticationBinding
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AuthenticationFragment : Fragment() {

    private val TAG: String = "AuthenticationFragment"
    private var _binding: FragmentAuthenticationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUp.setOnClickListener {
            val signUpDirection = SignUpFragmentDirections.actionGlobalSignUpFragment()
            findNavController().navigate(signUpDirection)
        }

        binding.login.setOnClickListener {
            lifecycleScope.launch {
                if (isInputValid())
                    viewModel.login(binding.email.text.toString(), binding.password.text.toString())
                        .collect {
                            if (it.isSuccess) {
                                requireActivity().onBackPressed()
                            } else {
                                it.exceptionOrNull()?.let { error ->
                                    Log.e(TAG, "onViewCreated: ", error)
                                    when (error) {
                                        is FirebaseAuthInvalidCredentialsException -> {
                                            binding.password.error = "Invalid Credentials"
                                        }
                                        is FirebaseAuthInvalidUserException -> {
                                            binding.email.error = "Invalid Email"
                                        }
                                    }
                                }
                            }
                        }
            }
        }
    }

    private fun isInputValid(): Boolean {
        binding.apply {
            if (email.text.toString().isEmpty()) {
                email.error = "Email is required"
                return false
            }
            if (password.text.toString().isEmpty()) {
                password.error = "Password is required"
                return false
            }
            return viewModel.isEmailFormedCorrectly(email.text.toString()).also {
                if (!it) email.error = "Invalid email"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}