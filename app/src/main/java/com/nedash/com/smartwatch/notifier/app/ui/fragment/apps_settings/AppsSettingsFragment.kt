package com.nedash.com.smartwatch.notifier.app.ui.fragment.apps_settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nedash.com.smartwatch.notifier.app.R
import com.nedash.com.smartwatch.notifier.app.databinding.AppsListItemBinding
import com.nedash.com.smartwatch.notifier.app.databinding.FragmentAppsSettingsBinding
import com.nedash.com.smartwatch.notifier.app.utils.Utils.gone
import com.nedash.com.smartwatch.notifier.app.utils.Utils.navigateWithSlideAnimation
import com.nedash.com.smartwatch.notifier.app.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppsSettingsFragment : Fragment() {

    private val appsSettingsViewModel: AppsSettingsViewModel by viewModels()
    private var _binding: FragmentAppsSettingsBinding? = null
    private val binding get() = _binding!!


    private val appsSettingsAdapter =
        AdapterAppsSettings (
            navigateToNotificationThemesFragment = {
                AppsSettingsFragmentDirections
                    .actionAppsSettingsFragmentToNotificationThemesFragment(
                        false, it)
            },
            updateSoundModeInAppDataEntity = {
                appsSettingsViewModel.setSoundMode(it)
            })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppsSettingsBinding.inflate(inflater, container, false)


        with(binding){
            rvApps.layoutManager = LinearLayoutManager(requireContext())
            rvApps.adapter = appsSettingsAdapter

            etSearch.addTextChangedListener{
                appsSettingsViewModel.getAppsByName(etSearch.text.toString())
            }

            if(etSearch.text.toString().isEmpty()){
                ivSearch.visible()
                ivClear.gone()
            } else {
                ivClear.visible()
                ivSearch.gone()
            }

            ivClear.setOnClickListener {
                etSearch.text.clear()
            }
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