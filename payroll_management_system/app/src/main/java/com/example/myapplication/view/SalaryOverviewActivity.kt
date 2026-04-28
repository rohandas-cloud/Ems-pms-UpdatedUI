package com.example.myapplication.view

import android.os.Bundle
import android.widget.ImageView
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.util.NavigationUtils
import com.example.myapplication.viewmodel.PayrollViewModel
import java.util.Locale

class SalaryOverviewActivity : AppCompatActivity() {
    private val viewModel: PayrollViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salary_overview)

        // Setup Bottom Navigation
        NavigationUtils.setupBottomNavigation(this)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvTotalSalary = findViewById<TextView>(R.id.tvTotalSalary)
        val tvBasicSalary = findViewById<TextView>(R.id.tvBasicSalary)
        val tvHRA = findViewById<TextView>(R.id.tvHRA)
        val tvAllowances = findViewById<TextView>(R.id.tvAllowances)
        val tvNetSalary = findViewById<TextView>(R.id.tvNetSalary)

        viewModel.payrollData.observe(this) { data ->
            if (data != null) {
                tvTotalSalary.text = String.format(Locale.getDefault(), "₹ %.2f", data.grossSalary ?: 0.0)
                tvNetSalary.text = String.format(Locale.getDefault(), "₹ %.2f", data.netSalary ?: 0.0)
                
                // Assuming data has components or we use breakdown from data
                tvBasicSalary.text = String.format(Locale.getDefault(), "₹ %.2f", data.grossSalary?.times(0.5) ?: 0.0) // Dummy split for now if not in component
                tvHRA.text = String.format(Locale.getDefault(), "₹ %.2f", data.grossSalary?.times(0.2) ?: 0.0)
                tvAllowances.text = String.format(Locale.getDefault(), "₹ %.2f", data.grossSalary?.times(0.3) ?: 0.0)
            }
        }

        // Fetch for current month
        val calendar = java.util.Calendar.getInstance()
        viewModel.fetchPayrollDetails(calendar.get(java.util.Calendar.MONTH) + 1, calendar.get(java.util.Calendar.YEAR))

        btnBack.setOnClickListener { finish() }
    }
}
