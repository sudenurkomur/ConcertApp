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

class FragmentEvent : Fragment() {

    private var event: Event? = null
    private var onEventClickListener: ((Event) -> Unit)? = null

    // Tıklama olayını dışarıdan almak için setter
    fun setOnEventClickListener(listener: (Event) -> Unit) {
        onEventClickListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            event = it.getParcelable("event")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment layout'unu inflate et
        val view = inflater.inflate(R.layout.fragment_event, container, false)

        // Verileri bağlama
        event?.let {
            view.findViewById<TextView>(R.id.eventTitle).text = it.title
            view.findViewById<TextView>(R.id.eventStage).text = "Stage: ${it.stage}"
            view.findViewById<TextView>(R.id.eventSinger).text = "Singer: ${it.singer}"
            view.findViewById<TextView>(R.id.eventDate).text = "Date: ${it.date}"
            view.findViewById<TextView>(R.id.eventLocation).text =
                "Location: ${it.location?.latitude}, ${it.location?.longitude}"

            val imageView = view.findViewById<ImageView>(R.id.eventImage)
            Glide.with(this)
                .load(it.imageUrl)
                .placeholder(R.drawable.sample_image) // Görsel yüklenirken geçici görsel
                .into(imageView)
        }

        // Fragment tıklama olayı
        view.setOnClickListener {
            event?.let { onEventClickListener?.invoke(it) }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(event: Event) =
            FragmentEvent().apply {
                arguments = Bundle().apply {
                    putParcelable("event", event)
                }
            }
    }
}