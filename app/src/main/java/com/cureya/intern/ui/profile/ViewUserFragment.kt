package com.cureya.intern.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.cureya.intern.firebase.FirebaseViewModel
import com.cureya.intern.UserDetail
import com.cureya.intern.databinding.FragmentViewUserBinding
import com.cureya.intern.ui.MainActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class ViewUserFragment : Fragment() {

    private lateinit var binding: FragmentViewUserBinding
    private val viewModel: FirebaseViewModel by activityViewModels()
    private val args: ViewUserFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentViewUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).showLoadingDialog("Loading Profile")
        lifecycleScope.launch {
            viewModel.getProfile(args.id).collect {
                (requireActivity() as MainActivity).dismissDialog()
                bind(it)
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