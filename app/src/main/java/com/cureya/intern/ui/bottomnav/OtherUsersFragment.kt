package com.cureya.intern.ui.bottomnav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cureya.intern.firebase.FirebaseViewModel
import com.cureya.intern.ui.adapter.UsersAdapter
import com.cureya.intern.databinding.FragmentOtherUsersBinding
import com.cureya.intern.ui.profile.ViewUserFragmentDirections
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OtherUsersFragment : Fragment() {

    private lateinit var binding: FragmentOtherUsersBinding

    private val viewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentOtherUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = UsersAdapter {
            val action = ViewUserFragmentDirections.actionGlobalViewUserFragment(it.id)
            findNavController().navigate(action)
        }

        fun fetchData() {
            binding.swipeRefreshLayout.isRefreshing = true
            lifecycleScope.launch {
                viewModel.getCollection().collect {
                    binding.swipeRefreshLayout.isRefreshing = false
                    adapter.submitList(it)
                }
            }
        }

        fetchData()
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchData()
        }

        binding.otherUsersRecyclerView.adapter = adapter


    }


}