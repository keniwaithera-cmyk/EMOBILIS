package com.emobilis.app.ui.student

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emobilis.app.data.model.Student
import com.emobilis.app.ui.theme.EmobilisAccent
import com.emobilis.app.ui.theme.EmobilisPrimary
import com.emobilis.app.viewmodel.AttendanceViewModel
import com.emobilis.app.viewmodel.AuthViewModel
import com.emobilis.app.viewmodel.MessageViewModel
import com.emobilis.app.data.model.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentPortalScreen(
    onLogout: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    val student by vm.currentStudent.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Home", "Attendance", "Messages", "Alerts", "Profile")

    LaunchedEffect(Unit) { vm.loadCurrentStudent() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Portal${student?.fullName?.let { " - $it" } ?: ""}",
                    maxLines = 1) },
                actions = {
                    IconButton(onClick = { vm.signOut(); onLogout() }) {
                        Icon(Icons.Default.Logout, "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EmobilisPrimary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { i, title ->
                    NavigationBarItem(
                        selected = selectedTab == i,
                        onClick = { selectedTab = i },
                        icon = {
                            Icon(
                                when (i) {
                                    0 -> Icons.Default.Home
                                    1 -> Icons.Default.CheckCircle
                                    2 -> Icons.Default.Message
                                    3 -> Icons.Default.Warning
                                    else -> Icons.Default.Person
                                },
                                contentDescription = title
                            )
                        },
                        label = { Text(title, fontSize = 10.sp) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                0 -> StudentHomeTab(student)
                1 -> AttendanceTab(student)
                2 -> MessageTab(student)
                3 -> ComputerAlertTab(student)
                4 -> ProfileTab(student, onLogout = { vm.signOut(); onLogout() })
            }
        }
    }
}

@Composable
fun StudentHomeTab(student: Student?) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EmobilisPrimary)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Welcome back!", color = Color.White, fontSize = 14.sp)
                    Text(
                        student?.fullName ?: "Loading...",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Reg: ${student?.registrationNumber ?: ""}",
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoCard("Course", student?.course ?: "-", Modifier.weight(1f))
                InfoCard("Lab", student?.laboratory ?: "-", Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoCard("Computer", student?.computerNumber ?: "-", Modifier.weight(1f))
                InfoCard("Fees Balance", "KSh ${student?.feesBalance ?: 0.0}", Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun InfoCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Composable
fun AttendanceTab(student: Student?, context: Context = LocalContext.current) {
    val vm: AttendanceViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            @Suppress("UNCHECKED_CAST") (AttendanceViewModel(context) as T)
    })
    val status by vm.attendanceStatus.collectAsState()
    val list by vm.attendanceList.collectAsState()

    LaunchedEffect(student) { student?.let { vm.loadAttendance(it.uid) } }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Attendance", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = EmobilisPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Sign attendance only works when you are physically at EMOBILIS",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { student?.let { vm.signAttendance(it) } },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Sign Today's Attendance")
                    }
                    if (status.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(status, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
        item {
            Text("Attendance History", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
        if (list.isEmpty()) {
            item {
                Text("No attendance records yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        items(list) { att ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(att.date, fontWeight = FontWeight.Medium)
                    Text(att.time.take(8), fontSize = 13.sp)
                    Text(if (att.signedAtSchool) "✅ Present" else "❌ Absent")
                }
            }
        }
    }
}

@Composable
fun MessageTab(student: Student?) {
    val vm: MessageViewModel = viewModel()
    var messageText by remember { mutableStateOf("") }
    var absenceMode by remember { mutableStateOf(false) }
    var sent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Messages & Notifications", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Absence Notification", fontWeight = FontWeight.SemiBold)
                Text("Send a message if you won't attend class today", fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = absenceMode, onCheckedChange = { absenceMode = it; sent = false })
                    Spacer(Modifier.width(8.dp))
                    Text(if (absenceMode) "Absence Notice Mode ON" else "Regular Message Mode")
                }
            }
        }

        OutlinedTextField(
            value = messageText,
            onValueChange = { messageText = it },
            label = { Text(if (absenceMode) "Reason for absence..." else "Message / Suggestion...") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 5
        )

        Button(
            onClick = {
                student?.let {
                    val msg = Message(
                        senderUid = it.uid,
                        senderName = it.fullName,
                        receiverUid = "SCHOOL",
                        content = messageText,
                        timestamp = System.currentTimeMillis(),
                        type = if (absenceMode) "absence" else "general"
                    )
                    vm.sendMessage(msg)
                    sent = true
                    messageText = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = messageText.isNotBlank()
        ) {
            Icon(if (absenceMode) Icons.Default.Sms else Icons.Default.Send, null)
            Spacer(Modifier.width(8.dp))
            Text(if (absenceMode) "Send Absence Notification" else "Send Message")
        }

        if (sent) Text("✅ Message sent successfully!", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ComputerAlertTab(student: Student?, context: Context = LocalContext.current) {
    val vm: AttendanceViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            @Suppress("UNCHECKED_CAST") (AttendanceViewModel(context) as T)
    })
    var issue by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Report Computer Issue", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Default.Warning, null, tint = EmobilisAccent, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(8.dp))
                Text("Alert will automatically include:", fontWeight = FontWeight.SemiBold)
                Text("• Computer: ${student?.computerNumber ?: "-"}")
                Text("• Lab: ${student?.laboratory ?: "-"}")
                Text("• Your Name: ${student?.fullName ?: "-"}")
            }
        }

        OutlinedTextField(
            value = issue,
            onValueChange = { issue = it; sent = false },
            label = { Text("Describe the issue with your computer...") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 5
        )

        Button(
            onClick = {
                student?.let {
                    vm.sendComputerAlert(it, issue)
                    sent = true
                    issue = ""
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            enabled = issue.isNotBlank()
        ) {
            Icon(Icons.Default.Warning, null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("🚨 Send Alert to Lab Technician", color = Color.White)
        }

        if (sent) Text("✅ Alert sent to Lab Technician!", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProfileTab(student: Student?, onLogout: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("My Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EmobilisPrimary)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(student?.fullName ?: "-", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(student?.registrationNumber ?: "", color = Color.White.copy(alpha = 0.8f))
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProfileRow("Email", student?.email ?: "-")
                    ProfileRow("Phone", student?.phone ?: "-")
                    ProfileRow("Course", student?.course ?: "-")
                    ProfileRow("Laboratory", student?.laboratory ?: "-")
                    ProfileRow("Computer", student?.computerNumber ?: "-")
                    ProfileRow("Fees Balance", "KSh ${student?.feesBalance ?: 0.0}")
                }
            }
        }
        item {
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text("Sign Out")
            }
        }
    }
}

@Composable
fun ProfileRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
    Divider()
}
