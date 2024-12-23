package com.example.concertapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.concertapp.R
import com.example.navdrawerkotpractice.AdminFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Layout'u activity için set ediyoruz
        setContentView(R.layout.fragment_home)

        // TextView bileşenini buluyoruz
        val dataNumbers = findViewById<TextView>(R.id.emptyBox)

        // Button bileşenini buluyoruz
        val backButton = findViewById<Button>(R.id.backButton)

        // Firebase Authentication ile giriş yapan kullanıcıyı alıyoruz
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Kullanıcı giriş yaptıysa işlem yapıyoruz
        if (currentUser != null) {
            // Firebase Realtime Database referansı
            val databaseReference = FirebaseDatabase.getInstance().getReference("members")

            // Firebase'den veri alıyoruz
            databaseReference.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Üye sayısını hesaplıyoruz
                    val memberCount = snapshot.childrenCount
                    // TextView'e üye sayısını yazıyoruz
                    dataNumbers.text = memberCount.toString()
                } else {
                    dataNumbers.text = "0"
                }
            }.addOnFailureListener {
                Log.e("FirebaseError", "Veri alınamadı: ${it.message}")
                dataNumbers.text = "Error"
            }
        } else {
            // Kullanıcı giriş yapmadıysa, hata mesajı gösteriyoruz
            Log.e("FirebaseAuth", "Kullanıcı giriş yapmamış")
            dataNumbers.text = "User not logged in"
        }

        // backButton'a tıklama olayını ayarlıyoruz
        backButton.setOnClickListener {
            // AdminFragment'e geçiş yapıyoruz
            navigateToFragment(AdminFragment())
        }
    }

    // Fragment'e geçiş yapan yardımcı bir fonksiyon
    private fun navigateToFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragment_container, fragment) // fragment_container id'sini düzenleyin
        fragmentTransaction.addToBackStack(null) // Geri tuşuna basıldığında önceki fragmente dönmek için
        fragmentTransaction.commit()
    }
}
