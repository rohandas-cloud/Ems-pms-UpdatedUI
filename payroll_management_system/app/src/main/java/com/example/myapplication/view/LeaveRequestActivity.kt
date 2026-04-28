package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.util.NavigationUtils
import com.example.myapplication.viewmodel.LeaveViewModel
import com.example.myapplication.viewmodel.LoginViewModel
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialog

class LeaveRequestActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private val leaveViewModel: LeaveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaves)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnApply = findViewById<View>(R.id.btnApply)
        val rvLeaveBalances = findViewById<RecyclerView>(R.id.rvLeaveBalances)

        rvLeaveBalances.layoutManager = LinearLayoutManager(this)

        // Set Global Navigation
        NavigationUtils.setupBottomNavigation(this)

        btnBack.setOnClickListener { finish() }

        btnApply.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.dialog_apply_leave, null)
            dialog.setContentView(view)

            val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
            btnSubmit.setOnClickListener {
                dialog.dismiss()
                // TODO: Implement submission logic here when API is ready
            }

            dialog.show()
        }

        // Observe Data
        leaveViewModel.leaveBalances.observe(this) { balance ->
            balance?.let {
                // Convert single balance object to list for the adapter
                rvLeaveBalances.adapter = LeaveBalanceAdapter(listOf(it, it, it))
            }
        }


        leaveViewModel.fetchLeaveBalance()
        leaveViewModel.fetchLeaveHistory()
    }
}
