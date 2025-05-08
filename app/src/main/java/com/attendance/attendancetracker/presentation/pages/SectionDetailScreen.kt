package com.attendance.attendancetracker.presentation.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.attendance.attendancetracker.R
import com.attendance.attendancetracker.ui.theme.Typography

@Composable
fun SectionDetailScreen(
    courseName: String,
    authToken: String,
    onBackClick: () -> Unit = {},
    onGenerateQRClick: (String) -> Unit = {}
) {
    var students by remember {
        mutableStateOf(
            mutableListOf(
                Student("Tsedeke Techane", "UGR/1234/15", 40),
                Student("Anat Esayas", "UGR/5002/15", 90),
                Student("Nuel Mezemir", "UGR/3456/15", 100),
                Student("Kalkidan Semere", "UGR/3894/15", 60),
                Student("Biruk Siyoum", "UGR/2486/15", 80),
                Student("Student Six", "UGR/6000/15", 50),
                Student("Student Seven", "UGR/7000/15", 75),
                Student("Student Eight", "UGR/8000/15", 85),
                Student("Student Nine", "UGR/9000/15", 95),
                Student("Student Ten", "UGR/10000/15", 88)
            )
        )
    }

    var showAddForm by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newId by remember { mutableStateOf("") }
    var showAll by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val visibleCount = 8

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddForm = !showAddForm },
                containerColor = Color(0xFF001E2F)
            ) {
                Text("+", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFFECECEC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF001E2F))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Image(
                    painter = painterResource(id = R.drawable.scanin_logo_removebg_preview__1__2_layerstyle__1_),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF001E2F)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = courseName,
                            style = Typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF001E2F)
                            )
                        )
                        Text(
                            text = "Total: ${students.size} Students",
                            style = Typography.bodySmall.copy(color = Color(0xFF4A6572))
                        )
                    }
                    IconButton(onClick = { onGenerateQRClick(courseName) }) {
                        Image(
                            painter = painterResource(id = R.drawable.gg_qr),
                            contentDescription = "Generate QR",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val displayedStudents = if (showAll) students else students.take(visibleCount)

                        displayedStudents.forEachIndexed { index, student ->
                            StudentAttendanceItem(index + 1, student)
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (!showAll && students.size > visibleCount) {
                            IconButton(
                                onClick = { showAll = true },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Show More",
                                    tint = Color(0xFF001E2F)
                                )
                            }
                        }

                        if (showAddForm) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9FA)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        "Add New Student",
                                        style = Typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF001E2F)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = newName,
                                        onValueChange = { newName = it },
                                        label = { Text("Student Name") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = newId,
                                        onValueChange = { newId = it },
                                        label = { Text("Student ID") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            if (newName.isNotBlank() && newId.isNotBlank()) {
                                                students.add(Student(newName, newId, 0))
                                                newName = ""
                                                newId = ""
                                                showAddForm = false
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .align(Alignment.End)
                                            .height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001E2F))
                                    ) {
                                        Text(text = "+ Add", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentAttendanceItem(index: Int, student: Student) {
    var showBottomSheet by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showBottomSheet = true },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                "$index. ${student.name}",
                fontWeight = FontWeight.Medium,
                color = Color(0xFF001E2F)
            )
            Text(
                student.id,
                color = Color(0xFF4A6572),
                style = Typography.bodySmall
            )
        }
        Text(
            "${student.attendancePercentage}%",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF001E2F)
        )
    }
    
    if (showBottomSheet) {
        AttendanceSummaryBottomSheet(
            student = student,
            onDismiss = { showBottomSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceSummaryBottomSheet(student: Student, onDismiss: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Header with title and close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Attendance Summary",
                    style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF001E2F)
                )
                
                IconButton(onClick = onDismiss) {
                    Text("✕", color = Color(0xFF001E2F), fontWeight = FontWeight.Bold)
                }
            }
            
            // Student info with avatar
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Student avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF001E2F), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Student",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Student details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF001E2F)
                    )
                    
                    Text(
                        text = "Email: ${student.name.replace(" ", "").lowercase()}@gmail.com",
                        style = Typography.bodySmall,
                        color = Color(0xFF4A6572)
                    )
                    
                    Text(
                        text = student.id,
                        style = Typography.bodySmall,
                        color = Color(0xFF4A6572)
                    )
                }
                
                // Section info
                Text(
                    text = "Section 1",
                    style = Typography.bodySmall,
                    color = Color(0xFF4A6572)
                )
            }
            
            // Present/Absent percentage cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Present card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Present icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF001E2F), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Present",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = "Present",
                                style = Typography.bodyMedium,
                                color = Color(0xFF001E2F)
                            )
                            
                            Text(
                                text = "${student.attendancePercentage}%",
                                style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF001E2F)
                            )
                        }
                    }
                }
                
                // Absent card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Absent icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF001E2F), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Absent",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = "Absent",
                                style = Typography.bodyMedium,
                                color = Color(0xFF001E2F)
                            )
                            
                            Text(
                                text = "${100 - student.attendancePercentage}%",
                                style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF001E2F)
                            )
                        }
                    }
                }
            }
            
            // Attendance history grid
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Grid of attendance days
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        // Generate sample attendance days
                        items(21) { day ->
                            val status = when {
                                day == 9 || day == 16 -> "absent" // Red squares
                                day == 2 -> "excused" // Yellow square
                                else -> "present" // Dark blue squares
                            }
                            
                            val backgroundColor = when(status) {
                                "present" -> Color(0xFF001E2F)
                                "absent" -> Color(0xFFE57373)
                                "excused" -> Color(0xFFFFD54F)
                                else -> Color(0xFF001E2F)
                            }
                            
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(32.dp)
                                    .background(
                                        color = backgroundColor,
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${day + 1}",
                                    color = Color.White,
                                    style = Typography.bodySmall
                                )
                            }
                        }
                    }
                    
                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Present legend
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color(0xFF001E2F), RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Present", style = Typography.bodySmall)
                        }
                        
                        // Excuse legend
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color(0xFFFFD54F), RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Excuse", style = Typography.bodySmall)
                        }
                        
                        // Absent legend
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color(0xFFE57373), RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Absent", style = Typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

data class Student(
    val name: String,
    val id: String,
    val attendancePercentage: Int
)

@Preview(showBackground = true)
@Composable
fun SectionDetailScreenPreview() {
    SectionDetailScreen(courseName = "Sample Course", authToken = "sample_token")
}
