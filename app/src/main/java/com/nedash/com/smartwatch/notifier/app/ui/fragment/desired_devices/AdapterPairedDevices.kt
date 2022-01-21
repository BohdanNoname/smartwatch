package com.nedash.com.smartwatch.notifier.app.ui.fragment.desired_devices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nedash.com.smartwatch.notifier.app.databinding.PairedDevicesListItemBinding
import com.nedash.com.smartwatch.notifier.app.db.entities.PairedDeviceEntity

class AdapterPairedDevices:
    ListAdapter<PairedDeviceEntity, AdapterPairedDevices.PairedDevicesViewHolder>(
        object : DiffUtil.ItemCallback<PairedDeviceEntity>() {
            override fun areItemsTheSame(oldItem: PairedDeviceEntity, newItem: PairedDeviceEntity) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PairedDeviceEntity, newItem: PairedDeviceEntity) =
                oldItem == newItem
        }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairedDevicesViewHolder =
        PairedDevicesViewHolder(binding = PairedDevicesListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        )


    override fun onBindViewHolder(holder: PairedDevicesViewHolder, position: Int) =
        holder.bind(pairedDevice = currentList[position])


    inner class PairedDevicesViewHolder constructor(
        private val binding: PairedDevicesListItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(pairedDevice: PairedDeviceEntity){
            with(binding){
                tvPairedDeviceName.text = pairedDevice.deviceName

                root.setOnClickListener {
//                    TODO(ADD LOGIC)
                }
            }
        }
    }
}