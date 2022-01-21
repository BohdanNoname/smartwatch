package com.nedash.com.smartwatch.notifier.app.ui.fragment.notification_themes

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nedash.com.smartwatch.notifier.app.R
import com.nedash.com.smartwatch.notifier.app.databinding.ThemesListItemBinding
import com.nedash.com.smartwatch.notifier.app.db.entities.AppDataEntity
import com.nedash.com.smartwatch.notifier.app.utils.Utils.gone
import com.nedash.com.smartwatch.notifier.app.utils.seald_classes.NotificationTheme

class AdapterNotificationThemes (
     private val updateAppDataEntity: (NotificationTheme) -> Unit
) : ListAdapter<NotificationTheme, AdapterNotificationThemes.ModeViewHolder>(
    object : DiffUtil.ItemCallback<NotificationTheme>() {
        override fun areItemsTheSame(oldItem: NotificationTheme, newItem: NotificationTheme) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: NotificationTheme, newItem: NotificationTheme) =
            oldItem == newItem
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModeViewHolder {
        val binding =
            ThemesListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false )
        return ModeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModeViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ModeViewHolder(val binding: ThemesListItemBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        fun bind(item: NotificationTheme){
            with(binding){
                if (!item.isPro){
                    ivLock.gone()
                }
                clContainer.background.setTint(item.color)
                tvHeader.setText(item.name)
                if(item == NotificationTheme.White){
                    tvHeader.setTextColor(R.color.black)
                    rb.buttonTintList = ColorStateList.valueOf(R.color.black)
                }

                root.setOnClickListener {
                    updateAppDataEntity(item)
                }
            }
        }
    }
}