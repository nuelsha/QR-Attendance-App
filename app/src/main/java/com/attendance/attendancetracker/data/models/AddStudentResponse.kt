package com.attendance.attendancetracker.data.models

data class AddStudentResponse(
    val success: Boolean,
    val message: String,
    val data: AddStudentData? = null
)

data class AddStudentData(
    val student: AddStudentInfo,
    val `class`: AddClassInfo
)

data class AddStudentInfo(
    val id: String,
    val name: String,
    val email: String
)

data class AddClassInfo(
    val schedule: Schedule,
    val _id: String,
    val className: String,
    val section: String,
    val teacher: String,
    val students: List<String>,
    val createdAt: String,
    val __v: Int
)

data class Schedule(
    val days: List<String> = emptyList()
)
