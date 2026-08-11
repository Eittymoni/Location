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
import com.google.firebase.firestore.FirebaseFirestore
import com.zubayer.location_exam.databinding.ActivityMyProfileBinding
import com.zubayer.location_exam.repository.ProfileRepository
import com.zubayer.location_exam.repository.UserRepository
import com.zubayer.location_exam.viewModel.MyProfileViewModel

class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileBinding

    private val profileRepository = ProfileRepository(
        FirebaseFirestore.getInstance()
    )

    private val userRepository = UserRepository(
        FirebaseFirestore.getInstance()
    )

    private val viewModel by viewModels<MyProfileViewModel> {

        object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                return MyProfileViewModel(
                    profileRepository,
                    userRepository
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = intent.getStringExtra("uid") ?: return
        val email = intent.getStringExtra("email") ?: ""

        binding.email.text = email

        binding.btnUpdateUsername.setOnClickListener {

            val newName = binding.edtUsername.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(this, "Enter username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateUsername(uid, newName) { success ->

                if (success) {

                    Toast.makeText(
                        this,
                        "Updated!",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Update failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.loadUser(uid)

        viewModel.user.observe(this) { user ->

            user?.let {

                binding.edtUsername.setText(it.username)

            }
        }
    }
}