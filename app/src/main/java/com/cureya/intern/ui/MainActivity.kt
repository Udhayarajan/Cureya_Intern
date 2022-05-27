package com.cureya.intern.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cureya.intern.firebase.FirebaseViewModel
import com.cureya.intern.R
import com.cureya.intern.databinding.ActivityMainBinding
import com.cureya.intern.databinding.LoadingDialogBinding
import com.cureya.intern.ui.auth.AuthenticationFragment
import com.cureya.intern.ui.auth.AuthenticationFragmentDirections
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FirebaseViewModel by viewModels()

    private var loadingDialog: AlertDialog? = null
    private lateinit var dialogBinding: LoadingDialogBinding

    private val currentFragment: Fragment?
        get() = supportFragmentManager
            .findFragmentById(R.id.fragment_container)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener(this)

        binding.bottomNavView.setupWithNavController(navController)

        if (!viewModel.isLoggedIn()) {
            gotoAuthFragment()
        }
    }

    private fun gotoAuthFragment() {
        val homeToAuth = AuthenticationFragmentDirections.actionGlobalAuthenticationFragment()
        navController.navigate(homeToAuth)
    }

    fun showLoadingDialog(msg: String = "") {
        if (loadingDialog == null) {
            dialogBinding = LoadingDialogBinding.inflate(layoutInflater)
            loadingDialog = MaterialAlertDialogBuilder(this).setView(dialogBinding.root)
                .setCancelable(false)
                .create()
        }
        dialogBinding.text.text = msg
        loadingDialog?.show()
    }

    fun dismissDialog() {
        loadingDialog?.dismiss()
    }

    override fun onBackPressed() {
        currentFragment?.let {
            if (it is AuthenticationFragment) {
                if (!viewModel.isLoggedIn())
                    finish()
            }
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?,
    ) {
        if (destination.id == R.id.authenticationFragment || destination.id == R.id.signUpFragment
            || destination.id == R.id.viewUserFragment
        ) {
            binding.bottomBar.visibility = View.GONE
            binding.bottomBar.performHide(true)
        } else {
            binding.bottomBar.visibility = View.VISIBLE
            binding.bottomBar.performShow(true)
        }
        if (destination.id == R.id.profileFragment || destination.id == R.id.otherUsersFragment) {
            navController.popBackStack(currentFragment?.id!!, true)
        }
    }
}