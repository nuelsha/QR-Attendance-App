package com.attendance.attendancetracker.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.attendance.attendancetracker.R
import com.attendance.attendancetracker.ui.theme.Typography

@Composable
fun CourseDashboardScreen(
    courseName: String = "Cyber Security",
    teacherName: String = "Senayit Demisse",
    presentPercentage: Int = 80,
    absentPercentage: Int = 20,
    onBackClick: () -> Unit = {},
    onScanClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF001E2F))
                .padding(16.dp)
        ) {
            // Logo
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "SCANIN Logo",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Course title with back button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Back",
                        tint = Color(0xFF001E2F)
                    )
                }

                Text(
                    text = courseName,
                    style = Typography.titleLarge.copy(
                        color = Color(0xFF001E2F),
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Teacher name
            Text(
                text = "Teacher: $teacherName",
                style = Typography.bodyMedium.copy(color = Color(0xFF4A6572)),
                modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
            )

            // Tracking Summary
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Calendar",
                    tint = Color(0xFF001E2F),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Tracking Summary",
                    style = Typography.titleMedium.copy(
                        color = Color(0xFF001E2F),
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Present card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF001E2F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Present",
                            tint = Color.White
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = "Present",
                            style = Typography.titleMedium.copy(
                                color = Color(0xFF001E2F),
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            text = "The number of days that the student was available.",
                            style = Typography.bodySmall.copy(color = Color(0xFF4A6572))
                        )
                    }

                    Text(
                        text = "$presentPercentage%",
                        style = Typography.titleLarge.copy(
                            color = Color(0xFF001E2F),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Absent card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF001E2F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Absent",
                            tint = Color.White
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = "Absent",
                            style = Typography.titleMedium.copy(
                                color = Color(0xFF001E2F),
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            text = "The number of days that the student was NOT available.",
                            style = Typography.bodySmall.copy(color = Color(0xFF4A6572))
                        )
                    }

                    Text(
                        text = "$absentPercentage%",
                        style = Typography.titleLarge.copy(
                            color = Color(0xFF001E2F),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Attendance Report
            Text(
                text = "Attendance Report",
                style = Typography.titleMedium.copy(
                    color = Color(0xFF001E2F),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Calendar grid
            AttendanceCalendarGrid(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                LegendItem(color = Color(0xFF001E2F), text = "Present")
                Spacer(modifier = Modifier.width(16.dp))
                LegendItem(color = Color(0xFFFFB74D), text = "Excuse")
                Spacer(modifier = Modifier.width(16.dp))
                LegendItem(color = Color(0xFFE53935), text = "Absent")
            }

            // Scan button
            Button(
                onClick = onScanClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001E2F)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.End)
                    .width(120.dp)
                    .height(48.dp)
                    .padding(top = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Scan",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan")
            }
        }
    }
}

@Composable
fun AttendanceCalendarGrid(modifier: Modifier) {
    // Sample data - 5 rows of 5 days each to match the image
    val attendanceData = listOf(
        listOf(1, 1, 1, 1, 1),
        listOf(1, 1, 3, 1, 1),
        listOf(1, 1, 1, 3, 1),
        listOf(1, 1, 1, 1, 1),
        listOf(1, 0, 0, 0, 0)
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        attendanceData.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { status ->
                    val color = when (status) {
                        1 -> Color(0xFF001E2F) // Present
                        2 -> Color(0xFFFFB74D) // Excuse
                        3 -> Color(0xFFE53935) // Absent
                        else -> Color.Transparent // Not yet
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(color, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = text,
            style = Typography.bodySmall.copy(color = Color(0xFF4A6572))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CourseDashboardScreenPreview() {
    CourseDashboardScreen()
}