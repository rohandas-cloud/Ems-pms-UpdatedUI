package com.example.myapplication.view

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.data.model.LeaveApplyRequest
import com.example.myapplication.data.model.LeaveType
import com.example.myapplication.util.NavigationUtils
import com.example.myapplication.viewmodel.LeaveViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log

class ApplyLeaveActivity : AppCompatActivity() {

    private lateinit var tvFromDate: TextView
    private lateinit var tvToDate: TextView
    private lateinit var etReason: EditText
    private lateinit var spinnerLeaveType: Spinner
    private lateinit var btnSubmit: Button

    private val viewModel: LeaveViewModel by viewModels()

    private var selectedType: LeaveType? = null
    private var allLeaveTypes: List<LeaveType> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_leave)

        initViews()
        setupDatePickers()
        setupObservers()

        viewModel.fetchLeaveTypes()

        NavigationUtils.setupBottomNavigation(this)

        btnSubmit.setOnClickListener { validateAndApply() }
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun initViews() {
        tvFromDate = findViewById(R.id.tvFromDate)
        tvToDate = findViewById(R.id.tvToDate)
        etReason = findViewById(R.id.etReason)
        spinnerLeaveType = findViewById(R.id.spinnerLeaveType)
        btnSubmit = findViewById(R.id.btnApplyLeave)
    }

    private fun setupObservers() {
        viewModel.leaveTypes.observe(this) { types ->
            allLeaveTypes = types ?: emptyList()
            updateDropdown()
        }

        viewModel.applyResult.observe(this) { result ->
            val (success, message) = result
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            if (success) finish()
        }

        viewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun updateDropdown() {
        val displayList = allLeaveTypes.map { it.type }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, displayList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLeaveType.adapter = adapter

        spinnerLeaveType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedType = allLeaveTypes[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupDatePickers() {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.select_date_range))
            .build()

        val listener = View.OnClickListener {
            if (!dateRangePicker.isAdded) {
                dateRangePicker.show(supportFragmentManager, "DATE_PICKER")
            }
        }

        tvFromDate.setOnClickListener(listener)
        tvToDate.setOnClickListener(listener)

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            tvFromDate.text = sdf.format(Date(selection.first))
            tvToDate.text = sdf.format(Date(selection.second))
            tvFromDate.setTextColor(android.graphics.Color.BLACK)
            tvToDate.setTextColor(android.graphics.Color.BLACK)
        }
    }

    private fun validateAndApply() {
        val type = selectedType ?: run {
            Toast.makeText(this, getString(R.string.select_leave_type), Toast.LENGTH_SHORT).show()
            return
        }

        val startDate = tvFromDate.text.toString()
        val endDate = tvToDate.text.toString()
        val reason = etReason.text.toString()
        val selectDateText = getString(R.string.select_date)

        if (startDate == selectDateText || endDate == selectDateText || reason.isBlank()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val empId = MyApplication.sessionManager.fetchEmpIdEms().orEmpty()

        val request = LeaveApplyRequest(
            empId = empId,
            leaveType = type.type,
            reason = reason,
            noOfDays = 1.0, // Simplified for now
            startDate = startDate,
            endDate = endDate,
            leaveDay = "FULL"
        )
        
        viewModel.applyLeave(request)
    }
}