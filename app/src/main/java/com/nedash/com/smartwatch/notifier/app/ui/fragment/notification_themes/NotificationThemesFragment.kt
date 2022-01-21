package com.nedash.com.smartwatch.notifier.app.ui.fragment.notification_themes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.nedash.com.smartwatch.notifier.app.databinding.FragmentNotificationThemesBinding
import com.nedash.com.smartwatch.notifier.app.utils.seald_classes.NotificationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationThemesFragment : Fragment() {

    private var _binding: FragmentNotificationThemesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationThemesViewModel by viewModels()

    private val args by navArgs<NotificationThemesFragmentArgs>()

    private var adapterNotificationThemes = AdapterNotificationThemes { theme ->
        if (args.isChangeDefaultThemeForAllApps){
            viewModel.updateDefaultThemeForAllAppDataEntities(
                getString(theme.name),
                theme.color
            )
        } else {
            args.appDataEntity.apply {
                    this?.themeName = getString(theme.name)
                    this?.notificationColor = theme.color
            }?.let { viewModel.updateThemeForAppDataEntity(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationThemesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterNotificationThemes.submitList(NotificationTheme.getAllNotificationThemes())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}