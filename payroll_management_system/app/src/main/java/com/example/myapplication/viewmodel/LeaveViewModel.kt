package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.myapplication.MyApplication
import com.example.myapplication.data.api.RetrofitClient
import com.example.myapplication.data.model.*
import kotlinx.coroutines.launch

class LeaveViewModel : ViewModel() {

    private val repository = com.example.myapplication.data.repository.LeaveRepository()
    private val emsApi = RetrofitClient.emsApi

    // =====================
    // LIVE DATA
    // =====================
    private val _leaveBalances = MutableLiveData<LeaveBalanceResponse?>()
    val leaveBalances: LiveData<LeaveBalanceResponse?> = _leaveBalances

    private val _leaveTypes = MutableLiveData<List<LeaveType>>()
    val leaveTypes: LiveData<List<LeaveType>> = _leaveTypes

    private val _applyResult = MutableLiveData<Pair<Boolean, String>>()
    val applyResult: LiveData<Pair<Boolean, String>> = _applyResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _leaveHistory = MutableLiveData<List<LeaveResponse>>()
    val leaveHistory: LiveData<List<LeaveResponse>> = _leaveHistory

    private val _leaveRequests = MutableLiveData<List<LeaveResponse>>()
    val leaveRequests: LiveData<List<LeaveResponse>> = _leaveRequests

    private val _leaveDetails = MutableLiveData<LeaveResponse?>()
    val leaveDetails: LiveData<LeaveResponse?> = _leaveDetails

    // =====================
    // BALANCE
    // =====================
    fun fetchLeaveBalance() {
        val empId = MyApplication.sessionManager.fetchEmpIdEms()
        if (empId.isNullOrBlank()) {
            _errorMessage.value = "Invalid Employee ID. Please login again."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getLeaveBalance(empId)
            result.onSuccess { dataList ->
                _leaveBalances.value = dataList.firstOrNull()?.let { 
                    // Map LeaveBalanceItem back to LeaveBalanceResponse if needed for compatibility
                    LeaveBalanceResponse(
                        empId = it.empLeaveId,
                        casualLeave = it.remainingLeaves?.toInt() ?: 0,
                        sickLeave = 0, // Placeholder
                        earnedLeave = 0,
                        totalLeave = it.totalLeaves?.toInt() ?: 0
                    )
                }
            }.onFailure { e ->
                _error.value = e.message ?: "Failed to load balance"
            }
            _isLoading.value = false
        }
    }

