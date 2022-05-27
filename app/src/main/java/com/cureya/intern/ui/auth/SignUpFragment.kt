package com.cureya.intern.ui.auth

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cureya.intern.firebase.FirebaseViewModel
import com.cureya.intern.R
import com.cureya.intern.databinding.FragmentSignUpBinding
import com.cureya.intern.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private val viewModel: FirebaseViewModel by activityViewModels()

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.showPass.setOnClickListener {
            showHidePassword(it)
        }
        binding.showPassCon.setOnClickListener {
            showHidePassword(it)
        }
        return binding.root
    }

    private fun showHidePassword(view: View) {
        if (view.id == R.id.show_pass) {
            if (binding.pass.transformationMethod.equals(PasswordTransformationMethod.getInstance())) {
                (view as ImageView).setImageResource(R.drawable.ic_hide_pass)
                binding.pass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                (view as ImageView).setImageResource(R.drawable.ic_pass_show)
                binding.pass.transformationMethod = PasswordTransformationMethod.getInstance();
            }
        }else if (view.id == R.id.show_pass_con) {
            if (binding.confirmPass.transformationMethod.equals(PasswordTransformationMethod.getInstance())) {
                (view as ImageView).setImageResource(R.drawable.ic_hide_pass)
                binding.confirmPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                (view as ImageView).setImageResource(R.drawable.ic_pass_show)
                binding.confirmPass.transformationMethod = PasswordTransformationMethod.getInstance();
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSignUp.setOnClickListener {
            if (validateInput()) {
                (requireActivity() as MainActivity).showLoadingDialog("Signing up...")
                lifecycleScope.launch {
                    viewModel.signUp(
                        binding.email.text.toString(),
                        binding.pass.text.toString(),
                    ).collect {
                        it?.let { result ->
                            (requireActivity() as MainActivity).dismissDialog()
                            if (result.isSuccess) {
                                Snackbar.make(binding.root, "Sign up successful", Snackbar.LENGTH_SHORT)
                                    .show()
                                binding.apply {
                                    viewModel.saveUser(
                                        result.getOrNull()!!.uid,
                                        email.text.toString(),
                                        name.text.toString(),
                                        city.text.toString(),
                                        age.text.toString(),
                                    ).collect {
                                        findNavController().popBackStack()
                                        requireActivity().onBackPressed()
                                    }
                                }
                            } else {
                                Snackbar.make(binding.root, "Sign up failed", Snackbar.LENGTH_SHORT)
                                    .show()
                                val error = result.exceptionOrNull()
                                if (error is FirebaseAuthUserCollisionException){
                                    binding.email.error = "Email already exists"
                                }
                                if (error is FirebaseAuthWeakPasswordException){
                                    binding.pass.error = "Password is too weak or small"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        binding.apply {
            if (email.text.isNullOrEmpty()) {
                email.error = "Email is required"
                return false
            }
            if (pass.text.isNullOrEmpty()) {
                pass.error = "Password is required"
                return false
            }
            if (confirmPass.text.isNullOrEmpty()) {
                pass.error = "Confirm password is required"
                return false
            }
            if (pass.text.toString() != confirmPass.text.toString()) {
                confirmPass.error = "Password does not match"
                return false
            }
            if (!viewModel.isEmailFormedCorrectly(email.text.toString())){
                email.error = "Invalid mail ID"
                return false
            }
        }
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}