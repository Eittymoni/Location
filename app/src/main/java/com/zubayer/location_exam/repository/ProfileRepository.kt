package com.zubayer.location_exam.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val db: FirebaseFirestore
) {

    suspend fun updateUsername(
        userId: String,
        name: String
    ): Result<Unit> {

        return try {

            db.collection("users")
                .document(userId)
                .update("username", name)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }
    }
}