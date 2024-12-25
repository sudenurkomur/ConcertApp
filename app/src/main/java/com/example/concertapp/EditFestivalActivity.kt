package com.example.concertapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class EditFestivalActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editStage: EditText
    private lateinit var editSinger: EditText
    private lateinit var editTime: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_festival)

        // EditText ve Button referanslarını buluyoruz
        editTitle = findViewById(R.id.editTitle)
        editStage = findViewById(R.id.editStage)
        editSinger = findViewById(R.id.editSinger)
        editTime = findViewById(R.id.editTime)
        saveButton = findViewById(R.id.saveButton)

        // Intent'ten gelen verileri alıyoruz
        val key = intent.getStringExtra("key")
        val title = intent.getStringExtra("dataTitle")
        val stage = intent.getStringExtra("dataStage") // Sahne adı
        val singer = intent.getStringExtra("dataSinger") // Şarkıcı adı
        val time = intent.getStringExtra("dataTime")

        // Gelen verileri EditText alanlarına yerleştiriyoruz
        editTitle.setText(title)
        editStage.setText(stage)
        editSinger.setText(singer)
        editTime.setText(time)

        // Kaydet butonuna tıklama olayını tanımlıyoruz
        saveButton.setOnClickListener {
            val updatedTitle = editTitle.text.toString()
            val updatedStage = editStage.text.toString()
            val updatedSinger = editSinger.text.toString()
            val updatedTime = editTime.text.toString()

            if (key != null) {
                // Firebase Realtime Database güncellemesi
                val databaseReference = FirebaseDatabase.getInstance().getReference("Festival Data").child(key)
                val updatedFestival = mapOf(
                    "dataTitle" to updatedTitle,
                    "dataStage" to updatedStage,
                    "dataSinger" to updatedSinger,
                    "dataTime" to updatedTime
                )

                databaseReference.updateChildren(updatedFestival).addOnSuccessListener {
                    Toast.makeText(this, "Festival updated successfully!", Toast.LENGTH_SHORT).show()
                    finish() // Düzenleme ekranını kapatır ve geri döner
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to update festival: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Invalid festival key!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
