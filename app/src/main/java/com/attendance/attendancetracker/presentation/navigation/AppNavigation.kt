package com.attendance.attendancetracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.attendance.attendancetracker.presentation.pages.AttendanceSummaryScreen
import com.attendance.attendancetracker.presentation.pages.CourseDashboardScreen
import com.attendance.attendancetracker.presentation.pages.LoginScreen
import com.attendance.attendancetracker.presentation.pages.OnboardingScreen
import com.attendance.attendancetracker.presentation.pages.QRGeneratorScreen
import com.attendance.attendancetracker.presentation.pages.QRScannerScreen
import com.attendance.attendancetracker.presentation.pages.SectionDetailScreen
import com.attendance.attendancetracker.presentation.pages.SignUpScreen
import com.attendance.attendancetracker.presentation.pages.StudentHomeScreen
import com.attendance.attendancetracker.presentation.pages.TeacherHomeScreen
import com.attendance.attendancetracker.presentation.viewmodels.AuthViewModel
import android.util.Log
import android.net.Uri

object Routes {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val STUDENT_HOME = "student_home"
    const val TEACHER_HOME = "teacher_home"
    private const val COURSE_DASHBOARD_BASE = "course_dashboard_screen"
    const val COURSE_DASHBOARD = "$COURSE_DASHBOARD_BASE/{classId}/{displayCourseName}/{teacherName}"
    const val QR_SCANNER = "qr_scanner/{classId}"
    const val SECTION_DETAIL = "section_detail/{courseName}"
    const val ATTENDANCE_SUMMARY = "attendance_summary/{sectionName}"
    const val QR_GENERATOR = "qr_generator/{courseName}"

    fun courseDashboard(classId: String, displayCourseName: String, teacherName: String) =
        "$COURSE_DASHBOARD_BASE/${Uri.encode(classId)}/${Uri.encode(displayCourseName)}/${Uri.encode(teacherName)}"

