package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.widget.Spinner
import android.widget.TextView
import android.widget.EditText
import java.util.Calendar
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
            val spinnerLeaveType = view.findViewById<Spinner>(R.id.spinnerLeaveType)
            val tvFromDate = view.findViewById<TextView>(R.id.tvFromDate)
            val tvToDate = view.findViewById<TextView>(R.id.tvToDate)
            val etReason = view.findViewById<EditText>(R.id.etReason)

            // Setup spinner adapter
            val leaveTypes = arrayOf("Select Leave Type", "Casual Leave", "Sick Leave", "Earned Leave")
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, leaveTypes)
            spinnerLeaveType.adapter = adapter

            // Validation logic
            fun validateForm() {
                val isLeaveTypeSelected = spinnerLeaveType.selectedItemPosition > 0
                val isFromDateSelected = tvFromDate.text.toString().isNotEmpty()
                val isToDateSelected = tvToDate.text.toString().isNotEmpty()
                val isReasonEntered = etReason.text.toString().trim().isNotEmpty()

                val isValid = isLeaveTypeSelected && isFromDateSelected && isToDateSelected && isReasonEntered

                btnSubmit.isEnabled = isValid
                if (isValid) {
                    btnSubmit.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0A0A1E"))
                } else {
                    btnSubmit.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
                }
            }

            // Initial validation
            validateForm()

            spinnerLeaveType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    validateForm()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            etReason.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    validateForm()
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            // Setup DatePickers
            val calendar = Calendar.getInstance()
            val dateSetListener = { textView: TextView ->
                val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                    val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    textView.text = selectedDate
                    validateForm()
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                
                // Disable past dates
                datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                
                // Restrict to end of current month
                val endOfMonth = Calendar.getInstance()
                endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH))
                datePickerDialog.datePicker.maxDate = endOfMonth.timeInMillis
                
                datePickerDialog.show()
            }

            tvFromDate.setOnClickListener { dateSetListener(tvFromDate) }
            tvToDate.setOnClickListener { dateSetListener(tvToDate) }

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
