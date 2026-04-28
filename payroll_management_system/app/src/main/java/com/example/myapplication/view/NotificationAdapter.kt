package com.example.myapplication.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

data class NotificationData(
    val title: String,
    val message: String,
    val time: String,
    val iconResId: Int,
    val iconTint: String,
    val bgTint: String,
    val isUnread: Boolean
)

class NotificationAdapter(private val notifications: List<NotificationData>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llRoot: LinearLayout = view.findViewById(R.id.llNotificationRoot)
        val flIconBg: View = view.findViewById(R.id.flIconBg)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val vUnreadDot: View = view.findViewById(R.id.vUnreadDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notif = notifications[position]
        holder.tvTitle.text = notif.title
        holder.tvMessage.text = notif.message
        holder.tvTime.text = notif.time

        holder.ivIcon.setImageResource(notif.iconResId)
        holder.ivIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(notif.iconTint))
        holder.flIconBg.backgroundTintList = ColorStateList.valueOf(Color.parseColor(notif.bgTint))

        if (notif.isUnread) {
            holder.llRoot.setBackgroundColor(Color.parseColor("#FFFCF8"))
            holder.vUnreadDot.visibility = View.VISIBLE
        } else {
            holder.llRoot.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.vUnreadDot.visibility = View.GONE
        }
    }

    override fun getItemCount() = notifications.size
}
