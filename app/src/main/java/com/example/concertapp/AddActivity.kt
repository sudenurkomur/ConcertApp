package com.example.concertapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navdrawerkotpractice.AdminFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class AddActivity : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var festivalList: MutableList<DataClass>
    private lateinit var festivalAdapter: FestivalAdapter
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("MissingInflatedId")
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
            intent.putExtra("key", selectedFestival.key) // Festival key burada gönderiliyor
            intent.putExtra("dataTitle", selectedFestival.dataTitle)
            intent.putExtra("dataStage", selectedFestival.dataStage)
            intent.putExtra("dataSinger", selectedFestival.dataSinger)
            intent.putExtra("dataTime", selectedFestival.dataTime)

            // Log ile gönderilen key'i kontrol edin
            Log.d("AddActivity", "Gönderilen key: ${selectedFestival.key}")

            startActivity(intent)
        }
        recyclerView.adapter = festivalAdapter

        // Firebase veritabanı referansını başlatıyoruz
        val festivalRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Festival Data")

        festivalRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                festivalList.clear() // Listeyi temizle

                for (festivalSnapshot in snapshot.children) {
                    val festival = festivalSnapshot.getValue(DataClass::class.java)
                    festival?.let {
                        it.key = festivalSnapshot.key // Firebase'deki benzersiz key'i ekle
                        festivalList.add(it)
                    }
                }

                // Adapteri güncelle
                festivalAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching data: ${error.message}")
            }
        })

        // FAB butonuna tıklama olayını ekliyoruz
        fab = findViewById(R.id.fab)
        backButton = findViewById(R.id.back)

        fab.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            val fragment = AdminFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}