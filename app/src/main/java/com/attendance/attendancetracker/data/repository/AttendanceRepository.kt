package com.attendance.attendancetracker.data.repository

import android.util.Log
import com.attendance.attendancetracker.data.remote.dto.AttendanceHistoryResponse
import com.attendance.attendancetracker.data.remote.api.AuthApi
import javax.inject.Inject

class AttendanceRepository @Inject constructor(
    private val api: AuthApi
) {
    suspend fun getClassAttendanceHistory(classId: String, token: String): Result<AttendanceHistoryResponse> {
        Log.d("AttendanceRepository", "getClassAttendanceHistory called. classId: '" + classId + "', token present: " + token.isNotBlank().toString())
        return try {
            // The token should already be in "Bearer <actual_token>" format if coming from AuthViewModel/DataStore
            Log.d("AttendanceRepository", "Attempting API call: api.getAttendanceHistory for classId=$classId")
            // Ensure token has 'Bearer ' prefix
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            Log.d("AttendanceRepository", "Using token format: ${formattedToken.take(10)}...")
            val response = api.getAttendanceHistory(classId = classId, token = formattedToken)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d("AttendanceRepository", "API call successful for classId $classId")
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null for classId $classId"))
            } else {
                val errorBody = response.errorBody()?.string() ?: "No error body"
                Log.e("AttendanceRepository", "API Error for classId $classId: ${response.code()} ${response.message()}. Error body: $errorBody")
                Result.failure(Exception("API Error: ${response.code()} ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Exception in getClassAttendanceHistory for classId $classId: " + e.message, e)
            Result.failure(e)
        }
    }
}
