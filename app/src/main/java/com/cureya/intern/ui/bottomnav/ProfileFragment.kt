package com.cureya.intern.ui.bottomnav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.cureya.intern.firebase.FirebaseViewModel
import com.cureya.intern.UserDetail
import com.cureya.intern.databinding.FragmentProfileBinding
import com.cureya.intern.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.isLoggedIn()){
            (requireActivity() as MainActivity).showLoadingDialog("Loading Profile")
        }
        lifecycleScope.launch {
            viewModel.getCurrentProfile().collect {
                (requireActivity() as MainActivity).dismissDialog()
                bind(it)
            }
        }

        binding.signOut.setOnClickListener {
            (requireActivity() as MainActivity).showLoadingDialog("Please wait....")
            lifecycleScope.launch {
                viewModel.signOut().collect {
                    (requireActivity() as MainActivity).dismissDialog()
                    if (it.isSuccess) {
                        requireActivity().recreate()
                    } else {
                        Snackbar.make(binding.root,
                            "Unable to sign out",
                            Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun bind(it: UserDetail){
        binding.apply {
            userName.text = it.name
            userEmail.text = it.email
            userAge.text = it.age
            userCity.text = it.city
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as MainActivity).dismissDialog()
    }

}