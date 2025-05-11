package com.attendance.attendancetracker.presentation.pages

import android.util.Log
import android.widget.Toast

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendance.attendancetracker.R
// Removed BackButton import as it's not available
import com.attendance.attendancetracker.data.models.GenerateQrResponse
import com.attendance.attendancetracker.presentation.viewmodels.AuthViewModel
import com.attendance.attendancetracker.presentation.viewmodels.AttendanceViewModel
import com.attendance.attendancetracker.presentation.viewmodels.TeacherViewModel
import com.attendance.attendancetracker.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun SectionDetailScreen(
    courseName: String,
    authToken: String,
    onBackClick: () -> Unit = {},
    onGenerateQRClick: (String) -> Unit = {},
    attendanceViewModel: AttendanceViewModel = hiltViewModel(),
    teacherViewModel: TeacherViewModel = hiltViewModel()
) {
    // Use API data instead of hardcoded students
    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
    
    // States from ViewModels
    val attendanceHistory by attendanceViewModel.attendanceHistory.observeAsState(null)
    val isLoading by attendanceViewModel.isLoading.observeAsState(initial = false)
    val error by attendanceViewModel.error.observeAsState()
    
    // Update students list when attendance history is loaded
    LaunchedEffect(attendanceHistory) {
        attendanceHistory?.let { history ->
            val studentList = history.overallStats.students
            if (studentList.isNotEmpty()) {
                students = studentList.map { apiStudent ->
                    Student(
                        name = apiStudent.name, 
                        // Use the student ID field from StudentInfo class
                        id = apiStudent.name, // Using name as ID temporarily
                        attendancePercentage = apiStudent.attendancePercentage
                    )
                }
            }
        }
    }

    // Controls visibility of the add student dialog
    var showAddDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newId by remember { mutableStateOf("") }
    var showAll by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope() // Add coroutine scope for suspend functions
    val visibleCount = 8
    
    // Student and QR code states
    val isStudentAdded by teacherViewModel.isStudentAdded.observeAsState(false)
    val addStudentError by teacherViewModel.addStudentError.observeAsState()
    val isAddingStudent by teacherViewModel.isLoading.observeAsState(false)
    val context = LocalContext.current

    // QR Code Generation States
    var showQrDialog by remember { mutableStateOf(false) }
    val qrCodeResponse by teacherViewModel.qrCodeResponse.observeAsState()
    val qrCodeError by teacherViewModel.qrCodeError.observeAsState()
    val isGeneratingQr by teacherViewModel.isGeneratingQr.observeAsState(false)

    LaunchedEffect(key1 = courseName, key2 = authToken) {
        Log.d("SectionDetailScreen", "LaunchedEffect triggered. courseName: '$courseName', authToken present: ${authToken.isNotBlank()}")
        if (courseName.isNotBlank() && authToken.isNotBlank()) {
            Log.d("SectionDetailScreen", "Calling loadAttendanceHistory for classId: $courseName")
            attendanceViewModel.loadAttendanceHistory(classId = courseName, token = authToken)
        } else {
            Log.w("SectionDetailScreen", "Skipping loadAttendanceHistory: courseName ('$courseName') or authToken (present: ${authToken.isNotBlank()}) is blank.")
        }
    }
    
    // Handle student addition results
    LaunchedEffect(isStudentAdded, addStudentError) {
        if (isStudentAdded) {
            // Student was successfully added
            Toast.makeText(context, "Student added successfully!", Toast.LENGTH_SHORT).show()
            showAddDialog = false
            newName = ""
            newId = ""
            
            // Refresh attendance data to show the newly added student
            if (courseName.isNotBlank() && authToken.isNotBlank()) {
                attendanceViewModel.loadAttendanceHistory(classId = courseName, token = authToken)
            }
            
            // Reset the state in viewModel
            teacherViewModel.resetAddStudentState()
        }
        
        addStudentError?.let { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                teacherViewModel.resetAddStudentState()
            }
        }
    }
    
    // Handle QR Code generation results
    LaunchedEffect(qrCodeResponse, qrCodeError) {
        qrCodeResponse?.let {
            showQrDialog = true
            // Potentially reset LiveData in ViewModel if it shouldn't persist post-dialog
        }
        qrCodeError?.let {
            if (it.isNotEmpty()) {
                Toast.makeText(context, "QR Error: $it", Toast.LENGTH_LONG).show()
                // Reset error in ViewModel
                // teacherViewModel.resetQrErrorState() // You'll need to implement this
            }
        }
    }
    
    // Add Student Dialog
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    // Dialog Title
                    Text(
                        text = "Add New Student",
                        style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF001E2F),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Student Name Field
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Student Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        singleLine = true
                    )
                    
                    // Student ID Field
                    OutlinedTextField(
                        value = newId,
                        onValueChange = { newId = it },
                        label = { Text("Student ID") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    
                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        // Cancel Button
                        TextButton(
                            onClick = { 
                                showAddDialog = false
                                newName = ""
                                newId = ""
                            }
                        ) {
                            Text("Cancel")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Add Button with loading indicator
                        Button(
                            onClick = {
                                if (newName.isBlank() || newId.isBlank()) {
                                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Call API to add student
                                    teacherViewModel.addStudentToClass(
                                        classId = courseName,
                                        studentName = newName,
                                        studentId = newId,
                                        token = authToken
                                    )
                                }
                            },
                            enabled = !isAddingStudent,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001E2F))
                        ) {
                            if (isAddingStudent) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Add Student")
                            }
                        }
                    }
                }
            }
        }
    }

    // QR Code Dialog
    if (showQrDialog && qrCodeResponse != null) {
        QrCodeDialog(
            qrResponse = qrCodeResponse!!,
            onDismiss = { 
                showQrDialog = false 
                // teacherViewModel.resetQrResponseState() // You'll need to implement this
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            // Add New Student button to match the image
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color.White,
                contentColor = Color(0xFF001E2F),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Add New Student",
                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
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
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    // Top row with back button, class name, and QR icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Back button
                        Row { IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF001E2F)
                            )
                        }

                            // Class name in the center
                            Column(
                                horizontalAlignment  = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Class:",
                                    style = Typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF001E2F)
                                    )
                                )
                                Text(
                                    // Use safe call operator for nullable properties
                                    text = "Physics", // Fixed class name
                                    style = Typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF001E2F)
                                    )
                                )
                            } }
                        
                        // QR code icon
                        IconButton(onClick = { 
                            teacherViewModel.generateQrCode(
                                classId = courseName,
                                token = authToken
                            )
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.gg_qr),
                                contentDescription = "Generate QR",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    // Section info
                    Text(
                        // Use safe call operator for nullable properties
                        text = "Section: 4", // Fixed section
                        style = Typography.bodyMedium.copy(color = Color(0xFF4A6572)),
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                    )
                    
                    // Attendance stats row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 4.dp)
                    ) {
                        Text(
                            // Use safe call operator for nullable properties
                            text = "Overall Attendance: ${attendanceHistory?.overallStats?.averageAttendance ?: 0}%",
                            style = Typography.bodySmall.copy(color = Color(0xFF4A6572))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            // Use safe call operator for nullable properties
                            text = "Total Classes Conducted: ${attendanceHistory?.overallStats?.totalClasses ?: 0}",
                            style = Typography.bodySmall.copy(color = Color(0xFF4A6572))
                        )
                    }
                    
                    // Total students
                    Text(
                        text = "Total: ${students.size} Students",
                        style = Typography.bodySmall.copy(color = Color(0xFF4A6572)),
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $error", style = Typography.bodySmall, color = Color(0xFF4A6572))
                    }
                } else if (attendanceHistory != null) {
                    val history = attendanceHistory!!
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Use the students list that's already been populated from the API
                            val displayedStudents = if (students.isEmpty()) {
                                // If API data is not available, use class info from history
                                history.overallStats.students.map { apiStudent ->
                                    Student(
                                        name = apiStudent.name,
                                        // Use the correct field name from StudentInfo class
                                        id = apiStudent.name, // Using name as ID temporarily
                                        attendancePercentage = apiStudent.attendancePercentage
                                    )
                                }
                            } else {
                                students
                            }
                            
                            val finalDisplayedStudents = if (showAll) displayedStudents else displayedStudents.take(visibleCount)
                            
                            finalDisplayedStudents.forEachIndexed { index, student ->
                                StudentAttendanceItem(index + 1, student)
                                if (index < finalDisplayedStudents.size - 1) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = Color(0xFFEEEEEE)
                                    )
                                }
                            }
                            
                            if (!showAll && displayedStudents.size > visibleCount) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color(0xFF001E2F), CircleShape)
                                            .clickable { showAll = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Show More",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val displayedStudents = if (showAll) students else students.take(visibleCount)

                            displayedStudents.forEachIndexed { index, student ->
                                StudentAttendanceItem(index + 1, student)
                                if (index < displayedStudents.size - 1) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = Color(0xFFEEEEEE)
                                    )
                                }
                            }

                            if (!showAll && students.size > visibleCount) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color(0xFF001E2F), CircleShape)
                                            .clickable { showAll = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Show More",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Removed redundant attendance history section

                    Spacer(modifier = Modifier.height(80.dp))
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "$index.${student.name}", // Remove space after the dot to match image
                fontWeight = FontWeight.Medium,
                color = Color(0xFF001E2F),
                style = Typography.bodyLarge
            )
            Text(
                student.id,
                color = Color(0xFF4A6572),
                style = Typography.bodySmall
            )
        }
        Text(
            "${student.attendancePercentage}% Present", // Add "Present" text as shown in image
            fontWeight = FontWeight.Medium,
            color = Color(0xFF001E2F),
            style = Typography.bodyMedium
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
                    "Attendance Summary",
                    style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF001E2F)
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Close",
                        tint = Color(0xFF001E2F)
                    )
                }
            }
            
            Divider(color = Color(0xFFEEEEEE))
            
            // Student info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    student.name,
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF001E2F)
                )
                Text(
                    "ID: ${student.id}",
                    style = Typography.bodyMedium,
                    color = Color(0xFF4A6572)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Overall attendance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Overall Attendance",
                        style = Typography.bodyMedium,
                        color = Color(0xFF4A6572)
                    )
                    Text(
                        "${student.attendancePercentage}%",
                        style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (student.attendancePercentage >= 75) Color(0xFF00695C) else Color(0xFFD32F2F)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Attendance progress bar
                LinearProgressIndicator(
                    progress = { student.attendancePercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = if (student.attendancePercentage >= 75) Color(0xFF00695C) else Color(0xFFD32F2F),
                    trackColor = Color(0xFFEEEEEE)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Attendance legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Present legend
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(0xFF00695C), RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Present", style = Typography.bodySmall)
                    }
                    
                    // Excused legend
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(0xFFFFA726), RoundedCornerShape(2.dp))
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

data class Student(
    val name: String,
    val id: String,
    val attendancePercentage: Int
)

@Composable
fun QrCodeDialog(qrResponse: GenerateQrResponse, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Scan for Attendance", 
                    style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Decode and display QR Code Image
                val imageBitmap = remember(qrResponse.qrCodeImage) {
                    try {
                        val pureBase64Encoded = qrResponse.qrCodeImage.substringAfter("base64,")
                        val imageBytes = android.util.Base64.decode(pureBase64Encoded, android.util.Base64.DEFAULT)
                        android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)?.asImageBitmap()
                    } catch (e: Exception) {
                        Log.e("QrCodeDialog", "Error decoding Base64 image", e)
                        null
                    }
                }

                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .size(250.dp)
                            .padding(bottom = 16.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        "Error displaying QR code.", 
                        color = Color.Red, 
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Text(
                    "Attendance ID: ${qrResponse.attendanceId}", 
                    style = Typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Expires At: ${qrResponse.expiresAt}", // Consider formatting this date/time
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001E2F))
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SectionDetailScreenPreview() {
    SectionDetailScreen(courseName = "Sample Course", authToken = "sample_token")
}
