package com.attendance.attendancetracker.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    fun createClass(token: String) {
        viewModelScope.launch {
            // Reset states before a new operation
            isClassCreated = false
            errorMessage = ""

            val result = repository.createClass(token)
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
}
