package com.attendance.attendancetracker.data.remote.dto

import com.attendance.attendancetracker.data.models.ClassItem
import com.google.gson.annotations.SerializedName

// Main response wrapper
data class DashboardResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("count") val count: Int,
    @SerializedName("data") val data: List<ClassItemDto>? // List can be null or empty
)

// DTO for items within the 'data' array
data class ClassItemDto(
    @SerializedName("_id") val id: String,
    @SerializedName("className") val className: String?,
    @SerializedName("section") val section: String?,
    @SerializedName("schedule") val schedule: ScheduleDto?,
    @SerializedName("students") val students: List<String>?,
    @SerializedName("teacher") val teacher: String?,
    @SerializedName("createdAt") val createdAt: String? // Consider converting to Date/LocalDateTime in mapper
)

// DTO for the 'schedule' object
data class ScheduleDto(
    @SerializedName("days") val days: List<String>?
    // Add other fields if your 'schedule' object has more details
)

// Mapper function for ClassItemDto to ClassItem domain model
fun ClassItemDto.toDomain(): ClassItem {
    return ClassItem(
        id = this.id,
        className = this.className ?: "N/A", // Provide default for nullable fields
        section = this.section ?: "N/A",
        scheduleDays = this.schedule?.toDomain() ?: emptyList(),
        teacherId = this.teacher ?: "N/A",
        createdAt = this.createdAt ?: "N/A"
    )
}

// Mapper function for ScheduleDto to List<String> (domain representation of schedule days)
fun ScheduleDto.toDomain(): List<String> {
    return this.days ?: emptyList()
}
