package com.zubayer.location_exam.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LocationRepository(private val db: FirebaseFirestore
) {

    suspend fun updateLocation(
        userId: String,
        latitude: Double,
        longitude: Double
    ): Result<Unit> {

        return try {

            db.collection("users")
                .document(userId)
                .update(
                    mapOf(
                        "latitude" to latitude,
                        "longitude" to longitude
                    )
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }
    }
}