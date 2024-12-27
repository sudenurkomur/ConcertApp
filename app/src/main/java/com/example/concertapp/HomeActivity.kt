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
import com.example.concertapp.api.SupabaseClient
import com.example.concertapp.models.DataClass
import com.example.navdrawerkotpractice.AdminFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)

        // TextView bileşenini buluyoruz
        val dataNumbers = findViewById<TextView>(R.id.emptyBox)

        // Button bileşenini buluyoruz
        val backButton = findViewById<Button>(R.id.backButton)

        // Supabase'den kullanıcı bilgisi çekme
        fetchUserCount(dataNumbers)

        // backButton'a tıklama olayını ayarlıyoruz
        backButton.setOnClickListener {
            // AdminFragment'e geçiş yapıyoruz
            navigateToFragment(AdminFragment())
        }
    }

    // Kullanıcı sayısını çekme işlemi
    private fun fetchUserCount(dataNumbers: TextView) {
        SupabaseClient.supabaseService.getFestivals()
            .enqueue(object : Callback<List<DataClass>> {
                override fun onResponse(
                    call: Call<List<DataClass>>,
                    response: Response<List<DataClass>>
                ) {
                    if (response.isSuccessful) {
                        val events = response.body()
                        val userCount = events?.size ?: 0
                        dataNumbers.text = userCount.toString()
                    } else {
                        Log.e("SupabaseError", "Failed to fetch users: ${response.errorBody()?.string()}")
                        dataNumbers.text = "Error"
                    }
                }

                override fun onFailure(call: Call<List<DataClass>>, t: Throwable) {
                    Log.e("SupabaseError", "Failed to fetch users: ${t.message}")
                    dataNumbers.text = "Error"
                }
            })
    }

    // Fragment'e geçiş yapan yardımcı bir fonksiyon
    private fun navigateToFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
