package com.attendance.attendancetracker.data.repository

import com.attendance.attendancetracker.data.models.AuthResponse // Domain model
// DTOs are not directly exposed by the repository interface in this design
// Instead, primitive types are used for request parameters.

interface AuthRepository {
    suspend fun login(name: String, ID: String, password: String): AuthResponse
    suspend fun signup(name: String, email: String, ID: String, password: String, role: String): AuthResponse
    suspend fun logout(name: String, ID: String, password: String): String // Returns a message string
} 