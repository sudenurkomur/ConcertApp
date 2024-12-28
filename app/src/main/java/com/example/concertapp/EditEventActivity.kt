package com.example.concertapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.concertapp.api.SupabaseClient
import com.example.concertapp.models.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditEventActivity : AppCompatActivity() {

    private lateinit var eventTitle: EditText
    private lateinit var eventStage: EditText
    private lateinit var eventSinger: EditText
    private lateinit var eventDate: EditText
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    private var eventId: Long = -1L
    private var festivalId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        eventTitle = findViewById(R.id.eventTitle)
        eventStage = findViewById(R.id.eventStage)
        eventSinger = findViewById(R.id.eventSinger)
        eventDate = findViewById(R.id.eventDate)
        saveButton = findViewById(R.id.saveButton)
        deleteButton = findViewById(R.id.deleteButton)

        eventId = intent.getLongExtra("eventId", -1L)
        festivalId = intent.getLongExtra("festivalId", -1L)

        if (eventId == -1L || festivalId == -1L) {
            Toast.makeText(this, "Invalid Event or Festival ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadEventDetails(eventId)

        eventDate.setOnClickListener { showDateTimePicker(eventDate) }

        saveButton.setOnClickListener { updateEvent(eventId) }
        deleteButton.setOnClickListener { deleteEvent(eventId) }
    }

    private fun showDateTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)

            TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val selectedDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(calendar.time)
                editText.setText(selectedDateTime)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun loadEventDetails(eventId: Long) {
        SupabaseClient.supabaseService.getEvents("id=eq.$eventId").enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    response.body()?.firstOrNull()?.let { event ->
                        eventTitle.setText(event.title)
                        eventStage.setText(event.stage)
                        eventSinger.setText(event.singer)
                        eventDate.setText(event.date)
                    } ?: run {
                        Toast.makeText(this@EditEventActivity, "Event not found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this@EditEventActivity, "Failed to load event", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Toast.makeText(this@EditEventActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateEvent(eventId: Long) {
        val updatedEvent = mapOf(
            "title" to eventTitle.text.toString(),
            "stage" to eventStage.text.toString(),
            "singer" to eventSinger.text.toString(),
            "date" to eventDate.text.toString()
        )

        SupabaseClient.supabaseService.updateEvent("id=eq.$eventId", updatedEvent).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditEventActivity, "Event updated successfully!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK) // EventListActivity'yi güncellemek için geri bildirim gönder
                    finish()
                } else {
                    val error = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@EditEventActivity, "Failed to update event: $error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@EditEventActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteEvent(eventId: Long) {
        SupabaseClient.supabaseService.deleteEvent("id=eq.$eventId").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditEventActivity, "Event deleted successfully!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK) // EventListActivity'yi güncellemek için geri bildirim gönder
                    finish()
                } else {
                    val error = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@EditEventActivity, "Failed to delete event: $error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@EditEventActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}