package com.example.concertapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.navdrawerkotpractice.AdminFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class AddActivity : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    private lateinit var backButton: FloatingActionButton

    // Firebase database reference
    private lateinit var database: DatabaseReference

    // TextView'ler
    private lateinit var dataTitle: TextView
    private lateinit var dataDesc: TextView
    private lateinit var dataTime: TextView
    private lateinit var dataLang: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_add)

        // Firebase bağlantısı
        database = FirebaseDatabase.getInstance().getReference("Festival Data")

        // TextView'lere bağlama
        dataTitle = findViewById(R.id.dataTitle)
        dataDesc = findViewById(R.id.dataDesc)
        dataTime = findViewById(R.id.dataTime)
        dataLang = findViewById(R.id.dataLang)

        // FAB butonuna tıklama olayını ekliyoruz
        fab = findViewById(R.id.fab)
        backButton = findViewById(R.id.back)

        fab.setOnClickListener {
            // UploadActivity'yi başlatıyoruz
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            // AdminFragment'i mevcut Activity içerisine ekliyoruz
            val fragment = AdminFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment) // fragment_container, layout dosyasındaki bir FrameLayout id'si
            transaction.addToBackStack(null) // Geri tuşuyla geri dönmeyi sağlar
            transaction.commit()
        }

        // Firebase'den veri çekme işlemi
        database.child("Festival Data").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Verileri çekiyoruz
                val title = snapshot.child("dataTitle").value.toString()
                val desc = snapshot.child("dataDesc").value.toString()
                val time = snapshot.child("dataTime").value.toString()
                val lang = snapshot.child("dataLang").value.toString()

                // TextView'lere verileri atıyoruz
                dataTitle.text = title
                dataDesc.text = desc
                dataTime.text = time
                dataLang.text = lang
            }

            override fun onCancelled(error: DatabaseError) {
                // Hata durumunda log atabilirsiniz
            }
        })
    }
}
