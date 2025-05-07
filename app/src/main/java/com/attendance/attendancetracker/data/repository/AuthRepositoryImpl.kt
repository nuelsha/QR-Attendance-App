package com.attendance.attendancetracker.data.repository

import com.attendance.attendancetracker.data.models.AuthResponse
import com.attendance.attendancetracker.data.remote.api.AuthApi
import com.attendance.attendancetracker.data.remote.dto.LoginRequestDto
import com.attendance.attendancetracker.data.remote.dto.LogoutRequestDto
import com.attendance.attendancetracker.data.remote.dto.SignupRequestDto
import com.attendance.attendancetracker.data.remote.dto.toDomain
import com.attendance.attendancetracker.data.storage.DataStoreManager
import com.attendance.attendancetracker.common.utils.HttpApiException
import com.attendance.attendancetracker.common.utils.NoInternetException
import com.attendance.attendancetracker.common.utils.UnknownApiException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val dataStore: DataStoreManager
) : AuthRepository {

    override suspend fun login(name: String, ID: String, password: String): AuthResponse {
        val request = LoginRequestDto(name = name, ID = ID, password = password)
        try {
            val responseDto = api.login(request)
            val domainResponse = responseDto.toDomain()
            dataStore.saveToken(domainResponse.token)
            return domainResponse
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            throw HttpApiException(e.code(), e.message(), errorBody, e)
        } catch (e: IOException) {
            throw NoInternetException()
        } catch (e: Exception) {
            throw UnknownApiException(cause = e)
        }
    }

    override suspend fun signup(name: String, email: String, ID: String, password: String, role: String): AuthResponse {
        val request = SignupRequestDto(name = name, email = email, password = password, ID = ID, role = role)
        try {
            val responseDto = api.register(request)
            val domainResponse = responseDto.toDomain()
            dataStore.saveToken(domainResponse.token)
            return domainResponse
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            throw HttpApiException(e.code(), e.message(), errorBody, e)
        } catch (e: IOException) {
            throw NoInternetException()
        } catch (e: Exception) {
            throw UnknownApiException(cause = e)
        }
    }

    override suspend fun logout(name: String, ID: String, password: String): String {
        val request = LogoutRequestDto(name = name, ID = ID, password = password)
        try {
            val response = api.logout(request)
            return response.message
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            throw HttpApiException(e.code(), e.message(), errorBody, e)
        } catch (e: IOException) {
            throw NoInternetException( )
        } catch (e: Exception) {
            throw UnknownApiException(cause = e)
        } finally {
            dataStore.clearToken()
        }
    }
} 