    fun qrScanner(classId: String) = "qr_scanner/${Uri.encode(classId)}"
    fun sectionDetail(courseName: String) = "section_detail/${Uri.encode(courseName)}"
    fun attendanceSummary(sectionName: String) = "attendance_summary/${Uri.encode(sectionName)}"
    fun qrGenerator(courseName: String) = "qr_generator/${Uri.encode(courseName)}"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.ONBOARDING
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    Log.d("AppNavigation", "Creating shared AuthViewModel at NavHost level")

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onGetStartedClick = { navController.navigate(Routes.SIGNUP) },
                onSkipClick = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.LOGIN) {
            Log.d("AppNavigation", "LOGIN route - Using shared AuthViewModel, authState: ${authViewModel.authState?.isSuccess}")
            LoginScreen(
                onSignUpClick = {
                    navController.navigate(Routes.SIGNUP)
                },
                onLoginSuccess = { isTeacher ->
                    val destination = if (isTeacher) Routes.TEACHER_HOME else Routes.STUDENT_HOME
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(Routes.SIGNUP) {
            Log.d("AppNavigation", "SIGNUP route - Using shared AuthViewModel, authState: ${authViewModel.authState?.isSuccess}")
            SignUpScreen(
                onLoginClick = { navController.navigate(Routes.LOGIN) },
                onSignUpSuccess = { isTeacher ->
                    val destination = if (isTeacher) Routes.TEACHER_HOME else Routes.STUDENT_HOME
                    navController.navigate(destination) {
                        popUpTo(Routes.SIGNUP) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(Routes.STUDENT_HOME) {
            val currentAuthState = authViewModel.authState
            val token = currentAuthState?.getOrNull()?.token ?: ""
            Log.d("AppNavigation", "STUDENT_HOME - Using shared AuthViewModel, currentAuthState: ${currentAuthState?.isSuccess}, token: '$token'")

            StudentHomeScreen(
                studentName = currentAuthState?.getOrNull()?.name ?: "Student",
                authToken = token,
                onCourseClick = { classId, displayCourseName, teacherName ->
                    navController.navigate(Routes.courseDashboard(classId, displayCourseName, teacherName))
                },
                onScanClick = { classId ->
                    navController.navigate(Routes.qrScanner(classId))
                }
            )
        }

        composable(Routes.TEACHER_HOME) {
            val currentAuthState = authViewModel.authState
            val token = currentAuthState?.getOrNull()?.token ?: ""
            Log.d("AppNavigation", "TEACHER_HOME - Using shared AuthViewModel, currentAuthState: ${currentAuthState?.isSuccess}, token: '$token'")

            TeacherHomeScreen(
                authToken = token,
                onSectionClick = { courseName ->
                    navController.navigate(Routes.sectionDetail(courseName))
                }
            )
        }

        composable(
            route = Routes.COURSE_DASHBOARD,
            arguments = listOf(
                navArgument("classId") { type = NavType.StringType },
                navArgument("displayCourseName") { type = NavType.StringType },
                navArgument("teacherName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("classId")?.let { Uri.decode(it) } ?: ""
            val displayCourseName = backStackEntry.arguments?.getString("displayCourseName")?.let { Uri.decode(it) } ?: "Course"
            val teacherName = backStackEntry.arguments?.getString("teacherName")?.let { Uri.decode(it) } ?: "Teacher"

            val authResult = authViewModel.authState?.getOrNull()
            val token = authResult?.token ?: ""

            if (token.isBlank()) {
                Log.e("AppNavigation", "COURSE_DASHBOARD: Auth token is blank. Navigating to login.")
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                return@composable
            }

            Log.d("AppNavigation", "Navigating to CourseDashboardScreen with classId: $classId, courseName: $displayCourseName, teacher: $teacherName")

            CourseDashboardScreen(
                classId = classId,
                displayCourseName = displayCourseName,
                teacherName = teacherName,
                authToken = token,
                onBackClick = { navController.popBackStack() },
                onScanClick = {
                    navController.navigate(Routes.qrScanner(classId))
                }
            )
        }

        composable(
            route = Routes.QR_SCANNER,
            arguments = listOf(navArgument("classId") { type = NavType.StringType })
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("classId")?.let { Uri.decode(it) } ?: ""
            val authResult = authViewModel.authState?.getOrNull()
            val token = authResult?.token ?: ""

            val teacherMap = remember {
                mapOf(
                    "Cyber Security" to "Senayit Demisse",
                    "Operating System" to "Senayit Demisse",
                    "Mobile" to "Sara Mohammed",
                    "Artificial Intelligence" to "Manyazewal Eshetu",
                    "Graphics" to "Abebe Tessema",
                    "Operating System 2" to "Teshome Chane"
                )
            }

            QRScannerScreen(
                courseName = classId,
                teacherName = teacherMap[classId] ?: "Unknown Teacher",
                onBackClick = { navController.popBackStack() },
                authToken = token,
                classId = classId
            )
        }

        composable(
            route = Routes.SECTION_DETAIL,
            arguments = listOf(navArgument("courseName") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseName = backStackEntry.arguments?.getString("courseName")?.let { Uri.decode(it) } ?: "DefaultCourse"
            val authResult = authViewModel.authState
            val token = authResult?.getOrNull()?.token ?: ""

            SectionDetailScreen(
                courseName = courseName,
                authToken = token,
                onBackClick = { navController.popBackStack() },
                onGenerateQRClick = { actualCourseName ->
                    navController.navigate(Routes.qrGenerator(actualCourseName))
                }
            )
        }

        composable(
            route = Routes.ATTENDANCE_SUMMARY,
            arguments = listOf(navArgument("sectionName") { type = NavType.StringType })
        ) { backStackEntry ->
            val sectionName = backStackEntry.arguments?.getString("sectionName")?.let { Uri.decode(it) } ?: "Section 1"
            val authResult = authViewModel.authState?.getOrNull()
            val token = authResult?.token ?: ""

            AttendanceSummaryScreen(
                sectionName = sectionName,
                authToken = token,
                onBackClick = { navController.popBackStack() },
                onAddNewStudentClick = {
                    // In a real app, you would navigate to a form to add a new student
                }
            )
        }

        composable(
            route = Routes.QR_GENERATOR,
            arguments = listOf(navArgument("courseName") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseNameArg = backStackEntry.arguments?.getString("courseName")?.let { Uri.decode(it) } ?: ""
            val teacherMap = remember {
                mapOf(
                    "Cyber Security" to "Senayit Demisse",
                    "Operating System" to "Senayit Demisse",
                    "Mobile" to "Sara Mohammed",
                    "Artificial Intelligence" to "Manyazewal Eshetu",
                    "Graphics" to "Abebe Tessema",
                    "Operating System 2" to "Teshome Chane",
                    "DefaultCourse" to "N/A"
                )
            }
            val teacherName = teacherMap[courseNameArg] ?: "Unknown Teacher"

            QRGeneratorScreen(
                courseName = courseNameArg,
                teacherName = teacherName,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
