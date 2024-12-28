package com.example.concertapp

import UpdatedFestival
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.concertapp.api.SupabaseClient
import com.example.concertapp.models.Festival
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditFestivalActivity : AppCompatActivity() {

    private lateinit var festivalName: EditText
    private lateinit var festivalDesc: EditText
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var saveButton: Button

    private var festivalId: Long = -1L // Varsayılan geçersiz ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_festival)

        // Bileşenleri bağla
        festivalName = findViewById(R.id.festivalName)
        festivalDesc = findViewById(R.id.festivalDesc)
        startDate = findViewById(R.id.startDate)
        endDate = findViewById(R.id.endDate)
        saveButton = findViewById(R.id.saveButton)

        // Festival ID'yi al
        festivalId = intent.getLongExtra("festivalId", -1L)
        if (festivalId == -1L) {
            Toast.makeText(this, "Invalid Festival ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Festival bilgilerini yükle
        loadFestivalDetails()

        // Kaydet düğmesine tıklama olayı
        saveButton.setOnClickListener {
            val name = festivalName.text.toString().trim()
            val desc = festivalDesc.text.toString().trim()
            val start = startDate.text.toString().trim()
            val end = endDate.text.toString().trim()

            if (name.isEmpty() || desc.isEmpty() || start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Güncelleme işlemini başlat
            updateFestival(festivalId.toString(), name, desc, start, end)
        }
    }

    private fun loadFestivalDetails() {
        SupabaseClient.supabaseService.getFestivalById("eq.$festivalId")
            .enqueue(object : Callback<List<Festival>> {
                override fun onResponse(call: Call<List<Festival>>, response: Response<List<Festival>>) {
                    if (response.isSuccessful) {
                        val festival = response.body()?.firstOrNull()
                        if (festival != null) {
                            // Alanlara mevcut bilgileri doldur
                            festivalName.setText(festival.name)
                            festivalDesc.setText(festival.desc)
                            startDate.setText(festival.start_date)
                            endDate.setText(festival.end_date)
                        } else {
                            Toast.makeText(this@EditFestivalActivity, "Festival not found.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        logError("Failed to load festival details", response.errorBody()?.string())
                        Toast.makeText(this@EditFestivalActivity, "Error loading festival details.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<List<Festival>>, t: Throwable) {
                    logError("Error loading festival details", t.message)
                    Toast.makeText(this@EditFestivalActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
    }

    private fun updateFestival(id: String, name: String, desc: String, startDate: String, endDate: String) {
        val updatedFestival = UpdatedFestival(
            name = name,
            desc = desc,
            start_date = startDate,
            end_date = endDate
        )

        // id'yi eq. ile biçimlendirin
        val formattedId = "eq.$id"

        SupabaseClient.supabaseService.updateFestival(formattedId, updatedFestival)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditFestivalActivity, "Festival updated successfully.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val error = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("EditFestivalActivity", "Error updating festival: $error")
                        Toast.makeText(this@EditFestivalActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("EditFestivalActivity", "Failed to update festival: ${t.message}")
                    Toast.makeText(this@EditFestivalActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun logError(tag: String, message: String?) {
        Log.e(tag, message ?: "Unknown error")
    }
}
