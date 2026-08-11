package com.zubayer.location_exam

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zubayer.location_exam.adapter.UserAdapter
import com.zubayer.location_exam.databinding.ActivityFriendListBinding
import com.zubayer.location_exam.location.DeviceLocationProvider
import com.zubayer.location_exam.repository.AuthRepository
import com.zubayer.location_exam.repository.LocationRepository
import com.zubayer.location_exam.repository.UserRepository
import com.zubayer.location_exam.viewModel.FriendListViewModel
import kotlinx.coroutines.launch

class FriendListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendListBinding
    private lateinit var headerView: View
    private lateinit var tvNavName: TextView
    private lateinit var tvNavEmail: TextView
    private lateinit var tvNavLat: TextView
    private lateinit var tvNavLng: TextView

    private val authRepository = AuthRepository(
        FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance()
    )

    private val userRepository = UserRepository(
        FirebaseFirestore.getInstance()
    )

    private val locationRepository = LocationRepository(
        FirebaseFirestore.getInstance()
    )

    private lateinit var locationProvider: DeviceLocationProvider

    private val viewModel by viewModels<FriendListViewModel> {
        object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                return FriendListViewModel(
                    userRepository,
                    authRepository
                ) as T
            }
        }
    }


    private var isMenuOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ================= SETUP TOOLBAR & DRAWER =================
        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Get Header Views
        headerView = binding.navView.getHeaderView(0)
        tvNavName = headerView.findViewById(R.id.tvNavName)
        tvNavEmail = headerView.findViewById(R.id.tvNavEmail)
        tvNavLat = headerView.findViewById(R.id.tvNavLat)
        tvNavLng = headerView.findViewById(R.id.tvNavLng)

        headerView.setOnClickListener {
            val uid = authRepository.getCurrentUserId()
            startActivity(Intent(this, MapsActivity::class.java).apply {
                putExtra("uid", uid)
            })
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    val uid = authRepository.getCurrentUserId()
                    val email = authRepository.getCurrentUserEmail()
                    startActivity(Intent(this, MyProfileActivity::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("email", email)
                    })
                }

                R.id.nav_map -> {
                    startActivity(Intent(this, MapsActivity::class.java).apply {
                        putExtra("showAll", true)
                    })
                }

                R.id.nav_logout -> {
                    viewModel.logOut()
                    startActivity(Intent(this, AuthActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        locationProvider = DeviceLocationProvider(applicationContext)
        loadCurrentUser()
        checkLocationPermission()

        // ================= STEP 1: RecyclerView =================
        val adapter = UserAdapter{ selectedUser ->
            startActivity(Intent(this, MapsActivity::class.java).apply {
                putExtra("uid", selectedUser.userId)
            })
        }

        binding.userRecycler.layoutManager = LinearLayoutManager(this)
        binding.userRecycler.setHasFixedSize(true)
        binding.userRecycler.adapter = adapter

        // ================= STEP 2: Fetch users =================
        viewModel.fetchUsers()

        // ================= STEP 3: Observe users =================
        viewModel.userList.observe(this) { list ->
            val currentUid = authRepository.getCurrentUserId()

            // remove current user
            adapter.submitList(list.filter { it.userId != currentUid })
        }

        // ================= FAB MAIN =================
        binding.fabMain.setOnClickListener {
            if (isMenuOpen) closeMenu() else openMenu()
        }

        // ================= FAB PROFILE =================
        binding.fabProfile.setOnClickListener {
            val uid = authRepository.getCurrentUserId()
            val email = authRepository.getCurrentUserEmail()

            startActivity(Intent(this, MyProfileActivity::class.java).apply {
                putExtra("uid", uid)
                putExtra("email", email)
            })

            closeMenu()
        }

        // ================= FAB MAP =================
        binding.fabShowMap.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java).apply {
                putExtra("showAll", true)
            })
            closeMenu()
        }


        binding.fabLogout.setOnClickListener {

            viewModel.logOut()

            startActivity(
                Intent(this, AuthActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )

            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadCurrentUser() {

        val uid = authRepository.getCurrentUserId() ?: return

        lifecycleScope.launch {

            val user = userRepository.getUserById(uid)

            if (user != null) {

                tvNavName.text = user.username.ifEmpty { "No Name" }
                tvNavEmail.text = user.email
                tvNavLat.text = "Lat: ${user.latitude ?: 0.0}"
                tvNavLng.text = "Lng: ${user.longitude ?: 0.0}"

            } else {

                tvNavName.text = "User not found"

            }
        }
    }

    // ================= LOCATION =================
    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkLocationPermission() {
        if (!hasLocationPermission()) {

            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100
            )
        } else {
            lifecycleScope.launch {
                updateLocation()
            }
        }
    }

    // ================= PERMISSION RESULT =================
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            lifecycleScope.launch {
                updateLocation()
            }
        } else {

            // ⚠️ TODO: show message if user denies permission
            // Example: Toast or dialog explaining why location is needed
        }
    }


    private suspend fun updateLocation() {

        val location = locationProvider.getCurrentLocation()

        if (location == null) {

            Toast.makeText(
                this,
                "Unable to get current location",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val userId = authRepository.getCurrentUserId() ?: return

        val result = locationRepository.updateLocation(
            userId,
            location.latitude,
            location.longitude
        )

        if (result.isSuccess) {

            loadCurrentUser()

        } else {

            Toast.makeText(
                this,
                result.exceptionOrNull()?.message ?: "Location update failed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    // ================= FAB MENU =================
    private fun openMenu() {
        binding.fabProfile.visibility = View.VISIBLE
        binding.fabShowMap.visibility = View.VISIBLE
        binding.fabLogout.visibility = View.VISIBLE

        // ⚠️ TODO: add animation (rotation or scale)
        // Example: fabMain.animate().rotation(45f)

        isMenuOpen = true
    }

    private fun closeMenu() {
        binding.fabProfile.visibility = View.GONE
        binding.fabShowMap.visibility = View.GONE
        binding.fabLogout.visibility = View.GONE


        isMenuOpen = false
    }

    override fun onResume() {
        super.onResume()

        isMenuOpen = false
        closeMenu()

        viewModel.fetchUsers()
        loadCurrentUser()
        checkLocationPermission()
    }
}