package com.emobilis.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emobilis.app.ui.theme.EmobilisPrimary
import com.emobilis.app.ui.theme.EmobilisSecondary
import com.emobilis.app.viewmodel.AuthState
import com.emobilis.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by vm.authState.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthState.Success) onLoginSuccess((state as AuthState.Success).role)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(EmobilisPrimary, EmobilisSecondary)))
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "EMOBILIS",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmobilisPrimary
                )
                Text(
                    "School Management System",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Divider()

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (state is AuthState.Error) {
                    Text(
                        (state as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = { vm.login(email, password) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = state !is AuthState.Loading
                ) {
                    if (state is AuthState.Loading)
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    else
                        Text("Sign In", fontSize = 16.sp)
                }

                TextButton(onClick = onNavigateToRegister) {
                    Text("New student? Register here")
                }
            }
        }
    }
}
