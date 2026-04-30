package com.emobilis.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emobilis.app.data.model.Attendance
import com.emobilis.app.data.model.ComputerAlert
import com.emobilis.app.data.model.Student
import com.emobilis.app.data.repository.AttendanceRepository
import com.emobilis.app.util.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class AttendanceViewModel(private val context: Context) : ViewModel() {
    private val repo = AttendanceRepository()

    private val _attendanceStatus = MutableStateFlow("")
    val attendanceStatus: StateFlow<String> = _attendanceStatus

    private val _attendanceList = MutableStateFlow<List<Attendance>>(emptyList())
    val attendanceList: StateFlow<List<Attendance>> = _attendanceList

    private val _alerts = MutableStateFlow<List<ComputerAlert>>(emptyList())
    val alerts: StateFlow<List<ComputerAlert>> = _alerts

    fun signAttendance(student: Student) {
        viewModelScope.launch {
            _attendanceStatus.value = "Checking location..."
            val atSchool = LocationHelper.isStudentAtSchool(context)
            if (!atSchool) {
                _attendanceStatus.value = "❌ You must be at school to sign attendance!"
                return@launch
            }
            val now = LocalDateTime.now()
            val attendance = Attendance(
                studentUid = student.uid,
                studentName = student.fullName,
                date = now.toLocalDate().toString(),
                time = now.toLocalTime().toString().take(8),
                signedAtSchool = true
            )
            val result = repo.signAttendance(attendance)
            _attendanceStatus.value = if (result.isSuccess)
                "✅ Attendance signed successfully!"
            else
                "❌ Failed: ${result.exceptionOrNull()?.message}"
        }
    }

    fun loadAttendance(uid: String) {
        viewModelScope.launch {
            _attendanceList.value = repo.getStudentAttendance(uid)
        }
    }

    fun sendComputerAlert(student: Student, issue: String) {
        viewModelScope.launch {
            val alert = ComputerAlert(
                studentName = student.fullName,
                studentUid = student.uid,
                computerNumber = student.computerNumber,
                laboratory = student.laboratory,
                issue = issue,
                timestamp = System.currentTimeMillis()
            )
            repo.sendComputerAlert(alert)
        }
    }

    fun loadAlerts() {
        viewModelScope.launch {
            _alerts.value = repo.getUnresolvedAlerts()
        }
    }
}
