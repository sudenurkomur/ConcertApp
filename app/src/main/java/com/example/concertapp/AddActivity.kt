package com.example.concertapp
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddActivity : AppCompatActivity(R.layout.fragment_add) {

    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FAB butonunu buluyoruz
        fab = findViewById(R.id.fab)

        // FAB butonuna tıklama olayını ekliyoruz
        fab.setOnClickListener {
            // UploadActivity'yi başlatıyoruz
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
    }
}
