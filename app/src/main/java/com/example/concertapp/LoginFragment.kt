package com.example.concertapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.concertapp.databinding.FragmentLoginTabBinding
import com.example.navdrawerkotpractice.AdminFragment
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login_tab) {

    private lateinit var binding: FragmentLoginTabBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginTabBinding.bind(view)

        // FirebaseAuth instance'ını başlatıyoruz
        auth = FirebaseAuth.getInstance()

        // Login butonunun tıklama olayını tanımlıyoruz
        binding.loginButton.setOnClickListener {
            val enteredEmail = binding.username.text.toString().trim() // Kullanıcı adı (e-posta)
            val enteredPassword = binding.password.text.toString().trim() // Şifre

            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both email and password", Toast.LENGTH_SHORT).show()
            } else {
                // Test amaçlı direkt UserFragment'a yönlendirme

                /*
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, UserFragment())
                    .addToBackStack(null)
                    .commit()
                */


                // Firebase Authentication işlemini yoruma aldık
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
        // Firebase Authentication ile giriş işlemi
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Giriş başarılı
                    if (email == "admin@gmail.com" && password == "concertappAdmin") {
                        Toast.makeText(requireContext(), "Welcome Admin!", Toast.LENGTH_SHORT).show()
                        // AdminFragment'a yönlendir
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, AdminFragment())
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Toast.makeText(requireContext(), "Login Successful!", Toast.LENGTH_SHORT).show()
                        /* Diğer kullanıcılar için farklı bir işlem yapılabilir
-----> Buradan devam edilecek, kullanici islemleri */
                    }
                } else {
                    // Giriş başarısız
                    Toast.makeText(requireContext(), "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
