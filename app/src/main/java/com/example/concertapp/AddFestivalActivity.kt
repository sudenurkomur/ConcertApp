package com.example.concertapp

import android.app.DatePickerDialog
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
import java.util.Calendar

class AddFestivalActivity : AppCompatActivity() {

    private lateinit var festivalName: EditText
    private lateinit var festivalDesc: EditText
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_festival)

        // Bileşenleri tanımlayın
        festivalName = findViewById(R.id.festivalName)
        festivalDesc = findViewById(R.id.festivalDesc)
        startDate = findViewById(R.id.startDate)
        endDate = findViewById(R.id.endDate)
        saveButton = findViewById(R.id.saveButton)

        // Başlangıç tarihi için DatePicker
        startDate.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                startDate.setText(selectedDate)
            }
        }

        // Bitiş tarihi için DatePicker
        endDate.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                endDate.setText(selectedDate)
            }
        }

        // Kaydetme işlemini başlatmak için
        saveButton.setOnClickListener {
            saveFestival()
        }
    }

    /**
     * Tarih seçmek için DatePickerDialog açılır.
     */
    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    "%04d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1, // Aylar 0'dan başlar
                    selectedDay
                )
                onDateSelected(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    /**
     * Festival bilgilerini Supabase'e kaydetmek için kullanılan fonksiyon.
     */
    private fun saveFestival() {
        val name = festivalName.text.toString()
        val desc = festivalDesc.text.toString()
        val start = startDate.text.toString()
        val end = endDate.text.toString()

        if (name.isNotEmpty() && desc.isNotEmpty() && start.isNotEmpty() && end.isNotEmpty()) {
            val festival = Festival(
                id = 0, // Supabase auto-increments ID
                name = name,
                desc = desc,
                start_date = start,
                end_date = end
            )

            SupabaseClient.supabaseService.addFestival(festival).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddFestivalActivity, "Festival added successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        Toast.makeText(this@AddFestivalActivity, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                        Log.e("AddFestivalActivity", "Failed to add festival: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AddFestivalActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AddFestivalActivity", "Network error occurred: ${t.message}")
                }
            })
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            Log.w("AddFestivalActivity", "Missing required fields")
        }
    }
}