package com.emobilis.app.ui.lecturer

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emobilis.app.data.model.Message
import com.emobilis.app.data.model.Student
import com.emobilis.app.ui.theme.EmobilisAccent
import com.google.firebase.firestore.FirebaseFirestore


val LecturerGreen = Color(0xFF1B5E20)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturerPortalScreen(onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dashboard", "Students", "Messages")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lecturer Portal") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LecturerGreen,
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
                                    0 -> Icons.Default.Home        // Dashboard → Home
                                    1 -> Icons.Default.People
                                    else -> Icons.Default.Email    // Message → Email
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
                0 -> LecturerDashboard()
                1 -> StudentListTab()
                2 -> LecturerMessagesTab()
            }
        }
    }
}

@Composable
fun LecturerDashboard() {
    val db = FirebaseFirestore.getInstance()
    var studentCount  by remember { mutableIntStateOf(0) }
    var pendingAlerts by remember { mutableIntStateOf(0) }
    var reminderSent  by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.collection("students").get()
            .addOnSuccessListener { studentCount = it.size() }
        db.collection("computer_alerts").whereEqualTo("resolved", false).get()
            .addOnSuccessListener { pendingAlerts = it.size() }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LecturerGreen)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Lecturer Dashboard", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("EMOBILIS School Management", color = Color.White.copy(alpha = 0.8f))
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Total Students", "$studentCount", Modifier.weight(1f))
                StatCard("Pending Alerts", "$pendingAlerts",
                    Modifier.weight(1f),
                    valueColor = if (pendingAlerts > 0) Color.Red else Color(0xFF2E7D32))
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Fees Reminder", fontWeight = FontWeight.Bold)
                    Text("Send an installment reminder to all students", fontSize = 12.sp)
                    Button(
                        onClick = {
                            db.collection("students").get().addOnSuccessListener { snap ->
                                snap.documents.forEach { doc ->
                                    val msg = Message(
                                        senderUid = "SCHOOL",
                                        senderName = "School Administration",
                                        receiverUid = doc.id,
                                        content = "Dear student, please remember to pay your fees installment. Contact admin for any queries.",
                                        timestamp = System.currentTimeMillis(),
                                        type = "fees_reminder"
                                    )
                                    db.collection("messages").add(msg)
                                }
                                reminderSent = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = EmobilisAccent)
                    ) {
                        Icon(Icons.Default.Notifications, null)
                        Spacer(Modifier.width(8.dp))
                        Text("💰 Send Fees Reminder to All Students")
                    }
                    if (reminderSent) Text("✅ Fees reminder sent to all students!", color = Color(0xFF2E7D32))
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = Color.Unspecified) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = valueColor)
        }
    }
}

@Composable
fun StudentListTab() {
    val db = FirebaseFirestore.getInstance()
    var students     by remember { mutableStateOf<List<Student>>(emptyList()) }
    var classMessage by remember { mutableStateOf("") }
    var messageSent  by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.collection("students").get().addOnSuccessListener { snap ->
            students = snap.toObjects(Student::class.java)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("My Students (${students.size})", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Broadcast Class Notice", fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = classMessage,
                        onValueChange = { classMessage = it; messageSent = false },
                        label = { Text("e.g. Class postponed to 3 PM...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            students.forEach { s ->
                                val msg = Message(
                                    senderUid = "LECTURER",
                                    senderName = "Lecturer",
                                    receiverUid = s.uid,
                                    content = classMessage,
                                    timestamp = System.currentTimeMillis(),
                                    type = "class_notice"
                                )
                                db.collection("messages").add(msg)
                            }
                            messageSent = true
                            classMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = classMessage.isNotBlank()
                    ) {
                        Text("📢 Send Class Notice to All (${students.size})")
                    }
                    if (messageSent) Text("✅ Sent to all students!", color = Color(0xFF2E7D32))
                }
            }
        }
        items(students) { s ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(s.fullName, fontWeight = FontWeight.Medium)
                    Text("${s.course}  •  ${s.laboratory}  •  ${s.computerNumber}", fontSize = 12.sp)
                    Text("Reg: ${s.registrationNumber}  •  ${s.email}", fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun LecturerMessagesTab() {
    val db = FirebaseFirestore.getInstance()
    var messages by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("messages")
            .whereEqualTo("receiverUid", "SCHOOL")
            .get()
            .addOnSuccessListener { snap ->
                messages = snap.documents.mapNotNull { it.data }
            }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Student Messages (${messages.size})", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        if (messages.isEmpty()) {
            item { Text("No messages yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(messages) { msg ->
            val type = msg["type"] as? String ?: "general"
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (type == "absence") Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(msg["senderName"] as? String ?: "Unknown", fontWeight = FontWeight.Bold)
                        Text(
                            if (type == "absence") "🟡 Absence" else "💬 General",
                            fontSize = 12.sp,
                            color = if (type == "absence") Color(0xFFE65100) else Color(0xFF1565C0)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(msg["content"] as? String ?: "", fontSize = 14.sp)
                }
            }
        }
    }
}
