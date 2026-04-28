package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.api.RetrofitClient
import com.example.myapplication.data.model.*

class LeaveRepository {
    private val emsApi = RetrofitClient.emsApi

    suspend fun getLeaveBalance(empId: String): Result<List<LeaveBalanceItem>> {
        return try {
            val response = emsApi.getLeaveBalance(empId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch balance: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLeaveTypes(): Result<List<LeaveType>> {
        return try {
            val response = emsApi.getLeaveTypes()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch leave types: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun applyLeave(request: LeaveApplyRequest): Result<String> {
        return try {
            val response = emsApi.applyLeave(request)
            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Leave applied successfully"
                Result.success(message)
            } else {
                Result.failure(Exception("Failed to apply leave: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLeaveHistory(empId: String): Result<List<LeaveResponse>> {
        return try {
            val response = emsApi.getLeaveHistory(empId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch history: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
