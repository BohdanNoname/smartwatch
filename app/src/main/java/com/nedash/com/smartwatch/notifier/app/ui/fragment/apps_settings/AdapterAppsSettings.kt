package com.nedash.com.smartwatch.notifier.app.ui.fragment.apps_settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nedash.com.smartwatch.notifier.app.databinding.AppsListItemBinding
import com.nedash.com.smartwatch.notifier.app.db.entities.AppDataEntity

class AdapterAppsSettings(
    val navigateToNotificationThemesFragment: (AppDataEntity) -> Unit,
    val updateSoundModeInAppDataEntity: (AppDataEntity) -> Unit
) : ListAdapter<AppDataEntity, AdapterAppsSettings.SettingsViewHolder>(
    object : DiffUtil.ItemCallback<AppDataEntity>() {

        override fun areItemsTheSame(oldItem: AppDataEntity, newItem: AppDataEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AppDataEntity, newItem: AppDataEntity) =
            oldItem == newItem
    })
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder =
        SettingsViewHolder(AppsListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) =
        holder.bind(currentList[position])

    inner class SettingsViewHolder(val binding: AppsListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
            fun bind(app: AppDataEntity){
                with(binding){
                    val context = root.context
                    val icon = context.applicationInfo.icon
                    ivAppIcon.setImageResource(icon)
                    ivTheme.setOnClickListener {
                        navigateToNotificationThemesFragment(app)
                    }
                    scSoundMode.setOnCheckedChangeListener { _, isChecked ->
                        isChecked.let{ app.mute = it }
                        updateSoundModeInAppDataEntity(app)
                    }
                }
            }
    }
}