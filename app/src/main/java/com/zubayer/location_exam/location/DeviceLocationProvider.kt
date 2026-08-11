package com.zubayer.location_exam.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class DeviceLocationProvider(
    private val context: Context
) {

    private val fused = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Location? {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        return try {

            fused.lastLocation.await()

        } catch (e: Exception) {

            null

        }
    }
}