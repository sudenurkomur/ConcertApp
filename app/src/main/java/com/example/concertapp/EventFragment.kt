package com.example.concertapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.concertapp.models.Event

class EventFragment : Fragment() {

    companion object {
        private const val ARG_EVENT = "event"

        fun newInstance(event: Event): EventFragment {
            val fragment = EventFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_EVENT, event)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        event = arguments?.getParcelable(ARG_EVENT) ?: throw IllegalArgumentException("Event is missing")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event, container, false)

        val eventTitle = view.findViewById<TextView>(R.id.eventTitle)
        val eventStage = view.findViewById<TextView>(R.id.eventStage)
        val eventSinger = view.findViewById<TextView>(R.id.eventSinger)
        val eventDate = view.findViewById<TextView>(R.id.eventDate)
        val eventLocation = view.findViewById<TextView>(R.id.eventLocation)
        val eventImage = view.findViewById<ImageView>(R.id.eventImage)

        eventTitle.text = event.title
        eventStage.text = event.stage
        eventSinger.text = event.singer
        eventDate.text = event.date
        eventLocation.text = "Lat: ${event.location?.latitude}, Lng: ${event.location?.longitude}"

        Glide.with(this)
            .load(event.imageUrl)
            .placeholder(R.drawable.sample_image)
            .into(eventImage)

        return view
    }
}