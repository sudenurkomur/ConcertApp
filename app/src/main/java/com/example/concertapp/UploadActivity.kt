package com.example.concertapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.util.Calendar

class UploadActivity : AppCompatActivity() {
    private lateinit var saveButton: Button
    private lateinit var uploadTopic: EditText
    private lateinit var uploadDesc: EditText
    private lateinit var uploadLang: EditText
    private lateinit var uploadDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        uploadDesc = findViewById(R.id.uploadDesc)
        uploadTopic = findViewById(R.id.uploadTopic)
        uploadLang = findViewById(R.id.uploadLang)
        uploadDate = findViewById(R.id.uploadDate)
        saveButton = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val title = uploadTopic.text.toString()
        val desc = uploadDesc.text.toString()
        val lang = uploadLang.text.toString()
        val date = uploadDate.text.toString()

        // Check if the fields are not empty
        if (title.isNotEmpty() && desc.isNotEmpty() && lang.isNotEmpty() && date.isNotEmpty()) {
            val currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

            // Create a data class instance to hold the concert details
            val dataClass = DataClass(title, desc, lang, date, null)

            // Save the data to Firebase Realtime Database
            FirebaseDatabase.getInstance().getReference("Festival Data").child(currentDate)
                .setValue(dataClass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Show a success message
                        Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show()
                        finish() // Finish the activity and return to the previous screen
                    }
                }
                .addOnFailureListener { e ->
                    // Show an error message if saving failed
                    Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
        } else {
            // Show an error message if fields are empty
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }
}
