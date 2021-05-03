package com.modolo.healthyplus.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.modolo.healthyplus.R

class NotificationAdapter(private val notifications: ArrayList<Notification>, private val notificationListener: NotificationListener) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.notificationTitle)
        val desc: TextView = itemView.findViewById(R.id.notification)
        val date: TextView = itemView.findViewById(R.id.timepassed)
    }

    override fun getItemCount(): Int = notifications.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.main_notification_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        with(holder) {

            title.text = notification.title
            if (notification.description == "")
                desc.isVisible = false
            else {
                desc.isVisible = true
                desc.text = notification.description
            }
            date.text = notification.date

            title.setTextColor(notification.color)

            holder.itemView.setOnClickListener {
                notificationListener.onNotificationListener(notification, holder.layoutPosition, false)
            }
            holder.itemView.setOnLongClickListener {
                notificationListener.onNotificationListener(notification, holder.layoutPosition, true)
                true
            }
        }
    }


    interface NotificationListener {
        fun onNotificationListener(notification: Notification, position: Int, longpress: Boolean)
    }
}