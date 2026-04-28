package com.example.myapplication.view

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.viewmodel.HolidayViewModel
import com.example.myapplication.util.NavigationUtils

class HolidayActivity : AppCompatActivity() {

    private val viewModel: HolidayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holidays)

        // Setup Bottom Navigation
        NavigationUtils.setupBottomNavigation(this)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val rvHolidays = findViewById<RecyclerView>(R.id.rvHolidays)
        
        rvHolidays.layoutManager = LinearLayoutManager(this)
        
        viewModel.holidays.observe(this) { holidayList ->
            if (holidayList != null) {
                rvHolidays.adapter = HolidayAdapter(holidayList)
            }
        }

        viewModel.loadMyHolidays()

        btnBack.setOnClickListener { finish() }
    }
}