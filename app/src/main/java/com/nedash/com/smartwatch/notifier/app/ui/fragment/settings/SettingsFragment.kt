package com.nedash.com.smartwatch.notifier.app.ui.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nedash.com.smartwatch.notifier.app.R
import com.nedash.com.smartwatch.notifier.app.databinding.FragmentSettingsBinding
import com.nedash.com.smartwatch.notifier.app.ui.activity.MainActivity
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Url.OUR_APPS_URL
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Url.SITE_URL
import com.nedash.com.smartwatch.notifier.app.utils.Dialogs.showChangeDefaultThemeDialog
import com.nedash.com.smartwatch.notifier.app.utils.Utils.gone
import com.nedash.com.smartwatch.notifier.app.utils.Utils.navToPurchaseInsideFragment
import com.nedash.com.smartwatch.notifier.app.utils.Utils.openURL
import com.nedash.com.smartwatch.notifier.app.utils.Utils.shareApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModels()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        with(binding){
            if((activity as MainActivity).isPro){
                llBecomePro.gone()
            } else {
                llBecomePro.setOnClickListener {
                    findNavController().navToPurchaseInsideFragment()
                }
            }

            llReconnect.setOnClickListener {
                cbReconnect.isChecked = !cbReconnect.isChecked
            }

            with(requireContext()){
                llDefaultTheme.setOnClickListener {
                    SettingsFragmentDirections
                        .actionSettingsFragmentToNotificationThemesFragment(
                            isChangeDefaultThemeForAllApps = true,
                            appDataEntity = null
                        )
                }

                llOpenInGp.setOnClickListener {
                    openURL(OUR_APPS_URL)
                }

                llShareWithFriends.setOnClickListener {
                    shareApp()
                }

                llOurSite.setOnClickListener {
                    openURL(SITE_URL)
                }

                llDefaultTheme.setOnClickListener {
                    showChangeDefaultThemeDialog(requireContext(), layoutInflater)
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}