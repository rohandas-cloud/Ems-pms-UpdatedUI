package com.example.myapplication.util

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.myapplication.R
import com.example.myapplication.view.SecondActivity
import com.example.myapplication.view.PayrollHistoryActivity
import com.example.myapplication.view.Calender
import com.example.myapplication.view.PayslipActivity
import com.example.myapplication.view.FilterPayslipActivity
import com.example.myapplication.view.LeaveRequestActivity
import com.example.myapplication.view.ApplyLeaveActivity
import com.example.myapplication.view.HolidayActivity
import com.example.myapplication.view.SalaryOverviewActivity

import com.example.myapplication.view.ProfileActivity

object NavigationUtils {

    fun setupBottomNavigation(activity: Activity) {
        val bottomNav = activity.findViewById<BottomNavigationView>(R.id.bottomNav)
        if (bottomNav != null) {
            // Determine active item
            when (activity) {
                is SecondActivity -> bottomNav.selectedItemId = R.id.nav_home
                is PayslipActivity, is FilterPayslipActivity, is PayrollHistoryActivity, is SalaryOverviewActivity -> bottomNav.selectedItemId = R.id.nav_salary
                is Calender, is LeaveRequestActivity, is ApplyLeaveActivity, is HolidayActivity -> bottomNav.selectedItemId = R.id.nav_leaves
                is ProfileActivity -> bottomNav.selectedItemId = R.id.nav_profile
            }

            bottomNav.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> navigateTo(activity, SecondActivity::class.java)
                    R.id.nav_salary -> navigateTo(activity, SalaryOverviewActivity::class.java)
                    R.id.nav_leaves -> navigateTo(activity, Calender::class.java)
                    R.id.nav_profile -> navigateTo(activity, ProfileActivity::class.java)
                }
                true
            }
        }
    }

    private fun navigateTo(currentActivity: Activity, targetClass: Class<*>) {
        if (currentActivity.javaClass != targetClass) {
            val intent = Intent(currentActivity, targetClass)
            if (targetClass == SecondActivity::class.java) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            currentActivity.startActivity(intent)
            currentActivity.overridePendingTransition(0, 0)
            if (targetClass == SecondActivity::class.java) {
                currentActivity.finish()
            }
        }
    }
}