package com.emobilis.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.emobilis.app.data.model.Attendance
import com.emobilis.app.data.model.ComputerAlert
import kotlinx.coroutines.tasks.await

class AttendanceRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun signAttendance(attendance: Attendance): Result<Unit> {
        return try {
            db.collection("attendance").add(attendance).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStudentAttendance(uid: String): List<Attendance> {
        return try {
            db.collection("attendance")
                .whereEqualTo("studentUid", uid)
                .get().await()
                .toObjects(Attendance::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun sendComputerAlert(alert: ComputerAlert): Result<Unit> {
        return try {
            db.collection("computer_alerts").add(alert).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUnresolvedAlerts(): List<ComputerAlert> {
        return try {
            db.collection("computer_alerts")
                .whereEqualTo("resolved", false)
                .get().await()
                .toObjects(ComputerAlert::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
