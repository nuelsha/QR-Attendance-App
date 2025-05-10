package com.attendance.attendancetracker.data.repository

import android.util.Log
import com.attendance.attendancetracker.data.models.AttendanceScanRequest
import com.attendance.attendancetracker.data.models.AttendanceScanResponse
import com.attendance.attendancetracker.data.remote.dto.AttendanceHistoryResponse
import com.attendance.attendancetracker.data.remote.dto.StudentAttendanceHistoryResponse
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

    suspend fun getStudentClassAttendanceHistory(classId: String, token: String): Result<StudentAttendanceHistoryResponse> {
        Log.d("AttendanceRepository", "getStudentClassAttendanceHistory called. classId: '$classId', token present: ${token.isNotBlank()}")
        return try {
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            Log.d("AttendanceRepository", "Attempting API call: api.getStudentAttendanceHistory for classId=$classId with token: ${formattedToken.take(10)}...")
            val response = api.getStudentAttendanceHistory(classId = classId, token = formattedToken)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d("AttendanceRepository", "API call successful for getStudentClassAttendanceHistory, classId $classId")
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null for getStudentClassAttendanceHistory, classId $classId"))
            } else {
                val errorBody = response.errorBody()?.string() ?: "No error body"
                Log.e("AttendanceRepository", "API Error for getStudentClassAttendanceHistory, classId $classId: ${response.code()} ${response.message()}. Error body: $errorBody")
                Result.failure(Exception("API Error: ${response.code()} ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Exception in getStudentClassAttendanceHistory for classId $classId: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun scanAttendance(qrToken: String, classId: String, authToken: String): Result<AttendanceScanResponse> {
        Log.d("AttendanceRepository", "scanAttendance called. classId: '$classId', QR token present: ${qrToken.isNotBlank()}, auth token present: ${authToken.isNotBlank()}")
        return try {
            val formattedToken = if (authToken.startsWith("Bearer ")) authToken else "Bearer $authToken"
            Log.d("AttendanceRepository", "Attempting API call: api.scanAttendance for classId=$classId")
            
            val request = AttendanceScanRequest(token = qrToken, classId = classId)
            val response = api.scanAttendance(request = request, token = formattedToken)
            
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d("AttendanceRepository", "Attendance scan successful for classId $classId")
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null for attendance scan with classId $classId"))
            } else {
                val errorBody = response.errorBody()?.string() ?: "No error body"
                Log.e("AttendanceRepository", "API Error for attendance scan, classId $classId: ${response.code()} ${response.message()}. Error body: $errorBody")
                Result.failure(Exception("API Error: ${response.code()} ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Exception in scanAttendance for classId $classId: ${e.message}", e)
            Result.failure(e)
        }
    }
}
