package com.example.concertapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.concertapp.api.SupabaseClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditFestivalActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editStage: EditText
    private lateinit var editSinger: EditText
    private lateinit var editTime: EditText
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_festival)

        // EditText ve Button referanslarını buluyoruz
        editTitle = findViewById(R.id.editTitle)
        editStage = findViewById(R.id.editStage)
        editSinger = findViewById(R.id.editSinger)
        editTime = findViewById(R.id.editTime)
        saveButton = findViewById(R.id.saveButton)
        deleteButton = findViewById(R.id.deleteButton)

        // Intent'ten gelen verileri alıyoruz
        val id = intent.getStringExtra("key") // Supabase'deki benzersiz ID
        val title = intent.getStringExtra("dataTitle")
        val stage = intent.getStringExtra("dataStage")
        val singer = intent.getStringExtra("dataSinger")
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

            if (id != null) {
                updateFestival(id, updatedTitle, updatedStage, updatedSinger, updatedTime)
            } else {
                Toast.makeText(this, "Invalid festival ID!", Toast.LENGTH_SHORT).show()
            }
        }

        // Silme butonuna tıklama olayını tanımlıyoruz
        deleteButton.setOnClickListener {
            if (id != null) {
                showDeleteConfirmationDialog(id)
            } else {
                Toast.makeText(this, "Festival ID not found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Festival güncelleme işlemi
    private fun updateFestival(id: String, title: String, stage: String, singer: String, time: String) {
        val updatedFestival = mapOf(
            "title" to title,
            "stage" to stage,
            "singer" to singer,
            "date" to time
        )

        SupabaseClient.supabaseService.updateFestival(id, updatedFestival)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditFestivalActivity, "Festival updated successfully!", Toast.LENGTH_SHORT).show()
                        finish() // Düzenleme ekranını kapat
                    } else {
                        Toast.makeText(this@EditFestivalActivity, "Failed to update festival!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@EditFestivalActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Silme işlemi için onaylama diyaloğu
    private fun showDeleteConfirmationDialog(id: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Festival")
            .setMessage("Are you sure you want to delete this festival?")
            .setPositiveButton("Yes") { _, _ ->
                deleteFestival(id)
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Festival silme işlemi
    private fun deleteFestival(id: String) {
        SupabaseClient.supabaseService.deleteFestival(id)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditFestivalActivity, "Festival deleted successfully!", Toast.LENGTH_SHORT).show()
                        finish() // Aktiviteden çık
                    } else {
                        Toast.makeText(this@EditFestivalActivity, "Failed to delete festival!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@EditFestivalActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
