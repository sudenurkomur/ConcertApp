package com.example.concertapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.concertapp.api.SupabaseClient
import com.example.concertapp.models.DataClass
import com.example.concertapp.models.Location
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadActivity : AppCompatActivity() {

    private lateinit var saveButton: Button
    private lateinit var uploadTitle: EditText
    private lateinit var uploadStage: EditText
    private lateinit var uploadSinger: EditText
    private lateinit var uploadDate: EditText
    private lateinit var uploadLatitude: EditText
    private lateinit var uploadLongitude: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        // EditText ve Button bileşenlerini tanımlayın
        uploadTitle = findViewById(R.id.uploadTitle)
        uploadStage = findViewById(R.id.uploadStage)
        uploadSinger = findViewById(R.id.uploadSinger)
        uploadDate = findViewById(R.id.uploadDate)
        uploadLatitude = findViewById(R.id.uploadLatitude)
        uploadLongitude = findViewById(R.id.uploadLongitude)
        saveButton = findViewById(R.id.saveButton)

        // Kaydetme işlemini başlatmak için tıklama olayı
        saveButton.setOnClickListener {
            saveData()
        }
    }

    // Güncellenmiş saveData fonksiyonu
    private fun saveData() {
        val title = uploadTitle.text.toString().trim()
        val stage = uploadStage.text.toString().trim()
        val singer = uploadSinger.text.toString().trim()
        val date = uploadDate.text.toString().trim()
        val latitude = uploadLatitude.text.toString().trim().toDoubleOrNull()
        val longitude = uploadLongitude.text.toString().trim().toDoubleOrNull()

        if (title.isNotEmpty() && stage.isNotEmpty() && singer.isNotEmpty() && date.isNotEmpty() && latitude != null && longitude != null) {
            val location = Location(latitude = latitude, longitude = longitude)

            val event = DataClass(
                id = "",
                title = title,
                stage = stage,
                singer = singer,
                date = date,
                image = null,
                location = location
            )

            SupabaseClient.supabaseService.addEvent(event)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@UploadActivity, "Saved Successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@UploadActivity, "Failed: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@UploadActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(this, "Please fill all fields with valid data", Toast.LENGTH_SHORT).show()
        }
    }
}
