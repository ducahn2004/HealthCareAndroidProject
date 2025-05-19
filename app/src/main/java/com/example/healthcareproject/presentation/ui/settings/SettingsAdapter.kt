package com.example.healthcareproject.presentation.ui.settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.R

class SettingsAdapter(
    private val items: List<SettingItem>,
    private val onItemClick: (SettingItem) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    // ViewHolder class
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_setting_name)
        private val ivArrow: ImageView = itemView.findViewById(R.id.iv_arrow)

        fun bind(item: SettingItem) {
            ivIcon.setImageResource(item.icon)
            tvTitle.text = item.title
            ivArrow.visibility = View.VISIBLE

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_setting, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}