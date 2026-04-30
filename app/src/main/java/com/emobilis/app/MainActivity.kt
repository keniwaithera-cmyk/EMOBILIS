package com.emobilis.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emobilis.app.ui.auth.LoginScreen
import com.emobilis.app.ui.auth.RegisterScreen
import com.emobilis.app.ui.lecturer.LecturerPortalScreen
import com.emobilis.app.ui.student.StudentPortalScreen
import com.emobilis.app.ui.technician.TechnicianPortalScreen
import com.emobilis.app.ui.theme.EmobilisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmobilisTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {

                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { role ->
                                val dest = when (role) {
                                    "lecturer"       -> "lecturer_portal"
                                    "lab_technician" -> "technician_portal"
                                    else             -> "student_portal"
                                }
                                navController.navigate(dest) {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToRegister = { navController.navigate("register") }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            onRegistered = {
                                navController.navigate("student_portal") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("student_portal") {
                        StudentPortalScreen(
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("lecturer_portal") {
                        LecturerPortalScreen(
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("technician_portal") {
                        TechnicianPortalScreen(
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
