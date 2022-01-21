package com.nedash.com.smartwatch.notifier.app.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nedash.com.smartwatch.notifier.app.databinding.FragmentSplashBinding
import com.nedash.com.smartwatch.notifier.app.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment: Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  FragmentSplashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(
                if (sharedPreferences.getBool(SharedPreferences.TUTORIAL_SHOWED))
                    SplashFragmentDirections.actionNavigationSplashToNavigationHome()
                else SplashFragmentDirections.actionNavigationSplashToPurchaseBeforeTutorialFragment()
            )
        }, 1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}