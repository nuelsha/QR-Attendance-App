package com.attendance.attendancetracker.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attendance.attendancetracker.data.models.AttendanceScanResponse
import com.attendance.attendancetracker.data.remote.dto.AttendanceHistoryResponse
import com.attendance.attendancetracker.data.remote.dto.StudentAttendanceHistoryResponse
import com.attendance.attendancetracker.data.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: AttendanceRepository
) : ViewModel() {

    private val _attendanceHistory = MutableLiveData<AttendanceHistoryResponse?>()
    val attendanceHistory: LiveData<AttendanceHistoryResponse?> = _attendanceHistory

    private val _studentAttendanceCalendarData = MutableLiveData<StudentAttendanceHistoryResponse?>()
    val studentAttendanceCalendarData: LiveData<StudentAttendanceHistoryResponse?> = _studentAttendanceCalendarData
    
    private val _scanResult = MutableLiveData<AttendanceScanResponse?>()
    val scanResult: LiveData<AttendanceScanResponse?> = _scanResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadAttendanceHistory(classId: String, token: String) {
        Log.d("AttendanceViewModel", "loadAttendanceHistory called with classId: " + classId + ", token present: " + token.isNotBlank().toString())
        viewModelScope.launch {
            _isLoading.postValue(true)
            _error.postValue(null)
            val result = repository.getClassAttendanceHistory(classId, token)
            result.fold(
                onSuccess = { data ->
                    Log.d("AttendanceViewModel", "Successfully fetched attendance history for $classId")
                    _attendanceHistory.postValue(data)
                    _isLoading.postValue(false)
                },
                onFailure = { exception ->
                    Log.e("AttendanceViewModel", "Error fetching attendance history for $classId: " + exception.message, exception)
                    _attendanceHistory.postValue(null) // Clear data on error
                    _error.postValue(exception.message ?: "Unknown error")
                    _isLoading.postValue(false)
                }
            )
        }
    }

    fun loadStudentAttendanceCalendar(classId: String, token: String) {
        Log.d("AttendanceViewModel", "loadStudentAttendanceCalendar called with classId: $classId, token present: ${token.isNotBlank()}")
        viewModelScope.launch {
            _isLoading.postValue(true)
            _error.postValue(null)
            _studentAttendanceCalendarData.postValue(null) // Clear previous data
            val result = repository.getStudentClassAttendanceHistory(classId, token)
            result.fold(
                onSuccess = { data ->
                    Log.d("AttendanceViewModel", "Successfully fetched student attendance calendar for $classId")
                    _studentAttendanceCalendarData.postValue(data)
                    _isLoading.postValue(false)
                },
                onFailure = { exception ->
                    Log.e("AttendanceViewModel", "Error fetching student attendance calendar for $classId: ${exception.message}", exception)
                    _studentAttendanceCalendarData.postValue(null) // Clear data on error
                    _error.postValue(exception.message ?: "Unknown error fetching student calendar")
                    _isLoading.postValue(false)
                }
            )
        }
    }
    
    fun scanAttendance(qrToken: String, classId: String, authToken: String) {
        Log.d("AttendanceViewModel", "scanAttendance called with classId: $classId, QR token present: ${qrToken.isNotBlank()}, auth token present: ${authToken.isNotBlank()}")
        viewModelScope.launch {
            _isLoading.postValue(true)
            _error.postValue(null)
            _scanResult.postValue(null) // Clear previous data
            
            val result = repository.scanAttendance(qrToken, classId, authToken)
            result.fold(
                onSuccess = { response ->
                    Log.d("AttendanceViewModel", "Successfully submitted attendance scan: ${response.message}")
                    _scanResult.postValue(response)
                    _isLoading.postValue(false)
                },
                onFailure = { exception ->
                    Log.e("AttendanceViewModel", "Error scanning attendance: ${exception.message}", exception)
                    _scanResult.postValue(null)
                    _error.postValue(exception.message ?: "Unknown error while scanning attendance")
                    _isLoading.postValue(false)
                }
            )
        }
    }
}
