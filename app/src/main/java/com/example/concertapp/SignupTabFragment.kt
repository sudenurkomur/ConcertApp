package com.example.concertapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.concertapp.databinding.FragmentSignupTabBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupTabFragment : Fragment() {

    private var _binding: FragmentSignupTabBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Binding'i başlat
        _binding = FragmentSignupTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString().trim()
            val password = binding.signupPassword.text.toString().trim()
            val confirmPassword = binding.signupConfirm.text.toString().trim()

            when {
                email.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please enter your email", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please enter your password", Toast.LENGTH_SHORT).show()
                }
                confirmPassword.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please confirm your password", Toast.LENGTH_SHORT).show()
                }
                password != confirmPassword -> {
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    registerUser(email, password)
                }
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Kullanıcı kaydı başarılı
                    val user = auth.currentUser
                    user?.let {
                        saveUserToDatabase(it.uid, email)
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Signup failed"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToDatabase(userId: String, email: String) {
        // Firebase Realtime Database referansı
        val usersRef = database.getReference("members")

        // Kullanıcı bilgilerini veri tabanına ekleyelim
        val userMap = hashMapOf(
            "userId" to userId,
            "email" to email
        )

        usersRef.child(userId).setValue(userMap)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "User saved to database", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error saving user: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
