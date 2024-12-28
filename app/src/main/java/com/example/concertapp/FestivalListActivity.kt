package com.example.concertapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.concertapp.api.SupabaseClient
import com.example.concertapp.models.Festival
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FestivalListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addFestivalFab: FloatingActionButton
    private lateinit var festivalAdapter: FestivalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_festival_list)

        // RecyclerView ve FloatingActionButton'ı bağlama
        recyclerView = findViewById(R.id.festivalRecyclerView)
        addFestivalFab = findViewById(R.id.addFestivalFab)

        recyclerView.layoutManager = LinearLayoutManager(this)

        festivalAdapter = FestivalAdapter(emptyList()) { festival ->
            // Festival seçildiğinde etkinlik listesine yönlendir
            val intent = Intent(this, EventListActivity::class.java)
            intent.putExtra("festivalId", festival.id)
            intent.putExtra("festivalName", festival.name)
            startActivity(intent)
        }
        recyclerView.adapter = festivalAdapter

        // FloatingActionButton'a tıklama işlemi
        addFestivalFab.setOnClickListener {
            val intent = Intent(this, AddFestivalActivity::class.java)
            startActivity(intent) // Yeni festival ekleme ekranına yönlendir
        }

        loadFestivals() // Başlangıçta festivalleri yükler
    }

    override fun onResume() {
        super.onResume()
        loadFestivals() // Aktiviteye geri dönüldüğünde festivalleri tekrar yükler
    }

    private fun loadFestivals() {
        SupabaseClient.supabaseService.getFestivalsWithEvents()
            .enqueue(object : Callback<List<Festival>> {
                override fun onResponse(call: Call<List<Festival>>, response: Response<List<Festival>>) {
                    if (response.isSuccessful) {
                        val festivals = response.body() ?: emptyList()
                        festivalAdapter.updateList(festivals) // Listeyi günceller
                    } else {
                        val error = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("FestivalListActivity", "Failed to load festivals: $error")
                        Toast.makeText(this@FestivalListActivity, "Failed to load festivals.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Festival>>, t: Throwable) {
                    Log.e("FestivalListActivity", "Error loading festivals: ${t.message}")
                    Toast.makeText(this@FestivalListActivity, "Error loading festivals.", Toast.LENGTH_SHORT).show()
                }
            })
    }
}