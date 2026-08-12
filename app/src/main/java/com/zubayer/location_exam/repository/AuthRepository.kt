package com.zubayer.location_exam.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zubayer.location_exam.model.AppUsers
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<Unit> {

        return try {

            val result = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val userId = result.user?.uid
                ?: throw Exception("User ID not found")

            val finalUsername = username.ifEmpty { email.substringBefore("@") }

            val user = AppUsers(
                userId = userId,
                email = email,
                username = finalUsername
            )

            db.collection("users")
                .document(userId)
                .set(user)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {

        return try {

            auth.signInWithEmailAndPassword(email, password)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUserId() = auth.currentUser?.uid

    fun getCurrentUserEmail() = auth.currentUser?.email
}