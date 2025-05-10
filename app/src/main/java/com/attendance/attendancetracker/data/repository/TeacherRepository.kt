package com.attendance.attendancetracker.data.repository

import com.attendance.attendancetracker.data.models.AddStudentRequest
import com.attendance.attendancetracker.data.models.AddStudentResponse
import com.attendance.attendancetracker.data.models.ClassRequest
import com.attendance.attendancetracker.data.models.CreateClassResponse
import com.attendance.attendancetracker.data.remote.api.AuthApi
import javax.inject.Inject

class TeacherRepository @Inject constructor(private val api: AuthApi) {

    suspend fun createClass(token: String, className: String, section: String, schedule: String): Result<CreateClassResponse> {
        return try {
            val classRequest = ClassRequest(
                className = className,
                section = section,
                schedule = schedule
            )
            val response = api.createClass(
                classRequest = classRequest,
                token = "Bearer $token" // Adding "Bearer " prefix
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
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
            
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
