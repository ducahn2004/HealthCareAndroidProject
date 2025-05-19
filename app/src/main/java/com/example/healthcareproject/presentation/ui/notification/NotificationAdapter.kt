package com.example.healthcareproject.presentation.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val onItemClick: (Notification) -> Unit // Callback cho sự kiện nhấn
) : ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NotificationViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = getItem(position)
        holder.bind(notification)
        val backgroundColor = if (position % 2 == 0) {
            ContextCompat.getColor(holder.itemView.context, R.color.surface)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.surface_alternate)
        }
        holder.itemView.setBackgroundColor(backgroundColor)
    }

    class NotificationViewHolder(
        private val binding: ItemNotificationBinding,
        private val onItemClick: (Notification) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            binding.tvTitle.text = notification.title
            binding.tvMessage.text = notification.message
            binding.tvTime.text = notification.time
            binding.ivIcon.setImageResource(notification.iconResId)

            // Xử lý sự kiện nhấn vào item
            binding.root.setOnClickListener {
                onItemClick(notification)
            }
        }
    }
}

class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem == newItem
    }
}