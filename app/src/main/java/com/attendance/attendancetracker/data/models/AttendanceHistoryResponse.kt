package com.attendance.attendancetracker.data.models

data class AttendanceHistoryResponse(
    val success: Boolean,
    val `class`: ClassInfo,
    val overallStats: OverallStats,
    val history: List<HistoryItem> // Prepare for future use
)

data class ClassInfo(
    val name: String,
    val section: String,
    val totalStudents: Int
)

data class OverallStats(
    val averageAttendance: Int,
    val totalClasses: Int,
    val students: List<StudentInfo>
)

data class StudentInfo(
    val _id: String,
    val name: String,
    val email: String,
    val ID: String,
    val attendancePercentage: Int
)

data class HistoryItem(
    val _id: String, // placeholder for future fields
    val date: String,
    val presentStudents: List<String>
)
