package com.zubayer.location_exam

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zubayer.location_exam.databinding.ActivityAuthBinding
import com.zubayer.location_exam.repository.AuthRepository
import com.zubayer.location_exam.viewModel.AuthViewModel

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val repo = AuthRepository(
        FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance()
    )

    private val viewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToggleGroup()
        setupSignInForm()
        setupSignUpForm()
        observeViewModel()
    }

    // Handle switching between Sign In and Sign Up forms
    private fun setupToggleGroup() {
        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnToggleSignIn -> {
                        binding.layoutSignIn.visibility = View.VISIBLE
                        binding.layoutSignUp.visibility = View.GONE
                    }
                    R.id.btnToggleSignUp -> {
                        binding.layoutSignIn.visibility = View.GONE
                        binding.layoutSignUp.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    // Handle Sign In submission
    private fun setupSignInForm() {
        binding.btnSubmitSignIn.setOnClickListener {
            val email = binding.etSignInEmail.text.toString().trim()
            val password = binding.etSignInPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
            // Optional: Implement password reset logic here
        }
    }

    // Handle Sign Up submission
    private fun setupSignUpForm() {
        binding.btnSubmitSignUp.setOnClickListener {
            val name = binding.etSignUpName.text.toString().trim()
            val email = binding.etSignUpEmail.text.toString().trim()
            val password = binding.etSignUpPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(name, email, password)
        }
    }

    // Observe login and registration LiveData results
    private fun observeViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                binding.btnSubmitSignIn.text = "Signing In..."
                binding.btnSubmitSignUp.text = "Signing Up..."
            } else {
                binding.btnSubmitSignIn.text = "Sign In"
                binding.btnSubmitSignUp.text = "Sign Up"
            }
            binding.btnSubmitSignIn.isEnabled = !isLoading
            binding.btnSubmitSignUp.isEnabled = !isLoading
        }

        viewModel.registerResult.observe(this) { (success, message) ->
            if (success) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                navigateToFriendList()
            } else {
                Toast.makeText(this, message ?: "Registration Failed", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.loginResult.observe(this) { (success, message) ->
            if (success) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                navigateToFriendList()
            } else {
                Toast.makeText(this, message ?: "Login Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToFriendList() {
        startActivity(Intent(this, FriendListActivity::class.java))
        finish()
    }
}