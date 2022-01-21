package com.nedash.com.smartwatch.notifier.app.ui.fragment.desired_devices

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.nedash.com.smartwatch.notifier.app.databinding.FragmentDesiredDevicesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DesiredDevicesFragment : Fragment() {

    private var _binding: FragmentDesiredDevicesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DesiredDevicesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDesiredDevicesBinding
            .inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}