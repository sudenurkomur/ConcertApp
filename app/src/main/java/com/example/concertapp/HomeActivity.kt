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
import com.example.concertapp.models.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home) // Doğru layout dosyasını bağlayın

        // TextView bileşenini buluyoruz
        val dataNumbers = findViewById<TextView>(R.id.eventCountTextView) // Doğru ID ile eşleşmeli

        // Button bileşenini buluyoruz
        val backButton = findViewById<Button>(R.id.backButton)

        // Etkinlik sayısını Supabase'den çekiyoruz
        fetchEventCount(dataNumbers)

        // Geri butonuna tıklama olayını ayarlıyoruz
        backButton.setOnClickListener {
            // AdminFragment'e geçiş yapıyoruz
            navigateToFragment(AdminFragment())
        }
    }

    // Etkinlik sayısını çekme işlemi
    private fun fetchEventCount(dataNumbers: TextView) {
        SupabaseClient.supabaseService.getEvents() // `getEvents` metodu SupabaseService içinde tanımlı olmalı
            .enqueue(object : Callback<List<Event>> {
                override fun onResponse(
                    call: Call<List<Event>>,
                    response: Response<List<Event>>
                ) {
                    if (response.isSuccessful) {
                        val events = response.body()
                        val eventCount = events?.size ?: 0
                        dataNumbers.text = eventCount.toString()
                    } else {
                        Log.e("SupabaseError", "Failed to fetch events: ${response.errorBody()?.string()}")
                        dataNumbers.text = "Error"
                    }
                }

                override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                    Log.e("SupabaseError", "Failed to fetch events: ${t.message}")
                    dataNumbers.text = "Error"
                }
            })
    }

    // Fragment'e geçiş yapan yardımcı bir fonksiyon
    private fun navigateToFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragment_container, fragment) // `fragment_container` doğru tanımlanmalı
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}