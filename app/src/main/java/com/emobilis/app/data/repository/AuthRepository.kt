package com.emobilis.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.emobilis.app.data.model.Student
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun registerStudent(student: Student, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(student.email, password).await()
            val uid = result.user!!.uid
            val reg = "EMO-${System.currentTimeMillis().toString().takeLast(6)}"
            db.collection("students").document(uid)
                .set(student.copy(uid = uid, registrationNumber = reg)).await()
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user!!.uid
            val studentDoc = db.collection("students").document(uid).get().await()
            if (studentDoc.exists()) return Result.success("student")
            val staffDoc = db.collection("staff").document(uid).get().await()
            if (staffDoc.exists()) {
                val role = staffDoc.getString("role") ?: "lecturer"
                return Result.success(role)
            }
            Result.failure(Exception("User not found in database"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUid(): String? = auth.currentUser?.uid

    fun signOut() = auth.signOut()

    suspend fun getStudent(uid: String): Student? {
        return try {
            db.collection("students").document(uid).get().await().toObject(Student::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
