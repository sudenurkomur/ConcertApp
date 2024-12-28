package com.example.concertapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.concertapp.api.SupabaseClient
import com.example.concertapp.models.Event
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventListActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var addEventButton: Button
    private var festivalName: String = "Festival Events"
    private var festivalId: Long = -1L

    private val editEventLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadEvents(festivalId) // Event düzenlendikten sonra listeyi güncelle
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        festivalId = intent.getLongExtra("festivalId", -1L)
        festivalName = intent.getStringExtra("festivalName") ?: "Festival Events"

        if (festivalId == -1L) {
            Toast.makeText(this, "Invalid Festival ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        supportActionBar?.title = festivalName

        tabLayout = findViewById(R.id.eventTabLayout)
        viewPager = findViewById(R.id.eventViewPager)
        addEventButton = findViewById(R.id.addEventButton)

        addEventButton.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            intent.putExtra("festivalId", festivalId)
            startActivity(intent)
        }

        loadEvents(festivalId)
    }

    private fun loadEvents(festivalId: Long) {
        SupabaseClient.supabaseService.getEventsForFestival("festival_id=eq.$festivalId")
            .enqueue(object : Callback<List<Event>> {
                override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                    if (response.isSuccessful) {
                        val events = response.body() ?: emptyList()
                        if (events.isNotEmpty()) {
                            setupViewPager(events)
                        } else {
                            Toast.makeText(this@EventListActivity, "No events found.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@EventListActivity, "Failed to load events.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                    Toast.makeText(this@EventListActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupViewPager(events: List<Event>) {
        val adapter = EventPagerAdapter(this, events) { event ->
            val intent = Intent(this, EditEventActivity::class.java)
            intent.putExtra("eventId", event.id)
            intent.putExtra("festivalId", festivalId)
            editEventLauncher.launch(intent) // Event düzenlemek için başlat
        }
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "Event ${position + 1}"
        }.attach()
    }
}