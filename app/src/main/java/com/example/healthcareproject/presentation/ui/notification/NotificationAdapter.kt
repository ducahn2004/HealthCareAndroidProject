package com.example.healthcareproject.presentation.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.ItemNotificationBinding
import com.example.healthcareproject.present.viewmodel.notification.NotificationViewModel

class NotificationAdapter(
    private val viewModel: NotificationViewModel
) : ListAdapter<NotificationViewModel.FormattedNotification, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

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
        val notification = getItem(position)
        holder.bind(notification, viewModel)
    }

    class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: NotificationViewModel.FormattedNotification, viewModel: NotificationViewModel) {
            binding.notification = notification
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    class NotificationDiffCallback : DiffUtil.ItemCallback<NotificationViewModel.FormattedNotification>() {
        override fun areItemsTheSame(oldItem: NotificationViewModel.FormattedNotification, newItem: NotificationViewModel.FormattedNotification): Boolean {
            return oldItem.notificationId == newItem.notificationId
        }

        override fun areContentsTheSame(oldItem: NotificationViewModel.FormattedNotification, newItem: NotificationViewModel.FormattedNotification): Boolean {
            return oldItem == newItem
        }
    }
}