package com.emobilis.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emobilis.app.AppConstants
import com.emobilis.app.data.model.Student
import com.emobilis.app.ui.theme.EmobilisPrimary
import com.emobilis.app.viewmodel.AuthState
import com.emobilis.app.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBack: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    var fullName       by remember { mutableStateOf("") }
    var email          by remember { mutableStateOf("") }
    var phone          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf("") }
    var selectedLab    by remember { mutableStateOf("") }
    var selectedPC     by remember { mutableStateOf("") }
    var courseExpanded by remember { mutableStateOf(false) }
    var labExpanded    by remember { mutableStateOf(false) }
    var pcExpanded     by remember { mutableStateOf(false) }

    val state by vm.authState.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthState.Success) onRegistered()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Registration") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EmobilisPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Personal Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        OutlinedTextField(value = fullName, onValueChange = { fullName = it },
                            label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = email, onValueChange = { email = it },
                            label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = phone, onValueChange = { phone = it },
                            label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = password, onValueChange = { password = it },
                            label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(), singleLine = true)
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Course & Lab Assignment", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        ExposedDropdownMenuBox(expanded = courseExpanded, onExpandedChange = { courseExpanded = it }) {
                            OutlinedTextField(value = selectedCourse, onValueChange = {}, readOnly = true,
                                label = { Text("Select Course") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor())
                            ExposedDropdownMenu(expanded = courseExpanded, onDismissRequest = { courseExpanded = false }) {
                                AppConstants.COURSES.forEach { course ->
                                    DropdownMenuItem(text = { Text(course) }, onClick = { selectedCourse = course; courseExpanded = false })
                                }
                            }
                        }

                        ExposedDropdownMenuBox(expanded = labExpanded, onExpandedChange = { labExpanded = it }) {
                            OutlinedTextField(value = selectedLab, onValueChange = {}, readOnly = true,
                                label = { Text("Select Laboratory") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = labExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor())
                            ExposedDropdownMenu(expanded = labExpanded, onDismissRequest = { labExpanded = false }) {
                                AppConstants.LABORATORIES.forEach { lab ->
                                    DropdownMenuItem(text = { Text(lab) }, onClick = { selectedLab = lab; labExpanded = false })
                                }
                            }
                        }

                        ExposedDropdownMenuBox(expanded = pcExpanded, onExpandedChange = { pcExpanded = it }) {
                            OutlinedTextField(value = selectedPC, onValueChange = {}, readOnly = true,
                                label = { Text("Select Computer Number") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = pcExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor())
                            ExposedDropdownMenu(expanded = pcExpanded, onDismissRequest = { pcExpanded = false }) {
                                AppConstants.COMPUTER_NUMBERS.forEach { pc ->
                                    DropdownMenuItem(text = { Text(pc) }, onClick = { selectedPC = pc; pcExpanded = false })
                                }
                            }
                        }
                    }
                }
            }

            item {
                if (state is AuthState.Error) {
                    Text((state as AuthState.Error).message, color = MaterialTheme.colorScheme.error)
                }
                Button(
                    onClick = {
                        vm.register(
                            Student(
                                fullName = fullName,
                                email = email,
                                phone = phone,
                                course = selectedCourse,
                                laboratory = selectedLab,
                                computerNumber = selectedPC
                            ),
                            password
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    enabled = state !is AuthState.Loading
                ) {
                    if (state is AuthState.Loading)
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    else
                        Text("Register", fontSize = 16.sp)
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
