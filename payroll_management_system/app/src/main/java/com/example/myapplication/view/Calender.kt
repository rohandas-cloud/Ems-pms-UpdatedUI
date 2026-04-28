package com.example.myapplication.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.util.NavigationUtils
import com.example.myapplication.viewmodel.AttendanceViewModel
import com.example.myapplication.viewmodel.CalendarViewModel

class Calender : AppCompatActivity() {
    
    private val viewModel: CalendarViewModel by viewModels()
    private val attendanceViewModel: AttendanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        NavigationUtils.setupBottomNavigation(this)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val rvCalendar = findViewById<RecyclerView>(R.id.rvCalendar)
        
        rvCalendar.layoutManager = GridLayoutManager(this, 7)

        // Observers
        viewModel.calendarDays.observe(this) { daysList ->
            rvCalendar.adapter = CalendarAdapter(daysList) { day ->
                // Handle date selection if needed
            }
        }

        val tvMonthFilter = findViewById<TextView>(R.id.tvMonthFilter)
        
        // Observe month name
        viewModel.currentMonthName.observe(this) { monthName ->
            tvMonthFilter.text = monthName
        }

        // Initialize Calendar for current month
        val currentCalendar = java.util.Calendar.getInstance()
        val currentMonth = currentCalendar.get(java.util.Calendar.MONTH) + 1
        val currentYear = currentCalendar.get(java.util.Calendar.YEAR)
        viewModel.generateCalendar(currentMonth, currentYear)

        tvMonthFilter.setOnClickListener { view ->
            val listPopupWindow = android.widget.ListPopupWindow(this)
            listPopupWindow.anchorView = view
            
            val monthsList = mutableListOf<String>()
            val monthData = mutableListOf<Pair<Int, Int>>() // Pair of Month, Year
            
            val cal = java.util.Calendar.getInstance()
            for (i in 0 until 4) {
                val m = cal.get(java.util.Calendar.MONTH) + 1
                val y = cal.get(java.util.Calendar.YEAR)
                val name = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault()).format(cal.time)
                monthsList.add(name)
                monthData.add(Pair(m, y))
                cal.add(java.util.Calendar.MONTH, -1)
            }
            
            val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_list_item_1, monthsList)
            listPopupWindow.setAdapter(adapter)
            
            listPopupWindow.setOnItemClickListener { _, _, position, _ ->
                val (m, y) = monthData[position]
                viewModel.generateCalendar(m, y)
                listPopupWindow.dismiss()
            }
            
            listPopupWindow.show()
        }

        val empId = com.example.myapplication.MyApplication.sessionManager.fetchEmpIdEms() ?: ""
        if (empId.isNotEmpty()) {
            attendanceViewModel.fetchAttendanceHistory(empId)
        }

        attendanceViewModel.attendanceHistory.observe(this) { history ->
            viewModel.setAttendanceData(history)
        }

        btnBack.setOnClickListener { finish() }
    }
}