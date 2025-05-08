package com.attendance.attendancetracker.data.repository

import android.util.Log
import com.attendance.attendancetracker.data.models.ClassItem
import com.attendance.attendancetracker.data.remote.api.AuthApi
import com.attendance.attendancetracker.data.remote.dto.DashboardResponse
import com.attendance.attendancetracker.data.remote.dto.toDomain
import javax.inject.Inject

class DashboardRepository @Inject constructor(private val api: AuthApi) {
    suspend fun getDashboard(token: String): Result<List<ClassItem>> {
        Log.d("DashboardRepository", "getDashboard called with token: $token")
        return try {
            val response = api.getDashboardClasses(token)
            Log.d("DashboardRepository", "API response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            if (response.isSuccessful) {
                val dashboardResponse = response.body()
                Log.d("DashboardRepository", "API response body: $dashboardResponse")
                if (dashboardResponse != null && dashboardResponse.success) {
                    val classItemDtos = dashboardResponse.data
                    if (classItemDtos != null) {
                        Log.d("DashboardRepository", "Mapping ${classItemDtos.size} DTOs to domain models.")
                        val classItems = classItemDtos.map { it.toDomain() }
                        Result.success(classItems)
                    } else {
                        Log.d("DashboardRepository", "API response data is null, returning empty list.")
                        Result.success(emptyList())
                    }
                } else {
                    val errorMessage = "API request failed or success flag is false. Response: $dashboardResponse"
                    Log.w("DashboardRepository", errorMessage)
                    Result.failure(Exception(errorMessage))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("DashboardRepository", "HTTP error ${response.code()}: ${response.message()}. Error body: $errorBody")
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("DashboardRepository", "Exception in getDashboard", e)
            Result.failure(e)
        }
    }
}
