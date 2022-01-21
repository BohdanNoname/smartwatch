package com.nedash.com.smartwatch.notifier.app.ui.fragment.apps_settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nedash.com.smartwatch.notifier.app.R
import com.nedash.com.smartwatch.notifier.app.databinding.AppsListItemBinding
import com.nedash.com.smartwatch.notifier.app.databinding.FragmentAppsSettingsBinding
import com.nedash.com.smartwatch.notifier.app.utils.Utils.navigateWithSlideAnimation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppsSettingsFragment : Fragment() {

    private val appsSettingsViewModel: AppsSettingsViewModel by viewModels()
    private var _binding: FragmentAppsSettingsBinding? = null
    private val appsSettingsAdapter = AdapterAppsSettings (
        navigateToNotificationThemesFragment = {
        AppsSettingsFragmentDirections
            .actionAppsSettingsFragmentToNotificationThemesFragment(it)
    },
        updateSoundModeInAppDataEntity = {
            appsSettingsViewModel.setSoundMode(it)
        })

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppsSettingsBinding.inflate(inflater, container, false)

        with(binding){
            rvApps.layoutManager = LinearLayoutManager(requireContext())
            rvApps.adapter = appsSettingsAdapter
        }

        appsSettingsViewModel.listAppData.observe(viewLifecycleOwner){
            appsSettingsAdapter.submitList(it)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}