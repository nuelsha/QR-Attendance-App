package com.attendance.attendancetracker.presentation.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendance.attendancetracker.R
import com.attendance.attendancetracker.data.models.ClassItem
import com.attendance.attendancetracker.presentation.viewmodels.AuthViewModel
import com.attendance.attendancetracker.presentation.viewmodels.DashboardViewModel
import com.attendance.attendancetracker.ui.theme.Typography
import android.util.Log

data class ClassSection(val name: String, val studentCount: Int, val originalId: String)

@Composable
fun TeacherHomeScreen(
    authToken: String = "", // Receive auth token directly as parameter
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    onSectionClick: (String) -> Unit = {},
    onAddNewClassClick: (String) -> Unit = {}
) {
    val classes by dashboardViewModel.classes.observeAsState(initial = emptyList())
    val isLoading by dashboardViewModel.isLoading.observeAsState(initial = false)
    val error by dashboardViewModel.error.observeAsState(initial = null)

    val showAddCard = remember { mutableStateOf(false) }

    Log.d("TeacherHomeScreen", "Composition: authToken='${authToken}'")

    LaunchedEffect(authToken) {
        Log.d("TeacherHomeScreen", "LaunchedEffect triggered. Current token: '$authToken'")
        if (authToken.isNotBlank()) {
            Log.d("TeacherHomeScreen", "Token is valid. Loading dashboard with token: $authToken")
            dashboardViewModel.loadDashboard(authToken)
        } else {
            Log.d("TeacherHomeScreen", "Token is blank inside LaunchedEffect. Dashboard not loaded.")
        }
    }

    val classSections = remember(classes) {
        classes.map {
            ClassSection(
                name = it.className,
                studentCount = it.students?.size ?: 0,
                originalId = it.id
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECECEC))
    ) {
        Column {
            Header()

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Teacher’s Dashboard",
                            style = Typography.titleLarge.copy(
                                color = Color(0xFF001E2F),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Track student participation, generate reports, and stay organized",
                            style = Typography.bodyMedium.copy(color = Color(0xFF4A6572)),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.gg_qr),
                        contentDescription = "Logo",
                        modifier = Modifier.size(40.dp)
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (error != null) {
                    Text(
                        text = "Error: $error",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(classSections) { section ->
                            SectionCard(
                                section = section,
                                onClick = { onSectionClick(section.originalId) },
                                onDashboardClick = { onSectionClick(section.originalId) }
                            )
                        }

                        if (showAddCard.value) {
                            item {
                                AddClassCard(
                                    onAddClick = { className ->
                                        onAddNewClassClick(className)
                                        showAddCard.value = false
                                    },
                                    onDashboardClick = { /* Decide what this should do */ }
                                )
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { showAddCard.value = !showAddCard.value },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, "Add new class", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF001E2F))
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.scanin_logo_removebg_preview__1__2_layerstyle__1_),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(40.dp)
        )
    }
}

@Composable
fun SectionCard(
    section: ClassSection,
    onClick: () -> Unit,
    onDashboardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF001E2F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = section.name,
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${section.studentCount} Students",
                    style = Typography.bodySmall,
                    color = Color.Gray
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onDashboardClick,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.dash),
                        contentDescription = "Dashboard",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Dashboard",
                        style = Typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun AddClassCard(
    onAddClick: (String) -> Unit,
    onDashboardClick: () -> Unit
) {
    var className by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF001E2F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = className,
                onValueChange = { className = it },
                placeholder = { Text("Name of the class", color = Color.Gray) },
                textStyle = TextStyle(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )


            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onDashboardClick,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.dash),
                        contentDescription = "Dashboard",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dashboard", style = Typography.labelSmall)
                }

                OutlinedButton(
                    onClick = { onAddClick(className) },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ Add", style = Typography.labelSmall)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TeacherHomeScreenPreview() {
    TeacherHomeScreen(authToken = "sample-preview-token")
}
