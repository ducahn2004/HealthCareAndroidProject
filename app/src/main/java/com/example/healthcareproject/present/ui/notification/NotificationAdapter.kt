package com.example.healthcareproject.present.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.ItemNotificationBinding
import com.example.healthcareproject.domain.model.Alert
import com.example.healthcareproject.present.viewmodel.notification.NotificationViewModel

class NotificationAdapter(
    private val viewModel: NotificationViewModel
) : ListAdapter<Alert, NotificationAdapter.NotificationViewHolder>(AlertDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding: ItemNotificationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_notification,
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val alert = getItem(position)
        holder.bind(alert, viewModel)
    }

    class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alert: Alert, viewModel: NotificationViewModel) {
            binding.alert = alert
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    class AlertDiffCallback : DiffUtil.ItemCallback<Alert>() {
        override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem.alertId == newItem.alertId
        }

        override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem == newItem
        }
    }
}