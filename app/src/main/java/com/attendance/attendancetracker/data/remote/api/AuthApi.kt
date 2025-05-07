package com.attendance.attendancetracker.data.remote.api

import com.attendance.attendancetracker.data.remote.dto.AuthResponseDto
import com.attendance.attendancetracker.data.remote.dto.LoginRequestDto
import com.attendance.attendancetracker.data.remote.dto.LogoutRequestDto
import com.attendance.attendancetracker.data.remote.dto.LogoutResponseDto
import com.attendance.attendancetracker.data.remote.dto.SignupRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: SignupRequestDto): AuthResponseDto

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequestDto): LogoutResponseDto
} 