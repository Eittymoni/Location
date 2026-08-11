package com.zubayer.location_exam

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.zubayer.location_exam.databinding.ActivityMapsBinding
import com.zubayer.location_exam.repository.UserRepository
import com.zubayer.location_exam.viewModel.MapsViewModel


class MapsActivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var binding: ActivityMapsBinding
    private lateinit var map: GoogleMap

    private val userRepository = UserRepository(
        FirebaseFirestore.getInstance()
    )

    private val viewModel by viewModels<MapsViewModel> {

        object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                return MapsViewModel(userRepository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment

        mapFragment?.getMapAsync(this)
            ?: Toast.makeText(this, "Map not found", Toast.LENGTH_SHORT).show()
    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap

        val showAll = intent.getBooleanExtra("showAll", false)
        val userId = intent.getStringExtra("uid")

        if (showAll) {
            viewModel.loadAllUsers()
        } else if (userId != null) {
            viewModel.loadSingleUser(userId)
        }

        observeData()
    }

    private fun observeData() {

        viewModel.user.observe(this) { user ->

            user?.let {

                if (it.latitude != null && it.longitude != null) {

                    val pos = LatLng(it.latitude, it.longitude)

                    map.clear()

                    map.addMarker(
                        MarkerOptions()
                            .position(pos)
                            .title(it.username.ifEmpty { it.email })
                    )

                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(pos, 15f)
                    )
                }
            }
        }

        viewModel.userList.observe(this) { list ->

            map.clear()

            list.forEach {

                if (it.latitude != null && it.longitude != null) {

                    map.addMarker(
                        MarkerOptions()
                            .position(
                                LatLng(
                                    it.latitude,
                                    it.longitude
                                )
                            )
                            .title(it.username.ifEmpty { it.email })
                    )
                }
            }

            val dhaka = LatLng(23.8103, 90.4125)

            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(dhaka, 11f)
            )
        }
    }
}