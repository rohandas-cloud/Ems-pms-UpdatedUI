package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityPayrollHistoryBinding
import com.example.myapplication.util.NavigationUtils
import com.example.myapplication.view.adapter.PayrollSummaryAdapter
import com.example.myapplication.viewmodel.PayrollViewModel

class PayrollHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayrollHistoryBinding
    private val viewModel: PayrollViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayrollHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
        
        // Initial fetch for April 2026
        viewModel.fetchPayrollFromSession(4, 2026)
    }

    private fun setupUI() {
        binding.rvPayrollHistory.layoutManager = LinearLayoutManager(this)
        
        binding.btnBack.setOnClickListener { finish() }
        
        NavigationUtils.setupBottomNavigation(this)
    }

    private fun observeViewModel() {
        // Dummy data to match the screenshot design
        val dummyData = listOf(
            com.example.myapplication.data.model.PayrollSummaryItem(
                empSalaryId = "1", firstName = null, lastName = null, netSalary = null, grossSalary = null, totalDeductions = null, month = 3, year = 2026, status = null
            ),
            com.example.myapplication.data.model.PayrollSummaryItem(
                empSalaryId = "2", firstName = null, lastName = null, netSalary = null, grossSalary = null, totalDeductions = null, month = 2, year = 2026, status = null
            ),
            com.example.myapplication.data.model.PayrollSummaryItem(
                empSalaryId = "3", firstName = null, lastName = null, netSalary = null, grossSalary = null, totalDeductions = null, month = 1, year = 2026, status = null
            ),
            com.example.myapplication.data.model.PayrollSummaryItem(
                empSalaryId = "4", firstName = null, lastName = null, netSalary = null, grossSalary = null, totalDeductions = null, month = 12, year = 2025, status = null
            ),
            com.example.myapplication.data.model.PayrollSummaryItem(
                empSalaryId = "5", firstName = null, lastName = null, netSalary = null, grossSalary = null, totalDeductions = null, month = 11, year = 2025, status = null
            ),
            com.example.myapplication.data.model.PayrollSummaryItem(
                empSalaryId = "6", firstName = null, lastName = null, netSalary = null, grossSalary = null, totalDeductions = null, month = 10, year = 2025, status = null
            )
        )

        binding.rvPayrollHistory.visibility = View.VISIBLE
        binding.tvEmptyState.visibility = View.GONE
        binding.rvPayrollHistory.adapter = PayrollSummaryAdapter(dummyData) { empSalaryId ->
            // Open dummy PDF or toast on download click
            viewModel.downloadPayslipByMonth(1, 2026)
            Toast.makeText(this, "Downloading Payslip...", Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
