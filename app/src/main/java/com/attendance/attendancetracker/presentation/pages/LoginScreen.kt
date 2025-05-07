package com.attendance.attendancetracker.presentation.pages

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.* 
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendance.attendancetracker.presentation.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: (isTeacher: Boolean) -> Unit = {}, 
    onSignUpClick: () -> Unit = {} 
) {
    val context = LocalContext.current

    // Input fields for Login
    var loginName by remember { mutableStateOf("") } 
    var loginId by remember { mutableStateOf("") } 
    var loginPassword by remember { mutableStateOf("") } 

    LaunchedEffect(viewModel.authState) {
        viewModel.authState?.let { result ->
            if (result.isSuccess) {
                val response = result.getOrNull()
                val userName = response?.name ?: "User"
                val isTeacher = response?.role == "teacher" 
                Toast.makeText(context, "Login Successful for $userName", Toast.LENGTH_SHORT).show()
                onLoginSuccess(isTeacher) 
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "An unknown error occurred"
                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                viewModel.clearAuthState() 
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Welcome Back!") }) 
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text("Log In to Your Account", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = loginName, onValueChange = { loginName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = loginId, onValueChange = { loginId = it }, label = { Text("Student/Staff ID") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = loginPassword, onValueChange = { loginPassword = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { 
                    if (loginName.isNotBlank() && loginId.isNotBlank() && loginPassword.isNotBlank()) {
                        viewModel.login(loginName, loginId, loginPassword)
                    } else {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
                onSignUpClick() 
                viewModel.clearAuthState() 
            }) {
                Text("Don't have an account? Sign Up") 
            }

            if (viewModel.authState?.isSuccess == true) { 
                 Spacer(modifier = Modifier.height(8.dp))
                 Button(onClick = { 
                        viewModel.logout(loginName, loginId, loginPassword) 
                    },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                 ) {
                    Text("Logout")
                 }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(onLoginSuccess = {}, onSignUpClick = {}) 
}
