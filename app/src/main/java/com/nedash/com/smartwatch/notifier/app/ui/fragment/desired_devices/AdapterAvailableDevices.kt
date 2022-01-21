package com.nedash.com.smartwatch.notifier.app.ui.fragment.desired_devices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nedash.com.smartwatch.notifier.app.data.model.AvailableDevice
import com.nedash.com.smartwatch.notifier.app.databinding.AvailableDevicesListItemBinding

class AdapterAvailableDevices: ListAdapter<AvailableDevice, AdapterAvailableDevices.AvailableDevicesViewHolder>(
    object : DiffUtil.ItemCallback<AvailableDevice>() {
        override fun areItemsTheSame(oldItem: AvailableDevice, newItem: AvailableDevice) =
            oldItem.deviceName == newItem.deviceName

        override fun areContentsTheSame(oldItem: AvailableDevice, newItem: AvailableDevice) =
            oldItem == newItem
    }){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableDevicesViewHolder =
        AvailableDevicesViewHolder(
            AvailableDevicesListItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: AvailableDevicesViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class AvailableDevicesViewHolder(
        private val binding: AvailableDevicesListItemBinding
    )
        : RecyclerView.ViewHolder(binding.root){
        fun bind(availableDevice: AvailableDevice){
            with(binding){
                tvAvailableDeviceName.text = availableDevice.deviceName
            }
        }
    }
}