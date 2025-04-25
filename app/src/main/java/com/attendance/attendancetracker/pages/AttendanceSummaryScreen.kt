package com.attendance.attendancetracker.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.attendance.attendancetracker.R
import com.attendance.attendancetracker.ui.theme.Typography

@Composable
fun AttendanceSummaryScreen(
    sectionName: String = "Section 1",
    onBackClick: () -> Unit = {},
    onAddNewStudentClick: () -> Unit = {}
) {
    var showSummaryDialog by remember { mutableStateOf(false) }

    // Sample student data
    val students = listOf(
        Student("Unanias Tekeste", 85),
        Student("Eyuel Hadera", 91),
        Student("Saron Merone", 100),
        Student("Kidanian Semere", 92),
        Student("Eyob Seyum", 85)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECECEC))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF001E2F),
                    RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)
                )
                .padding(16.dp)
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp)
            )
        }

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Section title with back button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_ios_back_svgrepo_com),
                        contentDescription = "Back",
                        tint = Color(0xFF001E2F)
                    )
                }

                Text(
                    text = sectionName,
                    style = Typography.titleLarge.copy(
                        color = Color(0xFF001E2F),
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Student list
            students.forEach { student ->
                StudentAttendanceItem(student = student)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add New Student button
            Button(
                onClick = onAddNewStudentClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001E2F)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Add New Student",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Student")
            }
        }
    }

    // Show attendance summary dialog
    LaunchedEffect(Unit) {
        showSummaryDialog = true
    }

    if (showSummaryDialog) {
        AttendanceSummaryDialog(
            onDismiss = { showSummaryDialog = false }
        )
    }
}

@Composable
fun AttendanceSummaryDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Attendance Summary",
                        style = Typography.titleMedium.copy(
                            color = Color(0xFF001E2F),
                            fontWeight = FontWeight.Bold
                        )
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Close",
                            tint = Color(0xFF001E2F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Student summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Student avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF001E2F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "TC",
                            color = Color.White,
                            style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Teshome Chane",
                            style = Typography.bodyLarge.copy(
                                color = Color(0xFF001E2F),
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Text(
                            text = "teshome.chane@example.com",
                            style = Typography.bodySmall.copy(
                                color = Color(0xFF4A6572)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Attendance stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AttendanceStatItem(
                        label = "Present",
                        value = "70%",
                        color = Color(0xFF4CAF50)
                    )

                    AttendanceStatItem(
                        label = "Absent",
                        value = "30%",
                        color = Color(0xFFE53935)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Attendance calendar
                Text(
                    text = "Attendance Calendar",
                    style = Typography.bodyMedium.copy(
                        color = Color(0xFF001E2F),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Calendar grid
                AttendanceCalendarGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                // Legend

            }
        }
    }
}

@Composable
fun AttendanceStatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(2.dp, color, CircleShape)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                style = Typography.bodyLarge.copy(
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = Typography.bodySmall.copy(
                color = Color(0xFF4A6572)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AttendanceSummaryScreenPreview() {
    AttendanceSummaryScreen()
}
