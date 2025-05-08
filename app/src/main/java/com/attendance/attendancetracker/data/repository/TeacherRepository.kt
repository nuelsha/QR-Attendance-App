package com.attendance.attendancetracker.data.repository

import com.attendance.attendancetracker.data.models.ClassRequest
import com.attendance.attendancetracker.data.models.CreateClassResponse
import com.attendance.attendancetracker.data.remote.api.AuthApi
import javax.inject.Inject

class TeacherRepository @Inject constructor(private val api: AuthApi) {

    suspend fun createClass(token: String): Result<CreateClassResponse> {
        return try {
            // Hardcoded ClassRequest as per the prompt
            val classRequest = ClassRequest(
                className = "os",
                section = "4",
                schedule = "something" // Or className, if that's still the preference
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
}
