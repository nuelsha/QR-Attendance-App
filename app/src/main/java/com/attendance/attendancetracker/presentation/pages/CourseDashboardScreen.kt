package com.attendance.attendancetracker.presentation.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
//import androidx.compose.material.icons.filled.People
//import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.attendance.attendancetracker.R
import com.attendance.attendancetracker.ui.theme.Typography

@Composable
fun CourseDashboardScreen(
    courseName: String = "Cyber Security",
    teacherName: String = "Senayit Demisse",
    authToken: String,
    presentPercentage: Int = 80,
    absentPercentage: Int = 20,
    onBackClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onPresentClick: () -> Unit = {},
    onExcuseClick: () -> Unit = {},
    onAbsentClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // Header with logo
        AppHeader()

        // Main content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Course title with back button
            CourseHeader(courseName, teacherName, onBackClick)

            // Tracking Summary section
            TrackingSummarySection(presentPercentage, absentPercentage)

            // Divider
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            // Attendance Report section (now more compact)
            AttendanceReportSection(onPresentClick, onExcuseClick, onAbsentClick)

            // Scan button
            Box(modifier = Modifier.fillMaxWidth()) {
                ScanButton(onScanClick)
            }
        }
    }
}

@Composable
private fun AppHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF001E2F))
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.scanin_logo_removebg_preview__1__2_layerstyle__1_),
            contentDescription = "App Logo",
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
private fun CourseHeader(
    courseName: String,
    teacherName: String,
    onBackClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color(0xFF001E2F),
            modifier = Modifier.size(28.dp).clickable { onBackClick() }
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = courseName,
                style = Typography.titleMedium.copy(
                    color = Color(0xFF001E2F),
                    fontWeight = FontWeight.Bold
                )
            )
            
            Text(
                text = "Teacher: $teacherName",
                style = Typography.bodySmall.copy(color = Color(0xFF4A6572))
            )
        }
    }
}

@Composable
private fun TrackingSummarySection(
    presentPercentage: Int,
    absentPercentage: Int
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        // Section title with icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Tracking Summary Icon",
                tint = Color(0xFF001E2F),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tracking Summary",
                style = Typography.titleSmall.copy(
                    color = Color(0xFF001E2F),
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // Stat Cards
        AttendanceStatCard(
            iconRes = -1,
            iconVector = ImageVector.vectorResource(id = R.drawable.p),
            title = "Present",
            description = "The number of days that the student was available",
            percentage = presentPercentage
        )

        Spacer(modifier = Modifier.height(12.dp))

        AttendanceStatCard(
            iconRes = -1,
            iconVector = ImageVector.vectorResource(id = R.drawable.group),
            title = "Absent",
            description = "The number of days that the student was NOT available",
            percentage = absentPercentage
        )

    }
}

@Composable
fun AttendanceStatCard(
    iconRes: Int,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
    title: String,
    description: String,
    percentage: Int
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF001E2F)),
                contentAlignment = Alignment.Center
            ) {
                if (iconVector != null) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                } else if (iconRes != -1) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = Typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF001E2F))
                )
                Text(
                    text = description,
                    style = Typography.bodySmall.copy(color = Color(0xFF4A6572))
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Percentage
            Text(
                text = "$percentage%",
                style = Typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF001E2F)
                )
            )
        }
    }
}

@Composable
private fun AttendanceReportSection(
    onPresentClick: () -> Unit,
    onExcuseClick: () -> Unit,
    onAbsentClick: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        // Section title
        Text(
            text = "Attendance Report",
            style = Typography.titleSmall.copy(
                color = Color(0xFF001E2F),
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Calendar grid with smaller cells
        AttendanceCalendarGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Compact legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CompactLegendItem(color = Color(0xFF001E2F), text = "Present")
            CompactLegendItem(color = Color(0xFFFFB74D), text = "Excuse")
            CompactLegendItem(color = Color(0xFFE53935), text = "Absent")
        }
    }
}

@Composable
fun AttendanceCalendarGrid(modifier: Modifier) {
    // Based on the image, create a 4x5 grid of attendance data
    val attendanceData = listOf(
        listOf(1, 1, 1, 1, 1, 1, 1, 1),
        listOf(1, 1, 1, 1, 3, 1, 1, 1),
        listOf(1, 1, 1, 1, 1, 1, 1, 3),
        listOf(1, 1, 1, 1)
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
                        1 -> Color(0xFF001E2F)
                        2 -> Color(0xFFFFB74D)
                        3 -> Color(0xFFE53935)
                        else -> Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .weight(if (row.size < 8 && row.indexOf(status) == row.lastIndex && row.size % 2 != 0) (8-row.size+1).toFloat() else 1f)
                            .aspectRatio(1f)
                            .background(color, RoundedCornerShape(4.dp))
                    )
                }
                if (row.size < 8) {
                    for (i in 0 until (8 - row.size)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactLegendItem(color: Color, text: String) {
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

@Composable
private fun ScanButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001E2F)),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 64.dp, vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.qr), // your custom vector resource
            contentDescription = "Scan",
            modifier = Modifier.size(24.dp),
            tint = Color.White
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text("Scan", style = Typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CourseDashboardScreenPreview() {
    MaterialTheme {
        CourseDashboardScreen(authToken = "dummy_auth_token")
    }
}