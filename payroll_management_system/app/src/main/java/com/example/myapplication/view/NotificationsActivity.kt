package com.example.myapplication.view

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val rvNotifications = findViewById<RecyclerView>(R.id.rvNotifications)
        rvNotifications.layoutManager = LinearLayoutManager(this)

        val notifications = listOf(
            NotificationData(
                "Salary Credited",
                "₹1,40,000 credited to your account",
                "2h ago",
                R.drawable.wallet,
                "#4CAF50", // Green tint
                "#E8F5E9", // Light green bg
                true
            ),
            NotificationData(
                "Payslip Available",
                "Your April payslip is now available",
                "5h ago",
                R.drawable.ic_file,
                "#2196F3", // Blue tint
                "#E3F2FD", // Light blue bg
                true
            ),
            NotificationData(
                "Leave Approved",
                "Your leave request for May 5-7 has been approved",
                "1d ago",
                R.drawable.ic_calendar,
                "#9C27B0", // Purple tint
                "#F3E5F5", // Light purple bg
                false
            ),
            NotificationData(
                "Check-in Reminder",
                "Don't forget to check in today",
                "1d ago",
                R.drawable.ic_clock,
                "#FF9800", // Orange tint
                "#FFF3E0", // Light orange bg
                false
            ),
            NotificationData(
                "Upcoming Holiday",
                "Labour Day on May 1st",
                "2d ago",
                R.drawable.ic_party,
                "#E91E63", // Pink tint
                "#FCE4EC", // Light pink bg
                false
            )
        )

        rvNotifications.adapter = NotificationAdapter(notifications)
    }
}
