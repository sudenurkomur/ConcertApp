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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        uploadDesc = findViewById(R.id.uploadDesc)
        uploadTopic = findViewById(R.id.uploadTopic)
        uploadLang = findViewById(R.id.uploadLang)
        saveButton = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val title = uploadTopic.text.toString()
        val desc = uploadDesc.text.toString()
        val lang = uploadLang.text.toString()

        if (title.isNotEmpty() && desc.isNotEmpty() && lang.isNotEmpty()) {
            val currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
            val dataClass = DataClass(title, desc, lang, null)

            FirebaseDatabase.getInstance().getReference("Android Tutorials").child(currentDate)
                .setValue(dataClass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }
}
