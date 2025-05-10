package com.attendance.attendancetracker.data.repository

import com.attendance.attendancetracker.data.models.AddStudentRequest
import com.attendance.attendancetracker.data.models.AddStudentResponse
import com.attendance.attendancetracker.data.models.ClassRequest
import com.attendance.attendancetracker.data.models.CreateClassResponse
import com.attendance.attendancetracker.data.models.DeleteClassResponse
import com.attendance.attendancetracker.data.models.GenerateQrRequest
import com.attendance.attendancetracker.data.models.GenerateQrResponse
import com.attendance.attendancetracker.data.remote.api.AuthApi
import com.attendance.attendancetracker.common.Result
import javax.inject.Inject

class TeacherRepository @Inject constructor(private val api: AuthApi) {

    suspend fun createClass(className: String, section: String, token: String): Result<CreateClassResponse> {
        return try {
            val classRequest = ClassRequest(
                className = className,
                section = section,
                schedule = ""
            )
            val response = api.createClass(
                classRequest = classRequest,
                token = "Bearer $token" // Adding "Bearer " prefix
            )
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to create class")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }
    
    suspend fun addStudentToClass(classId: String, studentName: String, studentId: String, token: String): Result<AddStudentResponse> {
        return try {
            val addStudentRequest = AddStudentRequest(
                ID = studentId,
                name = studentName
            )
            
            val response = api.addStudentToClass(
                classId = classId,
                request = addStudentRequest,
                token = "Bearer $token"
            )
            
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to add student")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun generateQrCode(classId: String, token: String): Result<GenerateQrResponse> {
        return try {
            val request = GenerateQrRequest(classId = classId)
            val response = api.generateQrCode(request = request, token = "Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to generate QR code")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }
}
