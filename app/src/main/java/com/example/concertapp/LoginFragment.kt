package com.example.concertapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.concertapp.api.SupabaseClient
import com.example.concertapp.databinding.FragmentLoginTabBinding
import com.example.concertapp.models.User
import com.example.navdrawerkotpractice.AdminFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment(R.layout.fragment_login_tab) {

    private lateinit var binding: FragmentLoginTabBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginTabBinding.bind(view)

        // Login butonunun tıklama olayını tanımlıyoruz
        binding.loginButton.setOnClickListener {
            val enteredEmail = binding.username.text.toString().trim() // Kullanıcı adı (e-posta)
            val enteredPassword = binding.password.text.toString().trim() // Şifre

            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both email and password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(enteredEmail, enteredPassword)
            }
        }

        // Sign Up yönlendirme
        binding.signupRedirect.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignupTabFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loginUser(email: String, password: String) {
        Log.d("Login", "Attempting login with email: $email") // Hata ayıklama için
        // Supabase API üzerinden kullanıcı kontrolü
        SupabaseClient.supabaseService.getUser("eq.$email", "eq.$password").enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    if (!users.isNullOrEmpty()) {
                        val user = users[0]
                        Log.d("Login", "User retrieved: $user") // Hata ayıklama için
                        when (user.role) {
                            "admin" -> {
                                Toast.makeText(requireContext(), "Welcome Admin!", Toast.LENGTH_SHORT).show()
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, AdminFragment())
                                    .addToBackStack(null)
                                    .commit()
                            }
                            "user" -> {
                                Toast.makeText(requireContext(), "Login Successful!", Toast.LENGTH_SHORT).show()
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, UserFragment())
                                    .addToBackStack(null)
                                    .commit()
                            }
                            else -> {
                                Toast.makeText(requireContext(), "Unknown role!", Toast.LENGTH_SHORT).show()
                                Log.e("Login", "Unknown role: ${user.role}") // Hata ayıklama için
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_SHORT).show()
                        Log.d("Login", "No user found for email: $email")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                    Log.e("Login", "API error: $errorMessage") // Hata ayıklama için
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("Login", "Network error: ${t.message}") // Hata ayıklama için
            }
        })
    }
}