    // =====================
    // TYPES
    // =====================
    fun fetchLeaveTypes() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getLeaveTypes()
            result.onSuccess { data ->
                _leaveTypes.value = data
            }.onFailure { e ->
                _error.value = e.message ?: "Failed to load leave types"
            }
            _isLoading.value = false
        }
    }

    // =====================
    // APPLY LEAVE
    // =====================
    fun applyLeave(request: LeaveApplyRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.applyLeave(request)
            result.onSuccess { message ->
                _applyResult.value = true to message
            }.onFailure { e ->
                _applyResult.value = false to (e.message ?: "Apply leave failed")
            }
            _isLoading.value = false
        }
    }

    // =====================
    // LEAVE HISTORY
    // =====================
    fun fetchLeaveHistory() {
        val empId = MyApplication.sessionManager.fetchEmpIdEms()
        if (empId.isNullOrBlank()) {
            _errorMessage.value = "Invalid Employee ID. Please login again."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getLeaveHistory(empId)
            result.onSuccess { data ->
                _leaveHistory.value = data
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Failed to load leave history"
            }
            _isLoading.value = false
        }
    }

    // =====================
    // LEAVE REQUESTS (HR)
    // =====================
    fun fetchLeaveRequests(status: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                Log.d("LeaveVM", "========== FETCH LEAVE REQUESTS ==========")
                Log.d("LeaveVM", "status filter: ${status ?: "ALL"}")
                
                val response = emsApi.getLeaveRequests(status)
                Log.d("LeaveVM", "Response Code: ${response.code()}")
                Log.d("LeaveVM", "Is Successful: ${response.isSuccessful}")
                
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    Log.d("LeaveVM", "Success! Received ${data.size} leave requests")
                    data.forEachIndexed { index, leave ->
                        Log.d("LeaveVM", "  Request ${index + 1}:")
                        Log.d("LeaveVM", "    leaveApplicationId: ${leave.leaveApplicationId}")
                        Log.d("LeaveVM", "    empId: ${leave.empId}")
                        Log.d("LeaveVM", "    status: ${leave.status}")
                    }
                    Log.d("LeaveVM", "================================================")
                    _leaveRequests.value = data
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LeaveVM", "Failed to fetch requests. Code: ${response.code()}")
                    Log.e("LeaveVM", "Error: $errorBody")
                    _errorMessage.value = "Failed to load leave requests: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("LeaveVM", "========== FETCH LEAVE REQUESTS FAILED ==========")
                Log.e("LeaveVM", "Exception: ${e.message}")
                e.printStackTrace()
                Log.d("LeaveVM", "================================================")
                _errorMessage.value = e.message ?: "Failed to load leave requests"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // =====================
    // APPROVE / REJECT LEAVE
    // =====================
    fun approveRejectLeave(leaveApplicationId: String, status: String, remarks: String?) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                Log.d("LeaveVM", "========== APPROVE/REJECT LEAVE ==========")
                Log.d("LeaveVM", "leaveApplicationId: $leaveApplicationId")
                Log.d("LeaveVM", "status: $status")
                Log.d("LeaveVM", "remarks: $remarks")
                
                val request = LeaveApprovalRequest(
                    leaveApplicationId = leaveApplicationId,
                    status = status,
                    remarks = remarks
                )
                
                val response = emsApi.approveRejectLeave(request)
                Log.d("LeaveVM", "Response Code: ${response.code()}")
                Log.d("LeaveVM", "Is Successful: ${response.isSuccessful}")
                
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    Log.d("LeaveVM", "Success!")
                    Log.d("LeaveVM", "  leaveApplicationId: ${data.leaveApplicationId}")
                    Log.d("LeaveVM", "  status: ${data.status}")
                    Log.d("LeaveVM", "  message: ${data.message}")
                    Log.d("LeaveVM", "================================================")
                    _applyResult.value = true to (data.message ?: "Leave $status successfully")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LeaveVM", "Failed to update leave. Code: ${response.code()}")
                    Log.e("LeaveVM", "Error: $errorBody")
                    Log.d("LeaveVM", "================================================")
                    _applyResult.value = false to ("Failed to update leave: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LeaveVM", "========== APPROVE/REJECT LEAVE FAILED ==========")
                Log.e("LeaveVM", "Exception: ${e.message}")
                e.printStackTrace()
                Log.d("LeaveVM", "================================================")
                _applyResult.value = false to (e.message ?: "Failed to update leave")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // =====================
    // SINGLE LEAVE DETAILS
    // =====================
    fun fetchLeaveDetails(leaveApplicationId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                Log.d("LeaveVM", "========== FETCH LEAVE DETAILS ==========")
                Log.d("LeaveVM", "leaveApplicationId: $leaveApplicationId")
                
                val response = emsApi.getLeaveDetails(leaveApplicationId)
                Log.d("LeaveVM", "Response Code: ${response.code()}")
                Log.d("LeaveVM", "Is Successful: ${response.isSuccessful}")
                
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    Log.d("LeaveVM", "Success! Leave Details:")
                    Log.d("LeaveVM", "  leaveApplicationId: ${data.leaveApplicationId}")
                    Log.d("LeaveVM", "  empId: ${data.empId}")
                    Log.d("LeaveVM", "  leaveType: ${data.leaveType}")
                    Log.d("LeaveVM", "  leaveDay: ${data.leaveDay}")
                    Log.d("LeaveVM", "  startDate: ${data.startDate}")
                    Log.d("LeaveVM", "  endDate: ${data.endDate}")
                    Log.d("LeaveVM", "  noOfDays: ${data.noOfDays}")
                    Log.d("LeaveVM", "  description: ${data.description}")
                    Log.d("LeaveVM", "  status: ${data.status}")
                    Log.d("LeaveVM", "  remarks: ${data.remarks}")
                    Log.d("LeaveVM", "================================================")
                    _leaveDetails.value = data
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LeaveVM", "Failed to fetch details. Code: ${response.code()}")
                    Log.e("LeaveVM", "Error: $errorBody")
                    _errorMessage.value = "Failed to load leave details: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("LeaveVM", "========== FETCH LEAVE DETAILS FAILED ==========")
                Log.e("LeaveVM", "Exception: ${e.message}")
                e.printStackTrace()
                Log.d("LeaveVM", "================================================")
                _errorMessage.value = e.message ?: "Failed to load leave details"
            } finally {
                _isLoading.value = false
            }
        }
    }
}