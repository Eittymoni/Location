package com.zubayer.location_exam.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.zubayer.location_exam.model.AppUsers
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val db: FirebaseFirestore
) {

    suspend fun saveUser(user: AppUsers): Result<Unit> {

        return try {

            db.collection("users")
                .document(user.userId)
                .set(user)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }
    }

    suspend fun getAllUsers(): List<AppUsers> {

        return try {

            val snapshot = db.collection("users")
                .get()
                .await()

            snapshot.documents.mapNotNull {
                it.toObject(AppUsers::class.java)
            }

        } catch (e: Exception) {

            emptyList()

        }
    }

    suspend fun getUserById(id: String): AppUsers? {

        return try {

            db.collection("users")
                .document(id)
                .get()
                .await()
                .toObject(AppUsers::class.java)

        } catch (e: Exception) {

            null

        }
    }
}