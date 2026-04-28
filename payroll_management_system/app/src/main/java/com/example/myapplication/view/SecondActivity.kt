package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.util.NavigationUtils
import com.example.myapplication.viewmodel.LeaveViewModel
import com.example.myapplication.viewmodel.LoginViewModel

class SecondActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()
    private val leaveViewModel: LeaveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Setup Bottom Navigation
        NavigationUtils.setupBottomNavigation(this)

        // Header Views
        val tvHiName = findViewById<TextView>(R.id.tvHiName)
        val tvEmpId = findViewById<TextView>(R.id.tvEmpId)

        // Navigation Containers
        val llLeaveBalance = findViewById<View>(R.id.llLeaveBalance)
        val llPayslip = findViewById<View>(R.id.llPayslip)
        val llApplyLeaveAction = findViewById<View>(R.id.llApplyLeaveAction)
        val llViewHolidaysAction = findViewById<View>(R.id.llViewHolidaysAction)
        val rlAttendanceDetailsAction = findViewById<View>(R.id.llAttendanceCard)
        val rlSalaryOverviewDetails = findViewById<View>(R.id.llSalaryOverviewCard)
        
        // Summary Views
        val tvLeaveBalanceSummary = findViewById<TextView>(R.id.tvLeaveBalanceSummary)

        // --- Salary Visibility Toggle ---
        val tvSalaryAmount = findViewById<TextView>(R.id.tvSalaryAmount)
        val tvGrossSalaryAmount = findViewById<TextView>(R.id.tvGrossSalaryAmount)
        val tvNetSalaryAmount = findViewById<TextView>(R.id.tvNetSalaryAmount)
        val ivToggleSalaryVisibility = findViewById<ImageView>(R.id.ivToggleSalaryVisibility)
        
        var isSalaryVisible = false
        val originalSalary = "₹1,40,000"
        val originalGross = "₹1,40,000"
        val originalNet = "₹1,18,200"
        val hiddenSalary = "₹XXXX"
        
        ivToggleSalaryVisibility?.setOnClickListener {
            isSalaryVisible = !isSalaryVisible
            if (isSalaryVisible) {
                tvSalaryAmount?.text = originalSalary
                tvGrossSalaryAmount?.text = originalGross
                tvNetSalaryAmount?.text = originalNet
                ivToggleSalaryVisibility.setImageResource(R.drawable.ic_visibility)
            } else {
                tvSalaryAmount?.text = hiddenSalary
                tvGrossSalaryAmount?.text = hiddenSalary
                tvNetSalaryAmount?.text = hiddenSalary
                ivToggleSalaryVisibility.setImageResource(R.drawable.ic_visibility_off)
            }
        }

        // Set User Info from Session
        val userName = MyApplication.sessionManager.fetchUserName() ?: "User"
        val empId = MyApplication.sessionManager.fetchEmpIdEms() ?: "-----"
        tvHiName.text = getString(R.string.hi_name_format, userName)
        tvEmpId.text = getString(R.string.employee_id_format, empId)

        // --- Leave Balance Logic ---
        leaveViewModel.fetchLeaveBalance()
        leaveViewModel.leaveBalances.observe(this) { balance ->
            if (balance != null) {
                val casualLeave = balance.casualLeave ?: 0
                val totalLeave = balance.totalLeave ?: 12
                tvLeaveBalanceSummary.text = getString(R.string.leave_balance_format, casualLeave, totalLeave)
            }
        }

        // --- Navigation Logic ---
        llLeaveBalance.setOnClickListener {
            startActivity(Intent(this, LeaveRequestActivity::class.java))
        }

        llPayslip.setOnClickListener {
            startActivity(Intent(this, PayrollHistoryActivity::class.java))
        }

        llApplyLeaveAction.setOnClickListener {
            startActivity(Intent(this, ApplyLeaveActivity::class.java))
        }

        llViewHolidaysAction.setOnClickListener {
            startActivity(Intent(this, HolidayActivity::class.java))
        }

        rlAttendanceDetailsAction.setOnClickListener {
            startActivity(Intent(this, Calender::class.java))
        }

        rlSalaryOverviewDetails.setOnClickListener {
            startActivity(Intent(this, SalaryOverviewActivity::class.java))
        }

        val ivNotifications = findViewById<ImageView>(R.id.ivNotifications)
        ivNotifications?.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        // Logout Observer
        viewModel.logoutResult.observe(this) { result ->
            result.onSuccess {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }.onFailure { t ->
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}