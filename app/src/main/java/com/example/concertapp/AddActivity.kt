package com.example.concertapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.concertapp.api.SupabaseClient
import com.example.concertapp.models.DataClass
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddActivity : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var festivalList: MutableList<DataClass>
    private lateinit var festivalAdapter: FestivalAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_add)

        // RecyclerView'i başlatıyoruz
        recyclerView = findViewById(R.id.festivalRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // FestivalAdapter'i başlatıyoruz
        festivalList = mutableListOf()
        festivalAdapter = FestivalAdapter(festivalList) { selectedFestival ->
            val intent = Intent(this, EditFestivalActivity::class.java)
            intent.putExtra("key", selectedFestival.id) // Supabase'deki benzersiz id
            intent.putExtra("dataTitle", selectedFestival.title)
            intent.putExtra("dataStage", selectedFestival.stage)
            intent.putExtra("dataSinger", selectedFestival.singer)
            intent.putExtra("dataTime", selectedFestival.date)

            // Log ile gönderilen id'yi kontrol edin
            Log.d("AddActivity", "Gönderilen id: ${selectedFestival.id}")

            startActivity(intent)
        }
        recyclerView.adapter = festivalAdapter

        // Supabase'den verileri çekiyoruz
        fetchFestivals()

        // FAB butonuna tıklama olayını ekliyoruz
        fab = findViewById(R.id.fab)
        backButton = findViewById(R.id.back)

        fab.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish() // Aktiviteyi kapatarak geri dön
        }
    }

    private fun fetchFestivals() {
        SupabaseClient.supabaseService.getFestivals().enqueue(object : Callback<List<DataClass>> {
            override fun onResponse(call: Call<List<DataClass>>, response: Response<List<DataClass>>) {
                if (response.isSuccessful) {
                    festivalList.clear()
                    response.body()?.let {
                        festivalList.addAll(it)
                    }
                    festivalAdapter.notifyDataSetChanged()
                    Log.d("AddActivity", "Festivals fetched successfully: $festivalList")
                } else {
                    Log.e("AddActivity", "Error fetching festivals: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<DataClass>>, t: Throwable) {
                Log.e("AddActivity", "Failed to fetch festivals: ${t.message}")
            }
        })
    }
}