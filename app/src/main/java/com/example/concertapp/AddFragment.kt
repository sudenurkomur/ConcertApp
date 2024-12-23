package com.example.concertapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddFragment : Fragment(R.layout.fragment_add) {

    private lateinit var fab: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FAB butonunu buluyoruz
        fab = view.findViewById(R.id.fab)

        // FAB butonuna tıklama olayını ekliyoruz
        fab.setOnClickListener {
            // UploadActivity'yi başlatıyoruz
            val intent = Intent(requireContext(), UploadActivity::class.java)
            startActivity(intent)
        }
    }
}
