package com.example.concertapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.concertapp.models.Event

class EventAdapter(
    private val eventList: List<Event>,
    private val onEditClick: ((Event) -> Unit)? = null, // Düzenleme işlemi
    private val onDeleteClick: ((Event) -> Unit)? = null // Silme işlemi
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    // ViewHolder sınıfı
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
        val eventStage: TextView = itemView.findViewById(R.id.eventStage)
        val eventSinger: TextView = itemView.findViewById(R.id.eventSinger)
        val eventDate: TextView = itemView.findViewById(R.id.eventDate)
        val eventLocation: TextView = itemView.findViewById(R.id.eventLocation)
        val eventImage: ImageView = itemView.findViewById(R.id.eventImage)
        val editButton: Button = itemView.findViewById(R.id.editEventButton) // Düzenleme butonu
        val deleteButton: Button = itemView.findViewById(R.id.deleteEventButton) // Silme butonu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]

        // Verileri bağlama
        holder.eventTitle.text = event.title
        holder.eventStage.text = event.stage
        holder.eventSinger.text = event.singer
        holder.eventDate.text = event.date
        holder.eventLocation.text = "Lat: ${event.location?.latitude}, Lng: ${event.location?.longitude}"

        // Glide kullanarak görseli yükleme
        Glide.with(holder.itemView.context)
            .load(event.imageUrl) // Supabase'den gelen görsel URL
            .placeholder(R.drawable.sample_image) // Görsel yüklenirken geçici resim
            .into(holder.eventImage)

        // Düzenleme butonuna tıklama
        holder.editButton.setOnClickListener {
            onEditClick?.invoke(event)
        }

        // Silme butonuna tıklama
        holder.deleteButton.setOnClickListener {
            onDeleteClick?.invoke(event)
        }
    }

    override fun getItemCount(): Int = eventList.size
}