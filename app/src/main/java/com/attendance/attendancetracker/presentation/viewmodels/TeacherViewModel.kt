package com.attendance.attendancetracker.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attendance.attendancetracker.data.repository.TeacherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherViewModel @Inject constructor(
    private val repository: TeacherRepository
) : ViewModel() {

    var isClassCreated by mutableStateOf(false)
        private set // Only ViewModel can set this

    var errorMessage by mutableStateOf("")
        private set // Only ViewModel can set this
        
    // Add student states with LiveData (to make it easier to observe in Compose)
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _isStudentAdded = MutableLiveData<Boolean>(false)
    val isStudentAdded: LiveData<Boolean> = _isStudentAdded
    
    private val _addStudentError = MutableLiveData<String?>(null)
    val addStudentError: LiveData<String?> = _addStudentError

    fun createClass(token: String, className: String, section: String, schedule: String) {
        viewModelScope.launch {
            // Reset states before a new operation
            isClassCreated = false
            errorMessage = ""

            val result = repository.createClass(token, className, section, schedule)
            if (result.isSuccess) {
                val responseData = result.getOrNull()
                if (responseData?.success == true) { // Check the 'success' field from CreateClassResponse
                    isClassCreated = true
                } else {
                    // API call itself was successful (e.g., 2xx HTTP status),
                    // but the operation indicated failure (e.g., createClassResponse.success = false)
                    errorMessage = responseData?.message ?: "Failed to create class (API reported failure)"
                }
            } else { // result.isFailure (e.g., network error, non-2xx HTTP status from Retrofit)
                errorMessage = result.exceptionOrNull()?.message ?: "Unknown error occurred during class creation"
            }
        }
    }

    // Call this from the UI after the state has been observed and handled (e.g., Toast shown)
    fun onStateHandled() {
        // Reset states if there was a definitive outcome from the last operation
        if (isClassCreated || errorMessage.isNotEmpty()) {
            isClassCreated = false
            errorMessage = ""
        }
    }
    
    /**
     * Adds a student to the specified class
     */
    fun addStudentToClass(classId: String, studentName: String, studentId: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _isStudentAdded.value = false
            _addStudentError.value = null
            
            try {
                val result = repository.addStudentToClass(classId, studentName, studentId, token)
                
                if (result.isSuccess) {
                    val response = result.getOrNull()
                    if (response?.success == true) {
                        _isStudentAdded.value = true
                    } else {
                        _addStudentError.value = response?.message ?: "Failed to add student"
                    }
                } else {
                    _addStudentError.value = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _addStudentError.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Reset add student state after it's been handled in the UI
     */
    fun resetAddStudentState() {
        _isStudentAdded.value = false
        _addStudentError.value = null
    }
}
