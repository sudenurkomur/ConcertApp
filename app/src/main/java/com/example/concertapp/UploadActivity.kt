package com.example.concertapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.concertapp.api.SupabaseClient
import com.example.concertapp.models.Event
import com.example.concertapp.models.Location
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class UploadActivity : AppCompatActivity() {

    private lateinit var saveButton: Button
    private lateinit var selectImageButton: Button
    private lateinit var uploadTitle: EditText
    private lateinit var uploadStage: EditText
    private lateinit var uploadSinger: EditText
    private lateinit var uploadDate: EditText
    private lateinit var uploadLatitude: EditText
    private lateinit var uploadLongitude: EditText
    private lateinit var selectedImageView: ImageView

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        // Bileşenleri bağlama
        uploadTitle = findViewById(R.id.uploadTitle)
        uploadStage = findViewById(R.id.uploadStage)
        uploadSinger = findViewById(R.id.uploadSinger)
        uploadDate = findViewById(R.id.uploadDate)
        uploadLatitude = findViewById(R.id.uploadLatitude)
        uploadLongitude = findViewById(R.id.uploadLongitude)
        saveButton = findViewById(R.id.saveButton)
        selectImageButton = findViewById(R.id.selectImageButton)
        selectedImageView = findViewById(R.id.selectedImageView)

        // Tarih ve saat seçimi
        uploadDate.setOnClickListener { showDateTimePicker() }

        // Görsel seçme işlemini başlat
        selectImageButton.setOnClickListener { pickImageFromGallery() }

        // Kaydetme işlemini başlat
        saveButton.setOnClickListener { saveData() }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()

        // Tarih seçimi
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)

            // Saat seçimi
            TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val selectedDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(calendar.time)
                uploadDate.setText(selectedDateTime)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startForResult.launch(intent)
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedImageUri = result.data?.data
                selectedImageView.setImageURI(selectedImageUri)
            }
        }

    private fun saveData() {
        val title = uploadTitle.text.toString().trim()
        val stage = uploadStage.text.toString().trim()
        val singer = uploadSinger.text.toString().trim()
        val date = uploadDate.text.toString().trim()
        val latitude = uploadLatitude.text.toString().trim().toDoubleOrNull()
        val longitude = uploadLongitude.text.toString().trim().toDoubleOrNull()

        if (title.isNotEmpty() && stage.isNotEmpty() && singer.isNotEmpty() && date.isNotEmpty() && latitude != null && longitude != null) {
            val location = Location(latitude = latitude, longitude = longitude)

            if (selectedImageUri != null) {
                uploadImageAndSaveEvent(title, stage, singer, date, location)
            } else {
                saveEvent(null, title, stage, singer, date, location)
            }
        } else {
            Toast.makeText(this, "Please fill all fields with valid data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageAndSaveEvent(title: String, stage: String, singer: String, date: String, location: Location) {
        val tempFile = File(cacheDir, "upload_image.jpg")
        val inputStream = contentResolver.openInputStream(selectedImageUri!!)
        FileOutputStream(tempFile).use { output -> inputStream?.copyTo(output) }

        val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

        val fileName = "event-images/${System.currentTimeMillis()}.jpg"
        SupabaseClient.supabaseService.uploadImage("event-images", fileName, body)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        SupabaseClient.supabaseService.getPublicUrl("event-images", fileName)
                            .enqueue(object : Callback<String> {
                                override fun onResponse(call: Call<String>, response: Response<String>) {
                                    if (response.isSuccessful) {
                                        val publicUrl = response.body()
                                        saveEvent(publicUrl, title, stage, singer, date, location)
                                    } else {
                                        logError("Failed to get public URL", response.errorBody()?.string())
                                    }
                                }

                                override fun onFailure(call: Call<String>, t: Throwable) {
                                    logError("Error fetching public URL", t.message)
                                }
                            })
                    } else {
                        logError("Image upload failed", response.errorBody()?.string())
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    logError("Error uploading image", t.message)
                }
            })
    }

    private fun saveEvent(imageUrl: String?, title: String, stage: String, singer: String, date: String, location: Location) {
        val festivalId = intent.getLongExtra("festivalId", -1L)
        if (festivalId == -1L) {
            Toast.makeText(this, "Invalid festival ID", Toast.LENGTH_SHORT).show()
            return
        }

        val event = Event(
            id = null,
            title = title,
            stage = stage,
            singer = singer,
            date = date,
            imageUrl = imageUrl,
            location = location,
            festival_id = festivalId
        )

        SupabaseClient.supabaseService.addEvent(event)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UploadActivity, "Event saved successfully.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        logError("Failed to save event", response.errorBody()?.string())
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    logError("Error saving event", t.message)
                }
            })
    }

    private fun logError(message: String, error: String?) {
        Log.e("UploadActivity", "$message: $error")
        Toast.makeText(this, "$message: $error", Toast.LENGTH_LONG).show()
    }
}
