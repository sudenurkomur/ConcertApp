package com.example.concertapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.concertapp.models.Event

class EventPagerAdapter(
    activity: FragmentActivity,
    private val events: List<Event>,
    private val onEventClick: (Event) -> Unit
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = events.size

    override fun createFragment(position: Int): Fragment {
        val fragment = FragmentEvent.newInstance(events[position])
        fragment.setOnEventClickListener(onEventClick)
        return fragment
    }
}