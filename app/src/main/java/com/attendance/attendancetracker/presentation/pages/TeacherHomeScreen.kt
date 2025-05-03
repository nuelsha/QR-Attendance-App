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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.attendance.attendancetracker.R
import com.attendance.attendancetracker.ui.theme.Typography

data class ClassSection(val name: String, val studentCount: Int)

@Composable
fun TeacherHomeScreen(
    classSections: List<ClassSection> = listOf(
        ClassSection("Section 1", 51),
        ClassSection("Section 2", 49),
        ClassSection("Section 3", 50),
        ClassSection("Section 4", 50),
        ClassSection("Section 5", 50),
    ),
    onSectionClick: (String) -> Unit = {},
    onAddNewClassClick: (String) -> Unit = {}
) {
    val showAddCard = remember { mutableStateOf(false) }

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

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(classSections) { section ->
                        SectionCard(
                            section = section,
                            onClick = { onSectionClick(section.name) },
                            onDashboardClick = { onSectionClick(section.name) }
                        )
                    }

                    if (showAddCard.value) {
                        item {
                            AddClassCard(
                                onAddClick = { className ->
                                    showAddCard.value = false
                                    onAddNewClassClick(className)
                                },
                                onDashboardClick = {
                                    // Handle dashboard action if needed
                                }
                            )
                        }
                    }
                }
            }
        }

        // Button matching the first image
        Button(
            onClick = { showAddCard.value = true },
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, Color(0xFF001E2F)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color(0xFF001E2F)
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add New Class",
                tint = Color(0xFF001E2F)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add New Class",
                style = Typography.labelLarge.copy(fontWeight = FontWeight.Medium)
            )
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
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF001E2F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = section.name,
                    style = Typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${section.studentCount} students",
                    style = Typography.bodySmall.copy(color = Color.Gray)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDashboardClick,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dash),
                        contentDescription = "Dashboard",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Dashboard",
                        style = Typography.labelSmall
                    )
                }

                IconButton(
                    onClick = { /* TODO: Handle options click */ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.vector),
                        contentDescription = "Options",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
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
    TeacherHomeScreen()
}
